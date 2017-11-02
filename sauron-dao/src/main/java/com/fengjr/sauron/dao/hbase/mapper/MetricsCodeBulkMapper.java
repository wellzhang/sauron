package com.fengjr.sauron.dao.hbase.mapper;

import com.fengjr.sauron.dao.hbase.HbaseTables;
import com.fengjr.sauron.dao.model.MetricsCodeBulkData;
import com.fengjr.sauron.dao.model.TransactionId;
import com.fengjr.sauron.dao.util.TransactionIdUtils;
import com.sematext.hbase.wd.RowKeyDistributorByHashPrefix;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/1.
 */
@Repository
public class MetricsCodeBulkMapper implements RowMapper<List<MetricsCodeBulkData>> {

    @Autowired
    @Qualifier("traceDistributor")
    private RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix;

    @Override
    public List<MetricsCodeBulkData> mapRow(Result result, int rowNum) throws Exception {
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        final byte[] rowKey = getOriginalKey(result.getRow());
        final TransactionId transactionId = new TransactionId(rowKey);

        final Cell[] rawCells = result.rawCells();
        List<MetricsCodeBulkData> bulkDatas = new LinkedList<MetricsCodeBulkData>();
        for (Cell cell : rawCells) {

            // only if family name is
            if (CellUtil.matchingFamily(cell, HbaseTables.METRICS_CODE_BULK_DATA_CF)) {
                MetricsCodeBulkData vo = new MetricsCodeBulkData();
                vo.setAppName(transactionId.getAppName());
                vo.setTraceId(transactionId.getStartTime()+ TransactionIdUtils.TRANSACTION_ID_DELIMITER+transactionId.getTransactionSequence());
                //vo.setSpanId(Bytes.toString(cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength()));
                vo.readValue(cell.getValueArray(), cell.getValueOffset());


                bulkDatas.add(vo);
            }
        }
        return bulkDatas;
    }

    private byte[] getOriginalKey(byte[] rowKey) {
        return rowKeyDistributorByHashPrefix.getOriginalKey(rowKey);
    }

}
