package com.feng.sauron.warning.task;

import com.feng.sauron.warning.service.HttpMonitorService;
import com.feng.sauron.warning.util.ConfigChangeWatcher;
import com.feng.sauron.warning.util.DateUtils;
import com.feng.sauron.warning.util.WatchableConfigClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xubiao.fan on 2016/5/12.
 */
public class RunHttpMonitorTask {


    private static  int cronMinute = 0;
    /**
     * 最大时间
     */
    private final int maxIntervalTime = 10;//单位为分钟
    private int singleThreadNum = 50;
    private static Map<Long,Integer> urlIdTimes;
    private ReentrantLock readLock = new ReentrantLock();
    private static final Logger logger = LoggerFactory.getLogger(RunHttpMonitorTask.class);
    //private static volatile boolean needRefreshData = false;
    private static volatile AtomicInteger versionData = new AtomicInteger(0);
    private final static String nodeName = "sauron_warning_sync_urlmonitor";
    private final static String key = "sauron.warning.urlmonitor";
    private static String format = DateUtils.format(new Date()) + "." + new Random().nextInt(1000);

    @Resource
    LeaderSelectionClient leaderSelectionClient;
    @Resource
    private HttpMonitorService httpMonitorService;

    static {
        try {
            defaultWatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void run(){
        logger.info("enter runHttpMonitor");
//        if (!leaderSelectionClient.hasLeaderShip()) {
//            logger.info("当前主机为非主节点 定时任务不开启");
//            return;
//        } else {
//            logger.info("定时任务开启...");
//        }
        if(versionData.get() > 0 || urlIdTimes == null  )
            init();
        runTask();
    }

    public  void init(){
        readLock.lock();
        if(versionData.get() == 0 && urlIdTimes!=null)
            return;
        try{

            urlIdTimes = httpMonitorService.getAUrlRulesIds();
            logger.info("loading data. size:"+urlIdTimes.size() );
        }finally {
            if(versionData.get() >0)
                versionData.getAndSet(0);
            readLock.unlock();
        }
    }
    public static void clearMap(){
        //urlIdTimes =null;
        //needRefreshData = true;
        versionData.addAndGet(1);
        logger.info("versionData change");
        //watch();
    }

    public static void defaultWatch() {

        WatchableConfigClient.getInstance().create(nodeName, key, "default");
        WatchableConfigClient.getInstance().getAndWatch(nodeName,key, "default", new ConfigChangeWatcher() {

            @Override
            public void onValueChanged(String newVal) {
                logger.info("检测到其他server 更新了数 url monitor 数据 , 开始刷新缓存");
                if (!format.equalsIgnoreCase(newVal)) {
                    clearMap();
                } else {
                    logger.info("此次更新 url monitor 来自 服务本身 ,已经刷新过,忽略...");
                }
            }
        });
    }

    public static void watch() {

        try {
            String format_now = DateUtils.format(new Date()) + "." + new Random().nextInt(1000);
            WatchableConfigClient.getInstance().set(nodeName, key, format_now);
            clearMap();
            format = format_now;
        } catch (Exception e) {
        }
    }


    private void runTask(){
        logger.debug("monitor url start runTask");
        cronMinute = (cronMinute+1)% maxIntervalTime;

        List<Long> runUrlIds = new ArrayList<Long>();
        for(Map.Entry<Long,Integer> e : urlIdTimes.entrySet()){
            int val = e.getValue();
            if(cronMinute % val == 0)
                runUrlIds.add(e.getKey());
        }
        // start run thread
        final int urlsSize = runUrlIds.size();
        final List<Long> fnRunUrlIds = runUrlIds;
        if(urlsSize >0 ){
            logger.info("monitor urls. size:{}",urlsSize );
            long start = System.currentTimeMillis();
            int threadNum = (urlsSize / singleThreadNum) + 1;
            ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
            final CountDownLatch countDownLatch = new CountDownLatch(urlsSize);
            for(int m = 0 ; m < threadNum ;m++){
                final int taskId = m ;
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            int start = taskId* singleThreadNum;
                            int end = urlsSize > start + singleThreadNum ? start + singleThreadNum : urlsSize;
                            for(int mid = start ;mid < end ;mid++ ){
                                httpMonitorService.runUrlMonitorById(fnRunUrlIds.get(mid));
                            }
                        }finally {
                            countDownLatch.countDown();
                        }


                    }
                });

            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("monitor urls run total time : {}ms",(System.currentTimeMillis()-start));
            threadPool.shutdown();// running over,shut down.
//            for( int id : runUrlIds)
//                httpMonitorService.runUrlMonitorById(id);
        }
    }


}
