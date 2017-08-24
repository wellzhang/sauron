package com.fengjr.sauron.converger.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xubiao.fan on 2016/8/5.
 */
public class LocalCache {

    private static int intervalSec = 1* 30 * 1000; //30 sec
    private static Map<String,Integer> cache = new ConcurrentHashMap<String,Integer>();
    private static final Logger logger = LoggerFactory.getLogger(LocalCache.class);

    static{
       Thread thread= new TaskClearCacheThread();
        thread.setDaemon(true);
        thread.start();
    }

    public static boolean getKeyExist(String key){

        return cache.containsKey(key);
    }

    public static void setCacheKey(String key){
        if(!cache.containsValue(key))
            cache.put(key,1);
    }

    private static void clearExpiredToken(){
        cache.clear();
    }


    static  class TaskClearCacheThread extends Thread {
        @Override
        public void run() {
            this.setName("Thread Clear local  traice app ship ");
            logger.info("LocalCache trace app ship start ");
            while (!isInterrupted()) {
                try {
                    logger.info("running LocalCache start ");
                    clearExpiredToken();
                    Thread.sleep(intervalSec);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}


