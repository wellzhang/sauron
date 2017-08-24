package com.feng.sauron.warning.service;

import com.alibaba.fastjson.JSON;
import com.feng.sauron.warning.service.hbase.HbaseMetricsOriDataService;
import com.feng.sauron.warning.util.MetricsOriDataUtils;
import com.feng.sauron.warning.web.vo.MetricsOriDataVO;
import com.feng.sauron.warning.web.vo.TraceItem;
import com.fengjr.sauron.dao.hbase.vo.Range;
import com.fengjr.sauron.dao.model.MetricsOriData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by lianbin.wang on 2016/11/17.
 */
@Service("MetricsDataService")
public class MetricsDataService implements ScatterService, TraceService, CallstackService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HbaseMetricsOriDataService hbaseMetricsOriDataService;

    @Override
    public List<CallstackItem> loadCallStack(String traceId, String appName) {
        List<MetricsOriData> list = hbaseMetricsOriDataService.getTraceTree(appName, traceId);

        List<CallstackItem> result = new ArrayList<>();

        for (MetricsOriData metricsOriData : list) {
            CallstackItem item = MetricsOriDataUtils.toCallstack(metricsOriData);
            result.add(item);
        }

        return result;
    }

    @Override
    public List<MetricsOriDataVO> loadScatterData(Date startTime, Date endTime, String appName, String host) {
        Range range = new Range(startTime.getTime(), endTime.getTime());
        long st = System.currentTimeMillis();
        logger.info("Enter ScatterData,-- app:{},start:{},end:{}",appName,startTime,endTime);
        List<MetricsOriDataVO> list = hbaseMetricsOriDataService.getMetricsOriDataPoint(appName, range);
        logger.info("ScatterData,app:{},range:{},result size:{},use time:{}ms",appName,range,list==null?0:list.size(),(System.currentTimeMillis()-st));
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list;
    }

    @Override
    public List<TraceItem> loadTrace(String[] appName, String[] traceID) {
        if (appName.length != traceID.length) {
            throw new IllegalArgumentException("size of appName does not equal to size of traceID");
        }

        List<TraceItem> traceList = new ArrayList<>();
        for (int i = 0; i < appName.length; i++) {
            List<MetricsOriData> list = hbaseMetricsOriDataService.getTraceTree(appName[i], traceID[i]);

            if (list != null && !list.isEmpty()) {
                TraceItem item = new TraceItem();
                BeanUtils.copyProperties(list.get(0), item);
                traceList.add(item);
            }
        }

        return traceList;
    }
}
