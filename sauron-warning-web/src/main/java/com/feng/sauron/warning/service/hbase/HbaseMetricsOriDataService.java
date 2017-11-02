package com.feng.sauron.warning.service.hbase;

import com.feng.sauron.warning.util.MetricsOriDataUtils;
import com.feng.sauron.warning.web.vo.MetricsOriDataVO;
import com.fengjr.sauron.dao.hbase.impl.HbaseMetricsOriData;
import com.fengjr.sauron.dao.hbase.impl.HbaseTraceAppShip;
import com.fengjr.sauron.dao.hbase.vo.LimitedScanResult;
import com.fengjr.sauron.dao.hbase.vo.Range;
import com.fengjr.sauron.dao.model.MetricsOriData;
import com.fengjr.sauron.dao.model.TraceAppShip;
import com.fengjr.sauron.dao.util.TransactionIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/18.
 */
@Service
public class HbaseMetricsOriDataService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${monitor.scatter.max_point_size:#{20000}}")
    private int frontViewNum;

    private int MAX_SCAN = 10;

    @Resource
    HbaseMetricsOriData hbaseMetricsOriData;

    @Resource
    HbaseTraceAppShip hbaseTraceAppShip;

    public List<MetricsOriDataVO> getMetricsOriDataPoint(String appName, Range range) {
        logger.debug("frontViewNum:{}", frontViewNum);

        List<MetricsOriDataVO> metricsOriDataVOs = new LinkedList<>();
        LimitedScanResult scanResult = hbaseMetricsOriData.getMetricsOriDataList(appName, range);
        List<List<MetricsOriData>> traceIndexList = (List<List<MetricsOriData>>) scanResult.getScanData();
        addMetricsOriDataVO(traceIndexList, metricsOriDataVOs);

        int scanTimes = MAX_SCAN;
        while (scanTimes > 0 && traceIndexList != null && traceIndexList.size() > 0
                && traceIndexList.size() <= hbaseMetricsOriData.getLIMIT_RESULT() && metricsOriDataVOs.size() < frontViewNum) { //need redo scan
            range = new Range(scanResult.getLastStartTime(), range.getTo(), scanResult.getLastTranSeq());
            scanResult = hbaseMetricsOriData.getMetricsOriDataList(appName, range);
            traceIndexList = (List<List<MetricsOriData>>) scanResult.getScanData();
            //add vo
            addMetricsOriDataVO(traceIndexList, metricsOriDataVOs);
            scanTimes--;
        }
        return metricsOriDataVOs;
    }

    private void addMetricsOriDataVO(List<List<MetricsOriData>> traceIndexList, List<MetricsOriDataVO> metricsOriDataVOs) {
        for (List<MetricsOriData> metricsOriDatas : traceIndexList) {
            if (metricsOriDataVOs.size() >= frontViewNum)
                break;
            if (metricsOriDatas == null || metricsOriDatas.size() == 0)
                continue;
            MetricsOriDataUtils.sortBySpanId(metricsOriDatas);
            MetricsOriDataVO vo = convert(metricsOriDatas.get(0));
            metricsOriDataVOs.add(vo);
        }
    }

    private MetricsOriDataVO convert(MetricsOriData metricsOriData) {
        if (metricsOriData == null)
            return null;
        MetricsOriDataVO vo = new MetricsOriDataVO();
        String traceId = metricsOriData.getTraceId();
        if (!traceId.contains(TransactionIdUtils.TRANSACTION_ID_DELIMITER)) {
            logger.info("traceId is error,data:{}", metricsOriData);
            return null;
        } else {
            traceId = traceId.substring(0, traceId.indexOf(TransactionIdUtils.TRANSACTION_ID_DELIMITER));
        }
//        String[] traceIdSplit = traceId.split(TransactionIdUtils.TRANSACTION_ID_DELIMITER);
        vo.setTimeCost(metricsOriData.getDuration());
        vo.setTraceID(metricsOriData.getTraceId());
        vo.setTime(Long.parseLong(traceId));
        vo.setStatus("0".equals(metricsOriData.getResult()) ? "success" : "fail");
        vo.setAppType(metricsOriData.getType());

        return vo;
    }

    public List<MetricsOriData> getTraceTree(String appName, String traceId) {

        List<TraceAppShip> appShips = hbaseTraceAppShip.getTraceAppShip(traceId);
        List<MetricsOriData> metricsOriDatas = new LinkedList<>();
        for (TraceAppShip appShip : appShips) {
            List<MetricsOriData> tempData = hbaseMetricsOriData.getMetricsOriData(appShip.getAppName(), appShip.getTraceId());
            if (tempData != null && tempData.size() > 0)
                metricsOriDatas.addAll(tempData);
        }

        MetricsOriDataUtils.sortBySpanId(metricsOriDatas);

        return metricsOriDatas;
    }


    /**
     * 方法监控:mongo-->hbase
     *
     * @param appName
     * @param range
     * @return
     */
    public List<MetricsOriData> getMetricsForMethodMonitor(String appName, Range range) {
        logger.debug("frontViewNum:{}", frontViewNum);

        List<MetricsOriData> metricsOriDataVOs = new LinkedList<>();
        LimitedScanResult scanResult = hbaseMetricsOriData.getMetricsOriDataList(appName, range);
        List<List<MetricsOriData>> traceIndexList = (List<List<MetricsOriData>>) scanResult.getScanData();
        addMetricsOriDataForMonitor(traceIndexList, metricsOriDataVOs);

        int scanTimes = MAX_SCAN;
        while (scanTimes > 0 && traceIndexList != null && traceIndexList.size() > 0
                && traceIndexList.size() <= hbaseMetricsOriData.getLIMIT_RESULT() && metricsOriDataVOs.size() < frontViewNum) { //need redo scan
            range = new Range(scanResult.getLastStartTime(), range.getTo(), scanResult.getLastTranSeq());
            scanResult = hbaseMetricsOriData.getMetricsOriDataList(appName, range);
            traceIndexList = (List<List<MetricsOriData>>) scanResult.getScanData();
            //add vo
            addMetricsOriDataForMonitor(traceIndexList, metricsOriDataVOs);
            scanTimes--;
        }

        return metricsOriDataVOs;
    }

    public MetricsOriData getMetricsForMethodMonitor(String appName, String traceId) {
        List<MetricsOriData> list = hbaseMetricsOriData.getMetricsOriData(appName, traceId);
        if (list == null || list.isEmpty()) {
            return null;
        }
        MetricsOriDataUtils.sortBySpanId(list);
        return list.get(0);
    }


    private void addMetricsOriDataForMonitor(List<List<MetricsOriData>> traceIndexList, List<MetricsOriData> metricsOriDataVOs) {
        for (List<MetricsOriData> metricsOriDatas : traceIndexList) {
            if (metricsOriDataVOs.size() >= frontViewNum)
                break;
            if (metricsOriDatas == null || metricsOriDatas.size() == 0)
                continue;
            MetricsOriDataUtils.sortBySpanId(metricsOriDatas);
            metricsOriDataVOs.add(metricsOriDatas.get(0));
        }
    }

}
