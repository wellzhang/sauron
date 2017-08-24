package com.feng.sauron.warning.service.hbase;

import com.fengjr.sauron.dao.hbase.impl.HbaseMetricsCodeBulkData;
import com.fengjr.sauron.dao.hbase.vo.Range;
import com.fengjr.sauron.dao.model.MetricsCodeBulkData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/19.
 */
@Service
public class HbaseMetricsCodeBulkService {

    @Resource
    HbaseMetricsCodeBulkData hbaseMetricsCodeBulkData;

    public MetricsCodeBulkData  getMetricsCodeBulkData(String appName, String traceId ){

        List<MetricsCodeBulkData> datas = hbaseMetricsCodeBulkData.getMetricsCodeBulkData(appName,traceId);
        if(datas != null && datas.size() >0)
            return datas.get(0);
        else
            return null;
    }

    public List<MetricsCodeBulkData> getMetricsCodeBulkDataRange(String appName, Range range){

        List<List<MetricsCodeBulkData>> datas = hbaseMetricsCodeBulkData.getMetricsCodeBulkDataRange(appName,range);
        List<MetricsCodeBulkData> codeBulkDatas = new LinkedList<>();
        for(List<MetricsCodeBulkData> data:datas){
            for (MetricsCodeBulkData da:data)
                codeBulkDatas.add(da);
        }
        return codeBulkDatas;
    }



}
