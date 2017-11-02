package com.feng.sauron.warning.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.feng.sauron.warning.web.vo.MetricsOriDataVO;
import com.fengjr.cachecloud.client.IRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by lianbin.wang on 2016/11/23.
 */
@Service("CachedScatterService")
public class CachedScatterService implements ScatterService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("MetricsDataService")
    private ScatterService realScatterService;

    @Autowired
    private IRedis redisCluster;



    private static Executor executor = Executors.newCachedThreadPool();


    @Override
    public List<MetricsOriDataVO> loadScatterData(Date startTime, Date endTime, String appName, String host) {
        List<MetricsOriDataVO> result = new ArrayList<>();

        List<TimeSpan> timeSpanList = splitTime(startTime, endTime);

        CompletionService<List<MetricsOriDataVO>> completionService = new ExecutorCompletionService(executor);
        for (TimeSpan timeSpan : timeSpanList) {
            completionService.submit(new FetchTask(timeSpan.getStart(), timeSpan.getEnd(), appName, host));
        }

        for (int i = 0; i < timeSpanList.size(); i++) {
            try {
                Future<List<MetricsOriDataVO>> future = completionService.poll(15, TimeUnit.SECONDS);
                if (future != null) {
                    List<MetricsOriDataVO> list = future.get();
                    if (list != null) {
                        result.addAll(list);
                    }
                }
            } catch (InterruptedException e) {
                logger.error("loadScatterData interrupted, msg:{}", e.getMessage(), e);
            } catch (ExecutionException e) {
                logger.error("loadScatterData error, msg:{}", e.getMessage(), e);
            }
        }

        return result;
    }

    private List<TimeSpan> splitTime(Date startTime, Date endTime) {
        long start = startTime.getTime();
        long end = endTime.getTime();

        if (end - start <= TEN_MIN) {
            return Arrays.asList(new TimeSpan(start, end));
        }

        start = cleanUp(start);
        end = cleanUp(end);

        List<TimeSpan> list = new ArrayList<>();

        long low = start;
        while (low < end) {
            list.add(new TimeSpan(low, low = (low + TEN_MIN)));
        }

        return list;
    }

    //毫秒精确到（10）分钟
    private static long TEN_MIN = 10 * 60 * 1000;

    private long cleanUp(long milli) {
        return milli / TEN_MIN * TEN_MIN;
    }

    class FetchTask implements Callable<List<MetricsOriDataVO>> {
        private long start;
        private long end;
        private String appName;
        private String host;

        static final int SECONDS_OF_DAY = 60 * 60 * 24;

        public FetchTask(long start, long end, String appName, String host) {
            this.start = start;
            this.end = end;
            this.appName = appName;
            this.host = host;
        }

        @Override
        public List<MetricsOriDataVO> call() throws Exception {
            //check cache
            List<MetricsOriDataVO> cached = getFromCache();
            if (cached != null) {
                if (CachedScatterService.this.logger.isDebugEnabled()) {
                    CachedScatterService.this.logger.debug("loading from cache, key:{}", formKey());
                }
                return cached;
            }
            //do query
            List<MetricsOriDataVO> realTime = getFromHbase();
            updateCache(realTime);
            return realTime;
        }

        private void updateCache(List<MetricsOriDataVO> list) {
            String key = formKey();
            redisCluster.set(key, JSON.toJSONString(list), SECONDS_OF_DAY);
//            RedisUtils.getRedis().expire(key, SECONDS_OF_DAY);
        }

        public List<MetricsOriDataVO> getFromHbase() {
            List<MetricsOriDataVO> list = CachedScatterService.this.realScatterService.loadScatterData(new Date(start), new Date(end), appName, host);
            if (list != null) {
                return list;
            }
            return Collections.emptyList();
        }

        public List<MetricsOriDataVO> getFromCache() {
            try {
                String cachedValue = redisCluster.get(formKey());
                if (cachedValue != null && !cachedValue.isEmpty()) {
                    return JSON.parseObject(cachedValue, new TypeReference<List<MetricsOriDataVO>>() {
                    });
                }
            } catch (Exception e) {
                CachedScatterService.this.logger.error("loadScatterData from redis error, msg:{}", e.getMessage(), e);
            }
            return null;
        }

        private String formKey() {
            return "sauron-warning:scatter:" + appName + ":" + start + "_" + end;
        }
    }


    class TimeSpan {
        private long start;
        private long end;

        public TimeSpan(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }
    }


}
