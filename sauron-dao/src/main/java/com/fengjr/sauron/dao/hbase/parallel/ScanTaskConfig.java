/*
 * Copyright 2015 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fengjr.sauron.dao.hbase.parallel;

import com.sematext.hbase.wd.AbstractRowKeyDistributor;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HTableInterfaceFactory;
import org.springframework.data.hadoop.hbase.HbaseAccessor;

import java.nio.charset.Charset;

/**
 * @author xubiao.fan@fengjr.com
 */
public class ScanTaskConfig {

    private final String tableName;
    private final Configuration configuration;
    private final Charset charset;
    private final HTableInterfaceFactory tableFactory;

    private final AbstractRowKeyDistributor rowKeyDistributor;
    private final int scanTaskQueueSize;

    public ScanTaskConfig(String tableName, HbaseAccessor hbaseAccessor, AbstractRowKeyDistributor rowKeyDistributor, int scanCaching) {
        this(tableName, hbaseAccessor.getConfiguration(), hbaseAccessor.getCharset(), hbaseAccessor.getTableFactory(), rowKeyDistributor, scanCaching);
    }

    public ScanTaskConfig(String tableName, Configuration configuration, Charset charset, HTableInterfaceFactory tableFactory, AbstractRowKeyDistributor rowKeyDistributor, int scanCaching) {
        if (tableName == null) {
            throw new NullPointerException("No table specified");
        }
        if (rowKeyDistributor == null) {
            throw new NullPointerException("rowKeyDistributor must not be null");
        }
        this.tableName = tableName;
        this.configuration = configuration;
        this.charset = charset;
        this.tableFactory = tableFactory;
        this.rowKeyDistributor = rowKeyDistributor;
        if (scanCaching > 0) {
            this.scanTaskQueueSize = scanCaching;
        } else {
            this.scanTaskQueueSize = configuration.getInt(
                    HConstants.HBASE_CLIENT_SCANNER_CACHING,
                    HConstants.DEFAULT_HBASE_CLIENT_SCANNER_CACHING);
        }
    }

    public String getTableName() {
        return tableName;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Charset getCharset() {
        return charset;
    }

    public HTableInterfaceFactory getTableFactory() {
        return tableFactory;
    }

    public AbstractRowKeyDistributor getRowKeyDistributor() {
        return rowKeyDistributor;
    }

    public int getScanTaskQueueSize() {
        return scanTaskQueueSize;
    }
}
