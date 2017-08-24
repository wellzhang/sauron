package com.fengjr.sauron.dao;

import com.fengjr.sauron.dao.hbase.vo.Range;
import com.fengjr.sauron.dao.model.MetricsCodeBulkAlarmData;

import java.util.List;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/1.
 */
public interface MetricsCodeBulkAlarmDataDao {

    void insert(MetricsCodeBulkAlarmData metricsCodeBulkAlarmData);

    void insertBatch(List<MetricsCodeBulkAlarmData> metricsCodeBulkAlarmDatas);

    List<MetricsCodeBulkAlarmData> getMetricsCodeBulkDataAlarm(String appName, String traceId);

    public List<List<MetricsCodeBulkAlarmData>> getMetricsCodeBulkDataAlarmRange(String appName, Range range);
}
