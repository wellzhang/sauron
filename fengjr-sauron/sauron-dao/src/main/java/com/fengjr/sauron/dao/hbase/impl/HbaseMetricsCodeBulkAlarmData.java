package com.fengjr.sauron.dao.hbase.impl;

import com.fengjr.sauron.commons.utils.SauronConstants;
import com.fengjr.sauron.dao.MetricsCodeBulkAlarmDataDao;
import com.fengjr.sauron.dao.MetricsCodeBulkDataDao;
import com.fengjr.sauron.dao.hbase.HbaseOperations2;
import com.fengjr.sauron.dao.hbase.HbaseTables;
import com.fengjr.sauron.dao.hbase.buffer.BytesUtils;
import com.fengjr.sauron.dao.hbase.vo.Range;
import com.fengjr.sauron.dao.model.MetricsCodeBulkAlarmData;
import com.fengjr.sauron.dao.model.MetricsCodeBulkData;
import com.fengjr.sauron.dao.model.MetricsOriData;
import com.fengjr.sauron.dao.model.TransactionId;
import com.fengjr.sauron.dao.util.TransactionIdUtils;
import com.sematext.hbase.wd.AbstractRowKeyDistributor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/1.
 */
@Repository
public class HbaseMetricsCodeBulkAlarmData implements MetricsCodeBulkAlarmDataDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HbaseOperations2 hbaseTemplate;

    @Autowired
    @Qualifier("metricsCodeBulkAlarmMapper")
    private RowMapper<List<MetricsCodeBulkAlarmData>> colMapper;

    @Autowired
    @Qualifier("traceDistributor")
    private AbstractRowKeyDistributor rowKeyDistributor;

    private int scanCacheSize = 256;
    private final  int LIMIT_RESULT = 100;

    @Override
    public void insert(MetricsCodeBulkAlarmData metricsCodeBulkAlarmData) {
        if (metricsCodeBulkAlarmData == null) {
            throw new NullPointerException("metricsOriData must not be null");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("insert event. {}", metricsCodeBulkAlarmData.toString());
        }

        final String agentId = metricsCodeBulkAlarmData.getTraceId();
        final long eventTimestamp = metricsCodeBulkAlarmData.getLogTime();

        byte[] rowKey = getDistributeRowKey(createRowKey(metricsCodeBulkAlarmData.getAppName(),metricsCodeBulkAlarmData.getTraceId()).getBytes());

       // final AgentEventType eventType = metricsOriData.getType();
        //byte[] qualifier = Bytes.toBytes(metricsOriData.getSpanId());
        this.hbaseTemplate.put(HbaseTables.METRICS_CODE_BULK_ALARM, rowKey, HbaseTables.METRICS_CODE_BULK_ALARM_CF, HbaseTables.METRICS_CODE_BULK_ALARM_CF_QUALIFIER,metricsCodeBulkAlarmData.writeValue() );
    }

    @Override
    public void insertBatch(List<MetricsCodeBulkAlarmData> metricsCodeBulkAlarmDatas) {

        if(metricsCodeBulkAlarmDatas == null || metricsCodeBulkAlarmDatas.size()<1)
            return ;
        List<Put> puts = new LinkedList<>();
        for(MetricsCodeBulkAlarmData metricsCodeBulkAlarmData:metricsCodeBulkAlarmDatas){
            byte[] rowKey = getDistributeRowKey(createRowKey(metricsCodeBulkAlarmData.getAppName(),metricsCodeBulkAlarmData.getTraceId()).getBytes());
            byte[] qualifier = HbaseTables.METRICS_CODE_BULK_ALARM_CF_QUALIFIER;
            Put put = new Put(rowKey);
            put.addColumn(HbaseTables.METRICS_CODE_BULK_ALARM_CF, qualifier, metricsCodeBulkAlarmData.writeValue());
            puts.add(put);
        }
        hbaseTemplate.put(HbaseTables.METRICS_CODE_BULK_ALARM,puts);
    }

    private TransactionId createRowKey(String appName, String traceId){

        if(appName == null)
            appName="";
        if(appName.length()>SauronConstants.APPLICATION_NAME_MAX_LEN)
            appName = appName.substring(0,SauronConstants.APPLICATION_NAME_MAX_LEN-1);
        String transactionId = TransactionIdUtils.formatString(appName,traceId);
        return  TransactionIdUtils.parseTransactionId(transactionId);
    }


    @Override
    public  List<MetricsCodeBulkAlarmData> getMetricsCodeBulkDataAlarm(String appName, String traceId) {

        if(appName == null || appName.length() < 1 ){
            throw new NullPointerException("appName must not be null");
        }
        if(traceId == null || traceId.length() < 1 ){
            throw new NullPointerException("traceId must not be null");
        }
        byte[] rowKey = getDistributeRowKey(createRowKey(appName,traceId).getBytes());

        Get get = new Get(rowKey);
        get.addFamily(HbaseTables.METRICS_CODE_BULK_ALARM_CF);

        return hbaseTemplate.get(HbaseTables.METRICS_CODE_BULK_ALARM, get, colMapper);
    }

    private byte[] getDistributeRowKey(byte[] transactionId) {
        return rowKeyDistributor.getDistributedKey(transactionId);
    }

    public List<List<MetricsCodeBulkAlarmData>> getMetricsCodeBulkDataAlarmRange(String appName, Range range){
        if(appName == null || appName.length() < 1 ){
            throw new NullPointerException("appName must not be null");
        }
        if(range == null ){
            throw new NullPointerException("range must not be null");
        }
        Scan scan = createScan(appName, range);

        List<List<MetricsCodeBulkAlarmData>> traceIndexList = hbaseTemplate.find(HbaseTables.METRICS_CODE_BULK_ALARM,
                scan, rowKeyDistributor, LIMIT_RESULT, colMapper);

        return traceIndexList;
    }

    private Scan createScan(String appName, Range range) {
        Scan scan = new Scan();
        scan.setCaching(this.scanCacheSize);

        byte[] startKey =  getRowKey(appName, range.getFrom());
        byte[] endKey = getRowKey(appName, range.getTo());
        scan.setStartRow(startKey);
        scan.setStopRow(endKey);
        scan.addFamily(HbaseTables.METRICS_CODE_BULK_ALARM_CF);
        scan.setId("MetricsOri Scan");
        // toString() method of Scan converts a message to json format so it is slow for the first time.
        logger.debug("create scan:{}", scan);
        return scan;
    }

    private byte[] getRowKey(String appName, long timestamp) {
        if (appName == null) {
            throw new IllegalArgumentException("appName must not null");
        }
        if(appName.length()>SauronConstants.APPLICATION_NAME_MAX_LEN)
            appName = appName.substring(0,SauronConstants.APPLICATION_NAME_MAX_LEN-1);

        return BytesUtils.stringLongToBytes(appName, SauronConstants.APPLICATION_NAME_MAX_LEN, timestamp);
    }






}
