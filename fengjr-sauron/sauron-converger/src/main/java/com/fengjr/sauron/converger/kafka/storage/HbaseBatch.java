package com.fengjr.sauron.converger.kafka.storage;

import com.fengjr.sauron.dao.model.MetricsOriData;
import org.bson.Document;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/21.
 */
public class HbaseBatch<T> {

    private List<T> batchObjs = new LinkedList<>();
    private final int BATCH_NUMS = 10000;
    private ReentrantLock lock = new ReentrantLock(false);
    private HbaseManger hbaseManger;
    private String table ;



    public HbaseBatch(HbaseManger hbaseManger,String table ){
        this.hbaseManger = hbaseManger;
        this.table = table;
    }


    public void addBatch(T  obj){
        try{
            lock.lock();
            batchObjs.add(obj);
        }finally {
            lock.unlock();
        }
        if (getSize() >= BATCH_NUMS)
            hbaseManger.submitBatch(table);

    }

    protected  List<T> getBatchObjsAndClear() {

        lock.lock();
        try{
            List docs = batchObjs;
            List<T> tmp = new LinkedList();
            batchObjs = tmp;
            return docs;
        }finally {
            lock.unlock();
        }

    }


    public int getSize() {
        return batchObjs.size();
    }
}
