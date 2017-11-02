package com.fengjr.sauron.dao.mongodb;

import java.util.Map;

/**
 * Created by bingquan.an@fengjr.com on 2015/9/7.
 */
public class MongoFactory {

    private Map<String, MongoConfig> configMap;

    public MongoConfig getMongoConfig(String key){
        if(configMap==null || configMap.size()<=0)
            throw new RuntimeException("MongoFactory initialization occur fatal exception!");

        return configMap.get(key);
    }

    public void setConfigMap(Map<String, MongoConfig> configMap) {
        this.configMap = configMap;
    }
}
