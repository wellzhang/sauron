package com.fengjr.sauron.dao.hbase.mapper;

import com.fengjr.sauron.dao.hbase.HbaseTables;
import com.fengjr.sauron.dao.model.MetricsOriData;
import com.fengjr.sauron.dao.model.TraceAppShip;
import com.sematext.hbase.wd.RowKeyDistributorByHashPrefix;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/10.
 */
@Repository
public class TraceAppShipMapper implements RowMapper<List<TraceAppShip>> {


    @Autowired
    @Qualifier("traceDistributor")
    private RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix;

    @Override
    public List<TraceAppShip> mapRow(Result result, int i) throws Exception {

        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        final byte[] rowKey = getOriginalKey(result.getRow());
        String traceId = Bytes.toString(rowKey);
        final Cell[] rawCells = result.rawCells();
        List<TraceAppShip> traceAppShips = new LinkedList<>();
        for (Cell cell : rawCells) {
            if (CellUtil.matchingFamily(cell, HbaseTables.TRACE_APP_SHIP_CF)) {
                TraceAppShip ship = new TraceAppShip();
                ship.setTraceId(traceId);
                ship.setAppName(Bytes.toString(cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength()));
                traceAppShips.add(ship);

            }
        }
        return traceAppShips;
    }

    private byte[] getOriginalKey(byte[] rowKey) {
        return rowKeyDistributorByHashPrefix.getOriginalKey(rowKey);
    }
}
