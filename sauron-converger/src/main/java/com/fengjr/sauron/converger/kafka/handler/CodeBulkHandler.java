package com.fengjr.sauron.converger.kafka.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengjr.cachecloud.client.IRedis;
import com.fengjr.sauron.commons.constant.RedisConstant;
import com.fengjr.sauron.commons.utils.DateUtils;
import com.fengjr.sauron.commons.utils.SauronConstants;
import com.fengjr.sauron.converger.kafka.decoder.LogMsgDecoder;
import com.fengjr.sauron.converger.kafka.storage.HbaseManger;
import com.fengjr.sauron.dao.MetricsCodeBulkDataDao;
import com.fengjr.sauron.dao.hbase.HbaseTables;
import com.fengjr.sauron.dao.model.MetricsCodeBulkData;
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
@Component("codeBulkHandler")
public class CodeBulkHandler implements BaseHandler {

    public static ConcurrentHashSet<String> userCodeBulkCache = LogMsgDecoder.userCodeBulkCache;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HbaseManger hbaseManger;
    @Autowired
    private IRedis redisClient;

    @Override
    public void handle(String line, String hostName, String logTime,String version) {

        Map mapData = JSON.parseObject(line,Map.class);
        JSONObject jno = (JSONObject)mapData.get(SauronConstants.APP);
        if(jno ==null)
            jno = (JSONObject)mapData.get(SauronConstants.APP_U);
        MetricsCodeBulkData codeBulk =  JSONObject.toJavaObject(jno, MetricsCodeBulkData.class);
        if(codeBulk == null){
            logger.info("parse json error, data:{}",line);
            return;
        }

        try {
            Date string2Date = DateUtils.String2Date(logTime, DateUtils.DEFAULT_DATESTRING_PATTERN);
            codeBulk.setLogTime(string2Date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        codeBulk.setVersion(version);
        codeBulk.setHostName(hostName);

//        if(codeBulk.getTracer()!=null)
//            codeBulk.setDuration(codeBulk.getTracer().getTime());
        if(codeBulk.getaTracerAdapter()!=null)
            codeBulk.setDuration(codeBulk.getaTracerAdapter().getTime());

        regCodeBulkData(codeBulk);
        //metricsCodeBulkDataDao.insert(codeBulk);
        hbaseManger.getBatch(HbaseTables.METRICS_CODE_BULK_DATA).addBatch(codeBulk);
    }



    @SuppressWarnings("unchecked")
    private void regCodeBulkData(MetricsCodeBulkData codeBulk) {
        try {

            String methodName = codeBulk.getKey();  //String.valueOf(sauronMap.get("Key"));
            String appName = codeBulk.getAppName().toString().replace("-", "_");
            String hostName =codeBulk.getHostName();
            String traceid = codeBulk.getTraceId();

            Date string2Date = new Date(codeBulk.getLogTime());
            String logTimeMinute = DateUtils.Date2String(string2Date, DateUtils.MINUTE_DATESTRING_PATTERN);

            StringBuffer sb = new StringBuffer();
            sb.append(hostName).append("|").append(appName).append("|").append(methodName).append("|").append(logTimeMinute);
            String key = sb.toString();

            //Map<String, Object> Tracer = (Map<String, Object>) sauronMap.get("Tracer");
            //String duration = (String.valueOf(Tracer.get("duration"))).replace("ms", "");
            String duration = codeBulk.getDuration()+ "";
            if (StringUtils.isNotBlank(key) && !userCodeBulkCache.contains(key)) {
                userCodeBulkCache.add(key);
            }

            if (redisClient.hexists(key, RedisConstant.HASH_KEY_SAMPLING_COUNT)) {
                redisClient.hincrby(key, RedisConstant.HASH_KEY_SAMPLING_COUNT, 1);
                redisClient.hincrby(key, RedisConstant.HASH_KEY_SAMPLING_SUMVAULE, Long.valueOf(duration));
            } else {
                redisClient.hset(key, RedisConstant.HASH_KEY_SAMPLING_COUNT, "1", RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
                redisClient.hset(key, RedisConstant.HASH_KEY_SAMPLING_SUMVAULE, duration, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
            }
            redisClient.zadd(key + "|tp", Double.valueOf(duration), traceid + "#" + duration, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    @SuppressWarnings("unchecked")
//    private void saveCodeBulkMongo(Map<String, Object> logData) {
//
//        try {
//            HashMap<String, Object> hashMap = new HashMap<>();
//            Map<String, Object> sauronMap = (Map<String, Object>) logData.get("Sauron");
//
//            hashMap.put("logtime", logData.get("logtime"));
//            hashMap.put("hostName", logData.get("hostName"));
//            hashMap.put("AppName", sauronMap.get("AppName").toString().replace("-", "_"));
//            hashMap.put("methodName", sauronMap.get("MethodName"));
//            hashMap.put("Traceid", sauronMap.get("Traceid"));
//            hashMap.put("Key", sauronMap.get("Key"));
//            hashMap.put("LineNumber", sauronMap.get("LineNumber"));
//            hashMap.put("Result", sauronMap.get("Result"));
//            hashMap.put("IsSuccess", sauronMap.get("IsSuccess"));
//
//            Map<String, Object> Tracer = (Map<String, Object>) sauronMap.get("Tracer");
//            Object duration = (String.valueOf(Tracer.get("duration"))).replace("ms", "");
//            hashMap.put("duration", duration);
//
//            mongoManger.getMongoBatch(COLLECTION_NAME_CODEBULK).addBatch(hashMap);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.info("insert mongo error...");
//        }
//    }


    public static void main(String[] args){
        CodeBulkHandler handler = new CodeBulkHandler();
        String line = "{\"Sauron\":{\"AppName\":\"sauron_insurance_aztec\",\"Traceid\":\"1484706449282^0554265085431\",\"Key\":\"usercenter polymerization method\",\"MethodName\":\"com.fengjr.insurance.pc.controller.usercenter.PcUserCenterController.queryPolymerizationInfo\",\"ATracerAdapter\":{\"time\":\"3\"},\"LineNumber\":\"249\",\"Result\":\"pcusercenter queryPolymerizationInfo method is orer\",\"IsSuccess\":\"0\"}}\n";
        handler.handle(line,"127.0.0.1","2016 02-01","v1");
    }
}
