package com.fengjr.sauron.converger.kafka.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengjr.cachecloud.client.IRedis;
import com.fengjr.sauron.commons.constant.RedisConstant;
import com.fengjr.sauron.commons.utils.DateUtils;
import com.fengjr.sauron.commons.utils.SauronConstants;
import com.fengjr.sauron.converger.kafka.decoder.LogMsgDecoder;
import com.fengjr.sauron.converger.kafka.storage.HbaseManger;
import com.fengjr.sauron.converger.util.LocalCache;
import com.fengjr.sauron.dao.hbase.HbaseTables;
import com.fengjr.sauron.dao.model.MetricsOriData;
import com.fengjr.sauron.dao.model.TraceAppShip;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/8.
 */
@Component("appHandler")
public class AppHandler implements BaseHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static ConcurrentHashSet<String> appDataCache = LogMsgDecoder.appDataCache;

    private String ERROR = "1";

    @Autowired
    HbaseManger hbaseManger;
    @Autowired
    IRedis redisClient;

    @Override
    public void handle(String line, String hostName, String logTime,String version) {

        Map mapData = JSON.parseObject(line,Map.class);
        JSONObject jno = (JSONObject)mapData.get(SauronConstants.APP);
        MetricsOriData oriData = JSONObject.toJavaObject(jno, MetricsOriData.class);
        if(oriData == null || oriData.getTraceId().contains("-")){
            logger.debug("parse json error, data:{}",line);
            return;
        }

        oriData.setDuration(oriData.getTracer().getTime());
        if(oriData.getParams()!=null)
            oriData.setParamStr(oriData.getParams().toString());
        try {
            Date string2Date = DateUtils.String2Date(logTime, DateUtils.DEFAULT_DATESTRING_PATTERN);
            oriData.setLogTime(string2Date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        oriData.setVersion(version);
        oriData.setHostName(hostName);

        regAllData(oriData);
        //long dd = System.currentTimeMillis();
        //metricsOriDataDao.insert(oriData);
        hbaseManger.getBatch(HbaseTables.METRICS_ORI_DATA).addBatch(oriData);
        //save trace app ship
        saveTraceAppShip(oriData.getTraceId(),oriData.getAppName());
        //System.err.println(System.currentTimeMillis()-dd);
    }


    @SuppressWarnings("unchecked")
    private void regAllData(MetricsOriData oriData) {
        try {
            String methodName = oriData.getMethodName();
            Date string2Date = new Date(oriData.getLogTime());
            String logTimeMinute = DateUtils.Date2String(string2Date, DateUtils.MINUTE_DATESTRING_PATTERN);

            String hostName = oriData.getHostName();
            String appName = oriData.getAppName().toString().replace("-", "_");
            String traceid = oriData.getTraceId();
            StringBuffer sb = new StringBuffer();
            sb.append(hostName).append("|").append(appName).append("|").append(methodName).append("|").append(logTimeMinute)
                                .append("|").append(oriData.getSource())
                                .append("|").append(oriData.getType())
                                .append("|").append(oriData.getDetail());

            String key = sb.toString();
           // Map<String, Object> Tracer = (Map<String, Object>) sauronMap.get("Tracer");

            //String duration = (String.valueOf(Tracer.get("duration"))).replace("ms", "");
            String duration = oriData.getDuration()+"";

            if (StringUtils.isNotBlank(key) && !appDataCache.contains(key)) {
                appDataCache.add(key);
            }

//            if (CacheUtils.redisClient.hexists(key, RedisConstant.HASH_KEY_SAMPLING_COUNT)) {
//                CacheUtils.redisClient.hincrby(key, RedisConstant.HASH_KEY_SAMPLING_COUNT, 1);
//                CacheUtils.redisClient.hincrby(key, RedisConstant.HASH_KEY_SAMPLING_SUMVAULE, Long.valueOf(duration));
//            } else {
//                CacheUtils.redisClient.hset(key, RedisConstant.HASH_KEY_SAMPLING_COUNT, "1", RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
//                CacheUtils.redisClient.hset(key, RedisConstant.HASH_KEY_SAMPLING_SUMVAULE, duration, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
//            }
            //add error num
            if(ERROR.equals(oriData.getResult())){
                String keyError = key+ "|ERR";
                redisClient.incr(keyError);
                redisClient.expire(keyError, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
                
                String keyErrorTraceID = key+ "|ERR_TRACEID";
                redisClient.set(keyErrorTraceID, traceid, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
                
            }
            redisClient.zadd(key + "|tp", Double.valueOf(duration), traceid + "#" + duration, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MetricsOriData error:",e);
        }
    }

    private void saveTraceAppShip(String traceId,String appName){

        String compose = traceId + appName;
        if(!LocalCache.getKeyExist(compose))
        {

            //traceAppShipDao.insert(new TraceAppShip(traceId,appName));
            hbaseManger.getBatch(HbaseTables.TRACE_APP_SHIP).addBatch(new TraceAppShip(traceId,appName));
            LocalCache.setCacheKey(compose);
        }else{
            if(logger.isDebugEnabled()){
                logger.debug("skip save trace app ship, ship exist.");
            }
        }

    }

}
