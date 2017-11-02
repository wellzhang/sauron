/*
 * Copyright 2014 NAVER Corp.
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

package com.fengjr.sauron.dao.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.hadoop.hbase.HbaseSystemException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * HTableInterfaceFactory based on HTablePool.
 * @author xubiao.fan@fengjr.com
 */
public class PooledHTableFactory implements HTableInterfaceFactory, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final int DEFAULT_POOL_SIZE = 256;
    public static final int DEFAULT_WORKER_QUEUE_SIZE = 1024*5;
    public static final boolean DEFAULT_PRESTART_THREAD_POOL = false;

    private final ExecutorService executor;
    private final HConnection connection;
    private final ClusterConnection conn;


    public PooledHTableFactory(Configuration config) {
        this(config, DEFAULT_POOL_SIZE, DEFAULT_WORKER_QUEUE_SIZE, DEFAULT_PRESTART_THREAD_POOL);
    }

    public PooledHTableFactory(Configuration config, int poolSize, int workerQueueSize, boolean prestartThreadPool) {

        this.executor = createExecutorService(poolSize, workerQueueSize, prestartThreadPool);
        try {

            this.connection = (HConnection)ConnectionFactory.createConnection(config, executor);
            conn = (ClusterConnection)ConnectionFactory.createConnection(config, executor);
        } catch (IOException e) {
            throw new HbaseSystemException(e);
        }
    }

    private ExecutorService createExecutorService(int poolSize, int workQueueMaxSize, boolean prestartThreadPool) {

        logger.info("create HConnectionThreadPoolExecutor poolSize:{}, workerQueueMaxSize:{}", poolSize, workQueueMaxSize);

        ThreadPoolExecutor threadPoolExecutor = ExecutorFactory.newFixedThreadPool(poolSize, workQueueMaxSize, "HConnectionExecutor", true);
        if (prestartThreadPool) {
            logger.info("prestartAllCoreThreads");
            threadPoolExecutor.prestartAllCoreThreads();
        }

        return threadPoolExecutor;
    }


    @Override
    public HTableInterface createHTableInterface(Configuration config, byte[] tableName) {
        try {
            conn.getTable(tableName,executor);
            return connection.getTable(tableName, executor);
        } catch (IOException e) {
            throw new HbaseSystemException(e);
        }
    }

    @Override
    public void releaseHTableInterface(HTableInterface table) throws IOException {
        if (table != null) {
            table.close();
        }
    }


    @Override
    public void destroy() throws Exception {
        logger.info("PooledHTableFactory.destroy()");
        if (connection != null) {
            try {
                this.connection.close();
            } catch (IOException ex) {
                logger.warn("Connection.close() error:" + ex.getMessage(), ex);
            }
        }

        if (this.executor != null) {
            this.executor.shutdown();
            try {
                final boolean shutdown = executor.awaitTermination(1000 * 5, TimeUnit.MILLISECONDS);
                if (!shutdown) {
                    final List<Runnable> discardTask = this.executor.shutdownNow();
                    logger.warn("discard task size:{}", discardTask.size());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
