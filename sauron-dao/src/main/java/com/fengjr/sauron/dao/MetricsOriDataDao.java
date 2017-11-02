package com.fengjr.sauron.dao;

import com.fengjr.sauron.dao.hbase.vo.LimitedScanResult;
import com.fengjr.sauron.dao.hbase.vo.Range;
import com.fengjr.sauron.dao.model.MetricsOriData;

import java.util.List;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/1.
 */
public interface MetricsOriDataDao {

    void insert(MetricsOriData metricsOriData);

    void insertBatch( List<MetricsOriData> metricsOriDatas);

    List<MetricsOriData> getMetricsOriData(String appName, String traceId);

    LimitedScanResult getMetricsOriDataList(String appName, Range range);
}
