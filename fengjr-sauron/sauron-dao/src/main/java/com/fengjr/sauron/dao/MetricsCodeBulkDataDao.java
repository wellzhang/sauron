package com.fengjr.sauron.dao;

import com.fengjr.sauron.dao.hbase.vo.Range;
import com.fengjr.sauron.dao.model.MetricsCodeBulkData;

import java.util.List;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/1.
 */
public interface MetricsCodeBulkDataDao {

    void insert(MetricsCodeBulkData metricsCodeBulkData);

    void insertBatch(List<MetricsCodeBulkData> metricsCodeBulkDatas);

    List<MetricsCodeBulkData> getMetricsCodeBulkData(String appName, String traceId);

    List<List<MetricsCodeBulkData>> getMetricsCodeBulkDataRange(String appName, Range range);
}
