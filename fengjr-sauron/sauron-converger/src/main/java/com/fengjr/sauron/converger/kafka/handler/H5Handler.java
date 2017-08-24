package com.fengjr.sauron.converger.kafka.handler;


import java.io.IOException;
import java.util.Date;
import java.util.Map;

import com.fengjr.cachecloud.client.IRedis;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fengjr.sauron.commons.constant.RedisConstant;
import com.fengjr.sauron.commons.utils.DateUtils;
import com.fengjr.sauron.commons.utils.JsonUtils;
import com.fengjr.sauron.converger.kafka.decoder.LogMsgDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by xubiao.fan@fengjr.com on 2016/11/8.
 */
@Component("h5Handle")
public class H5Handler implements BaseHandler {


    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static ConcurrentHashSet<String> h5DataCache = LogMsgDecoder.h5DataCache;
	@Autowired
	IRedis redisClient;

    @Override
    public void handle(String line, String hostName, String logTime,String version) {

        try {
        	Map<String, Object> mapData = JsonUtils.getObject(line, Map.class);
            mapData.put("hostName",hostName);
            mapData.put("logTime",logTime);
            mapData.put("version",version);
            regMethod_h5(mapData);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @SuppressWarnings("unchecked")
	private void regMethod_h5(Map<String, Object> logData) {

		try {
			Map<String, Object> sauronMap = (Map<String, Object>) logData.get("sauron");

			String logtime = (String) logData.get("logtime");

			String hostName = (String) logData.get("hostName");

			String methodName = (String) sauronMap.get("url");

			String duration = String.valueOf(sauronMap.get("time"));

			if (duration.indexOf(".") > 0) {
				duration = duration.substring(0, duration.indexOf("."));
			}

			try {
				Long.valueOf(duration);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			String appName = sauronMap.get("serviceType").toString().replace("-", "_");

			Date string2Date = DateUtils.String2Date(logtime, DateUtils.DEFAULT_DATESTRING_PATTERN);
			String logTimeMinute = DateUtils.Date2String(string2Date, DateUtils.MINUTE_DATESTRING_PATTERN);

			StringBuffer sb = new StringBuffer();

			sb.append(hostName).append("|").append(appName).append("|").append(methodName).append("|").append(logTimeMinute);

			String key = sb.toString();

			if (StringUtils.isNotBlank(key) && !h5DataCache.contains(key)) {
				h5DataCache.add(key);
			}

			if (redisClient.hexists(key, RedisConstant.HASH_KEY_SAMPLING_COUNT)) {
				redisClient.hincrby(key, RedisConstant.HASH_KEY_SAMPLING_COUNT, 1);
				redisClient.hincrby(key, RedisConstant.HASH_KEY_SAMPLING_SUMVAULE, Long.valueOf(duration));
			} else {
				redisClient.hset(key, RedisConstant.HASH_KEY_SAMPLING_COUNT, "1", RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
				redisClient.hset(key, RedisConstant.HASH_KEY_SAMPLING_SUMVAULE, duration, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
			}
			redisClient.zadd(key + "|tp", Long.valueOf(duration), duration, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
