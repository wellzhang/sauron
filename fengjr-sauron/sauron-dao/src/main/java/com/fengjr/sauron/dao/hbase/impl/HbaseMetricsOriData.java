package com.fengjr.sauron.dao.hbase.impl;

import com.fengjr.sauron.commons.utils.SauronConstants;
import com.fengjr.sauron.dao.MetricsOriDataDao;
import com.fengjr.sauron.dao.hbase.HbaseOperations2;
import com.fengjr.sauron.dao.hbase.HbaseTables;
import com.fengjr.sauron.dao.hbase.LimitEventHandler;
import com.fengjr.sauron.dao.hbase.buffer.BytesUtils;
import com.fengjr.sauron.dao.hbase.vo.LimitedScanResult;
import com.fengjr.sauron.dao.hbase.vo.Range;
import com.fengjr.sauron.dao.model.MetricsOriData;
import com.fengjr.sauron.dao.model.TransactionId;
import com.fengjr.sauron.dao.util.TransactionIdUtils;
import com.sematext.hbase.wd.AbstractRowKeyDistributor;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.*;
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
public class HbaseMetricsOriData implements MetricsOriDataDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HbaseOperations2 hbaseTemplate;

    @Autowired
    @Qualifier("metricsOriDataMapper")
    private RowMapper<List<MetricsOriData>> colMapper;

    @Autowired
    @Qualifier("traceDistributor")
    private AbstractRowKeyDistributor rowKeyDistributor;

    private int scanCacheSize = 256;
    private final int LIMIT_RESULT = 10000;
    private static final int APPLICATION_TRACE_INDEX_NUM_PARTITIONS = 32;

    @Override
    public void insert(MetricsOriData metricsOriData) {

        if (metricsOriData == null) {
            throw new NullPointerException("metricsOriData must not be null");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("insert  {}", metricsOriData.toString());
        }

        byte[] rowKey = getDistributeRowKey(createRowKey(metricsOriData.getAppName(), metricsOriData.getTraceId()).getBytes());
        byte[] qualifier = Bytes.toBytes(metricsOriData.getSpanId());
        this.hbaseTemplate.put(HbaseTables.METRICS_ORI_DATA, rowKey, HbaseTables.METRICS_ORI_DATA_CF_TRACE, qualifier, metricsOriData.writeValue());
    }

    @Override
    public void insertBatch(List<MetricsOriData> metricsOriDatas) {
        if (metricsOriDatas == null || metricsOriDatas.size() < 1)
            return;
        List<Put> puts = new LinkedList<>();
        for (MetricsOriData metricsOriData : metricsOriDatas) {
            byte[] rowKey = getDistributeRowKey(createRowKey(metricsOriData.getAppName(), metricsOriData.getTraceId()).getBytes());
            byte[] qualifier = Bytes.toBytes(metricsOriData.getSpanId());
            Put put = new Put(rowKey);
            put.addColumn(HbaseTables.METRICS_ORI_DATA_CF_TRACE, qualifier, metricsOriData.writeValue());
            puts.add(put);
        }
        hbaseTemplate.put(HbaseTables.METRICS_ORI_DATA, puts);
    }

    private TransactionId createRowKey(String appName, String traceId) {

        if (appName == null)
            appName = "";
        if (appName.length() > SauronConstants.APPLICATION_NAME_MAX_LEN)
            appName = appName.substring(0, SauronConstants.APPLICATION_NAME_MAX_LEN - 1);
        String transactionId = TransactionIdUtils.formatString(appName, traceId);
        return TransactionIdUtils.parseTransactionId(transactionId);
    }


    @Override
    public List<MetricsOriData> getMetricsOriData(String appName, String traceId) {

        if (appName == null || appName.length() < 1) {
            throw new NullPointerException("appName must not be null");
        }
        if (traceId == null || traceId.length() < 1) {
            throw new NullPointerException("traceId must not be null");
        }
        byte[] rowKey = getDistributeRowKey(createRowKey(appName, traceId).getBytes());
        Get get = new Get(rowKey);
        get.addFamily(HbaseTables.METRICS_ORI_DATA_CF_TRACE);
        return hbaseTemplate.get(HbaseTables.METRICS_ORI_DATA, get, colMapper);
    }

    private byte[] getDistributeRowKey(byte[] transactionId) {
        return rowKeyDistributor.getDistributedKey(transactionId);
    }


    @Override
    public LimitedScanResult getMetricsOriDataList(String appName, Range range) {

        if (appName == null || appName.length() < 1) {
            throw new NullPointerException("appName must not be null");
        }
        if (range == null) {
            throw new NullPointerException("range must not be null");
        }
        LimitedScanResult result = new LimitedScanResult();
        Scan scan = createScan(appName, range);
        LastRowAccessor lastRowAccessor = new LastRowAccessor();
        List<List<MetricsOriData>> traceIndexList = hbaseTemplate.findParallel(HbaseTables.METRICS_ORI_DATA,
                scan, rowKeyDistributor, LIMIT_RESULT, colMapper, lastRowAccessor, APPLICATION_TRACE_INDEX_NUM_PARTITIONS);

        result.setScanData(traceIndexList);
        if (lastRowAccessor != null && lastRowAccessor.getLastTransactionId() != null) {
            result.setLastStartTime(lastRowAccessor.getLastTransactionId().getStartTime());
            result.setLastTranSeq(lastRowAccessor.getLastTransactionId().getTransactionSequence());
        }

        return result;
    }

    private class LastRowAccessor implements LimitEventHandler {
        private Long lastRowTimestamp = -1L;
        private TransactionId lastTransactionId = null;
        //private int lastTransactionElapsed = -1;

        @Override
        public void handleLastResult(Result lastResult) {
            if (lastResult == null) {
                return;
            }

            Cell[] rawCells = lastResult.rawCells();
            Cell last = rawCells[rawCells.length - 1];
            byte[] row = CellUtil.cloneRow(last);
            byte[] originalRow = rowKeyDistributor.getOriginalKey(row);
            this.lastTransactionId = new TransactionId(originalRow);
            if (logger.isDebugEnabled()) {
                logger.debug("lastRowTimestamp={}, lastTransactionId={}, ", lastRowTimestamp, lastTransactionId);
            }
        }

        private Long getLastRowTimestamp() {
            return lastRowTimestamp;
        }

        public TransactionId getLastTransactionId() {
            return lastTransactionId;
        }
    }

    private Scan createScan(String appName, Range range) {

        if (appName == null) {
            throw new IllegalArgumentException("appName must not null");
        }
        if (appName.length() > SauronConstants.APPLICATION_NAME_MAX_LEN)
            appName = appName.substring(0, SauronConstants.APPLICATION_NAME_MAX_LEN - 1);

        Scan scan = new Scan();
        scan.setCaching(this.scanCacheSize);

        byte[] startKey = null;
        if (range.getFromTranSeq() != null && range.getFromTranSeq().length() > 0)
            startKey = BytesUtils.stringLongStringToBytes(appName, SauronConstants.APPLICATION_NAME_MAX_LEN, range.getFrom(), range.getFromTranSeq());
        else
            startKey = getRowKey(appName, range.getFrom());
        byte[] endKey = getRowKey(appName, range.getTo());
        scan.setStartRow(startKey);
        scan.setStopRow(endKey);
        scan.addFamily(HbaseTables.METRICS_ORI_DATA_CF_TRACE);
        scan.setId("MetricsOri Scan");
        // toString() method of Scan converts a message to json format so it is slow for the first time.
        logger.debug("create scan:{}", scan);
        return scan;
    }


    private byte[] getRowKey(String appName, long timestamp) {

        return BytesUtils.stringLongToBytes(appName, SauronConstants.APPLICATION_NAME_MAX_LEN, timestamp);
    }


    public int getLIMIT_RESULT() {
        return LIMIT_RESULT;
    }
}
