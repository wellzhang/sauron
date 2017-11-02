package com.feng.sauron.warning.service.hbase;

import com.fengjr.sauron.dao.hbase.impl.HbaseMetricsCodeBulkAlarmData;
import com.fengjr.sauron.dao.hbase.impl.HbaseMetricsCodeBulkData;
import com.fengjr.sauron.dao.hbase.vo.Range;
import com.fengjr.sauron.dao.model.MetricsCodeBulkAlarmData;
import com.fengjr.sauron.dao.model.MetricsCodeBulkData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/19.
 */
@Service
public class HbaseMetricsCodeAlarmService {

    @Resource
    HbaseMetricsCodeBulkAlarmData hbaseMetricsCodeBulkAlarmData;

    public MetricsCodeBulkAlarmData getMetricsCodeBulkAlarm(String appName, String traceId ){

        List<MetricsCodeBulkAlarmData> datas = hbaseMetricsCodeBulkAlarmData.getMetricsCodeBulkDataAlarm(appName,traceId);
        if(datas != null && datas.size() >0)
            return datas.get(0);
        else
            return null;
    }

    public List<MetricsCodeBulkAlarmData> getMetricsCodeBulkAlarmRange(String appName, Range range){

        List<List<MetricsCodeBulkAlarmData>> datas = hbaseMetricsCodeBulkAlarmData.getMetricsCodeBulkDataAlarmRange(appName,range);
        List<MetricsCodeBulkAlarmData> codeBulkAlarms = new LinkedList<>();
        for(List<MetricsCodeBulkAlarmData> data:datas){
            for (MetricsCodeBulkAlarmData da:data)
                codeBulkAlarms.add(da);
        }
        return codeBulkAlarms;
    }



}
