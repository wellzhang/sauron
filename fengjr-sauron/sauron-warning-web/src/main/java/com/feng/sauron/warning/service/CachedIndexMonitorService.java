package com.feng.sauron.warning.service;

import com.alibaba.fastjson.JSON;
import com.feng.sauron.warning.web.vo.TopologyData;
import com.fengjr.cachecloud.client.IRedis;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by lianbin.wang on 2016/11/25.
 */
@Service("CachedIndexMonitorService")
public class CachedIndexMonitorService implements TopologyService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("TopologyServiceImpl")
    private TopologyService topologyService;

    @Autowired
    private IRedis redisCluster;


    @Override
    public TopologyData load(String appName, String host, Date startTime, Date endTime) {
        long start = (startTime.getTime() / 60 * 1000) * 60 * 1000;
        long end = (endTime.getTime() / 60 * 1000) * 60 * 1000;

        String redisKey = "sauron-warning:topology:" + appName + ":" + start + "_" + end;
        String json = loadFromRedis(redisKey);

        if (StringUtils.isNotBlank(json)) {
            return JSON.parseObject(json, TopologyData.class);
        }
        TopologyData data = topologyService.load(appName, host, startTime, endTime);
        updateRedis(redisKey, JSON.toJSONString(data), 60);

        return data;
    }

    private void updateRedis(String redisKey, String redisValue, int seconds) {
        try {
            redisCluster.set(redisKey, redisValue, seconds);
        } catch (Exception e) {
            logger.error("error setting cache in redis, key:{}, msg:{}", redisKey, e.getMessage(), e);
        }
    }

    private String loadFromRedis(String redisKey) {
        try {
            return redisCluster.get(redisKey);
        } catch (Exception e) {
            logger.error("error getting from redis, key:{}, msg：{}", redisKey, e.getMessage(), e);
        }
        return null;
    }
}
