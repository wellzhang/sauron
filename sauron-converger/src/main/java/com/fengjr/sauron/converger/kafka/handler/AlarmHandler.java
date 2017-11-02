package com.fengjr.sauron.converger.kafka.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengjr.sauron.commons.utils.DateUtils;
import com.fengjr.sauron.commons.utils.SauronConstants;
import com.fengjr.sauron.converger.kafka.storage.HbaseManger;
import com.fengjr.sauron.dao.MetricsCodeBulkAlarmDataDao;
import com.fengjr.sauron.dao.hbase.HbaseTables;
import com.fengjr.sauron.dao.model.MetricsCodeBulkAlarmData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;



/**
 * Created by xubiao.fan@fengjr.com on 2016/11/8.
 */
@Component("alarmHandler")
public class AlarmHandler implements BaseHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    HbaseManger hbaseManger;

    @Override
    public void handle(String line, String hostName, String logTime,String version) {

        Map mapData = JSON.parseObject(line,Map.class);
        JSONObject jno = (JSONObject)mapData.get(SauronConstants.APP);
        if(jno ==null)
            jno = (JSONObject)mapData.get(SauronConstants.APP_U);
        MetricsCodeBulkAlarmData alarmData =  JSONObject.toJavaObject(jno, MetricsCodeBulkAlarmData.class);
        if(alarmData == null){
            logger.info("parse json error, data:{}",line);
            return;
        }
        alarmData.setHostName(hostName);
        alarmData.setVersion(version);
        //alarmData.setLogTime(Long.parseLong(logTime));
        try {
            Date string2Date = DateUtils.String2Date(logTime, DateUtils.DEFAULT_DATESTRING_PATTERN);
            alarmData.setLogTime(string2Date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveAlarm(alarmData);
    }

    private void saveAlarm(MetricsCodeBulkAlarmData alarmData) {

        //metricsCodeBulkAlarmDataDao.insert(alarmData);
        hbaseManger.getBatch(HbaseTables.METRICS_CODE_BULK_ALARM).addBatch(alarmData);

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
//            hashMap.put("Type", sauronMap.get("Type"));
//
//            mongoManger.getMongoBatch(COLLECTION_NAME_CODEBULK_ALARM).addBatch(hashMap);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.info("insert mongo error...");
//        }
    }


}
