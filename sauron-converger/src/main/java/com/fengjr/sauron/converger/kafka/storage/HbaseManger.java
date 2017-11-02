package com.fengjr.sauron.converger.kafka.storage;

import com.fengjr.sauron.dao.MetricsCodeBulkAlarmDataDao;
import com.fengjr.sauron.dao.MetricsCodeBulkDataDao;
import com.fengjr.sauron.dao.MetricsOriDataDao;
import com.fengjr.sauron.dao.TraceAppShipDao;
import com.fengjr.sauron.dao.hbase.HbaseTables;
import com.fengjr.sauron.dao.model.MetricsCodeBulkAlarmData;
import com.fengjr.sauron.dao.model.MetricsCodeBulkData;
import com.fengjr.sauron.dao.model.MetricsOriData;
import com.fengjr.sauron.dao.model.TraceAppShip;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/21.
 */
@Service
public class HbaseManger  implements InitializingBean{

    private Logger logger = LoggerFactory.getLogger(HbaseManger.class);

    @Autowired
    MetricsOriDataDao metricsOriDataDao;
    @Autowired
    TraceAppShipDao traceAppShipDao;
    @Autowired
    MetricsCodeBulkDataDao metricsCodeBulkDataDao;
    @Autowired
    MetricsCodeBulkAlarmDataDao metricsCodeBulkAlarmDataDao;



    ReentrantLock lock = new ReentrantLock();
    ConcurrentHashMap<String, HbaseBatch> map = new ConcurrentHashMap<String, HbaseBatch>();

    int intervalSec = 10 * 1000;

    public HbaseBatch getBatch(String table) {

        HbaseBatch hbaseBatch = map.get(table);
		if (hbaseBatch == null) {
			try {
				lock.lock();
				if (map.get(table) != null)
					return map.get(table);
                hbaseBatch = new HbaseBatch(this,table);
				map.put(table, hbaseBatch);
				return hbaseBatch;
			} finally {
				lock.unlock();
			}
		}
		return hbaseBatch;
    }


    public void submitBatch(String table) {
        HbaseBatch hbaseBatch = map.get(table);
        if (hbaseBatch != null) {

            if( HbaseTables.METRICS_ORI_DATA.equals(table)) {
                List<MetricsOriData> datas = hbaseBatch.getBatchObjsAndClear();

                long dd = System.currentTimeMillis();
                if (datas.size() > 0)
                    metricsOriDataDao.insertBatch(datas);
                logger.info("insert MetricsOriData hbase use {} ms, size:{} ", (System.currentTimeMillis() - dd), datas.size());
            }else if(table.equals(HbaseTables.TRACE_APP_SHIP) ) {

                List<TraceAppShip> traceAppShips = hbaseBatch.getBatchObjsAndClear();
                long traceSt = System.currentTimeMillis();
                if (traceAppShips.size() > 0)
                    traceAppShipDao.insertBatch(traceAppShips);
                logger.info("insert TraceAppShip  hbase use {} ms, size:{} ", (System.currentTimeMillis() - traceSt), traceAppShips.size());
            }else if(table.equals(HbaseTables.METRICS_CODE_BULK_DATA)) {

                List<MetricsCodeBulkData> metricsCodeBulkDatas = hbaseBatch.getBatchObjsAndClear();
                long bulkSt = System.currentTimeMillis();
                if (metricsCodeBulkDatas.size() > 0)
                    metricsCodeBulkDataDao.insertBatch(metricsCodeBulkDatas);
                logger.info("insert MetricsCodeBulkData  hbase use {} ms, size:{} ", (System.currentTimeMillis() - bulkSt), metricsCodeBulkDatas.size());
            }else if(table.equals(HbaseTables.METRICS_CODE_BULK_ALARM)){

                    List<MetricsCodeBulkAlarmData> metricsCodeBulkAlarmDatas = hbaseBatch.getBatchObjsAndClear();
                    long alarmSt = System.currentTimeMillis();
                    if (metricsCodeBulkAlarmDatas.size() > 0)
                        metricsCodeBulkAlarmDataDao.insertBatch(metricsCodeBulkAlarmDatas);
                    logger.info("insert MetricsCodeBulkData  hbase use {} ms, size:{} " , (System.currentTimeMillis() - alarmSt) ,metricsCodeBulkAlarmDatas.size());

            }
            //SauronTracer.end("sauron_converger_insert_mongo",docs.size()+"");
        }

    }

    protected void batchAll() {
        for (Map.Entry entry : map.entrySet()) {
            String key = entry.getKey().toString();
            submitBatch(key);
        }
    }

    public void init() {
        new HbaseManger.TaskSubmitThread().start();
        Runtime.getRuntime().addShutdownHook(new HbaseManger.HookSubmitThread());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    class TaskSubmitThread extends Thread {
        @Override
        public void run() {
            this.setName("Hbase Timer  Batch Submit");

            while (!isInterrupted()) {
                try {
                    batchAll();
                    Thread.sleep(intervalSec);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class HookSubmitThread extends Thread {
        @Override
        public void run() {
           batchAll();
        }
    }


}


