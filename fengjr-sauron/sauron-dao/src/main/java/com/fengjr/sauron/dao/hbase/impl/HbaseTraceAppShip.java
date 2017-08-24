package com.fengjr.sauron.dao.hbase.impl;

import com.fengjr.sauron.dao.TraceAppShipDao;
import com.fengjr.sauron.dao.hbase.HbaseOperations2;
import com.fengjr.sauron.dao.hbase.HbaseTables;
import com.fengjr.sauron.dao.model.MetricsOriData;
import com.fengjr.sauron.dao.model.TraceAppShip;
import com.google.common.primitives.Bytes;
import com.mchange.lang.ByteUtils;
import com.sematext.hbase.wd.AbstractRowKeyDistributor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/11.
 */
@Repository
public class HbaseTraceAppShip implements TraceAppShipDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HbaseOperations2 hbaseTemplate;

    @Autowired
    @Qualifier("traceAppShipMapper")
    private RowMapper<List<TraceAppShip>> colMapper;

    @Autowired
    @Qualifier("traceDistributor")
    private AbstractRowKeyDistributor rowKeyDistributor;


    @Override
    public void insert(TraceAppShip traceAppShip) {
        if (traceAppShip == null) {
            throw new NullPointerException("traceAppShip must not be null");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("insert  {}", traceAppShip.toString());
        }
        byte[] rowKey = getDistributeRowKey(traceAppShip.getTraceId().getBytes());
        byte[] qualifier = org.apache.hadoop.hbase.util.Bytes.toBytes(traceAppShip.getAppName());

        this.hbaseTemplate.put(HbaseTables.TRACE_APP_SHIP, rowKey, HbaseTables.TRACE_APP_SHIP_CF, qualifier,new byte[]{0x31} );

    }

    @Override
    public void insertBatch(List<TraceAppShip> traceAppShips) {
        if(traceAppShips == null || traceAppShips.size()<1)
            return ;
        List<Put> puts = new LinkedList<>();
        for(TraceAppShip traceAppShip :traceAppShips){
            byte[] rowKey = getDistributeRowKey(traceAppShip.getTraceId().getBytes());
            byte[] qualifier = org.apache.hadoop.hbase.util.Bytes.toBytes(traceAppShip.getAppName());
            Put put = new Put(rowKey);
            put.addColumn(HbaseTables.TRACE_APP_SHIP_CF, qualifier, new byte[]{0x31});
            puts.add(put);
        }
        hbaseTemplate.put(HbaseTables.TRACE_APP_SHIP,puts);
    }


    @Override
    public List<TraceAppShip> getTraceAppShip(String traceId) {

        if(traceId == null || traceId.length() < 1 ){
            throw new NullPointerException("traceId must not be null");
        }
        byte[] rowKey = getDistributeRowKey(traceId.getBytes());
        Get get = new Get(rowKey);
        get.addFamily(HbaseTables.TRACE_APP_SHIP_CF);
        return hbaseTemplate.get(HbaseTables.TRACE_APP_SHIP, get, colMapper);
    }

    private byte[] getDistributeRowKey(byte[] transactionId) {
        return rowKeyDistributor.getDistributedKey(transactionId);
    }
}
