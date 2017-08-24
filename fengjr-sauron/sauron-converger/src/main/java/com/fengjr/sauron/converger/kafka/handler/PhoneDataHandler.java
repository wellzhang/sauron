package com.fengjr.sauron.converger.kafka.handler;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import com.fengjr.cachecloud.client.IRedis;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;

import com.fengjr.sauron.commons.constant.RedisConstant;
import com.fengjr.sauron.commons.utils.DateUtils;
import com.fengjr.sauron.commons.utils.JsonUtils;
import com.fengjr.sauron.converger.kafka.decoder.LogMsgDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by xubiao.fan@fengjr.com on 2016/11/8.
 */
@Component("phoneDataHandler")
public class PhoneDataHandler implements BaseHandler {

	@Autowired
	private IRedis redisClient;

	public static ConcurrentHashSet<String> phoneDataCache = LogMsgDecoder.phoneDataCache;



	@Override
	public void handle(String line, String hostName, String logTime, String version) {

		try {
			Map<String, Object> mapData = JsonUtils.getObject(line, Map.class);
			mapData.put("hostName", hostName);
			mapData.put("logTime", logTime);
			mapData.put("version", version);
			regPhoneData(mapData);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@SuppressWarnings("unchecked")
	private void regPhoneData(Map<String, Object> logData) {
		try {
			Map<String, Object> sauronMap = (Map<String, Object>) logData.get("Sauron");
			String methodName = String.valueOf(sauronMap.get("MethodName"));

			String logtime = String.valueOf(logData.get("logtime"));
			Date string2Date = DateUtils.String2Date(logtime, DateUtils.DEFAULT_DATESTRING_PATTERN);
			String logTimeMinute = DateUtils.Date2String(string2Date, DateUtils.MINUTE_DATESTRING_PATTERN);

			String hostName = String.valueOf(logData.get("hostName"));

			String appName = sauronMap.get("AppName").toString().replace("-", "_");

			String traceid = String.valueOf(sauronMap.get("Traceid"));

			String screen = String.valueOf(sauronMap.get("Screen"));

			String methodType = String.valueOf(sauronMap.get("MethodType"));

			String platform = String.valueOf(sauronMap.get("Platform"));

			StringBuffer sb = new StringBuffer();

			sb.append(hostName).append("|").append(appName).append("|").append(methodName).append("|").append(logTimeMinute).append("|").append(screen).append("|").append(methodType).append("|")
					.append(platform);

			String key = sb.toString();

			Map<String, Object> Tracer = (Map<String, Object>) sauronMap.get("Tracer");

			String duration = (String.valueOf(Tracer.get("duration"))).replace("ms", "");

			if (duration == null || "null".equals(duration) || duration.length() == 0) {
				return;
			}

			try {
				Long.valueOf(duration);
			} catch (Exception e) {
				return;
			}

			if (StringUtils.isNotBlank(key) && !phoneDataCache.contains(key)) {
				phoneDataCache.add(key);
			}

			if (redisClient.hexists(key, RedisConstant.HASH_KEY_SAMPLING_COUNT)) {
				redisClient.hincrby(key, RedisConstant.HASH_KEY_SAMPLING_COUNT, 1);
				redisClient.hincrby(key, RedisConstant.HASH_KEY_SAMPLING_SUMVAULE, Long.valueOf(duration));
			} else {
				redisClient.hset(key, RedisConstant.HASH_KEY_SAMPLING_COUNT, "1", 600);
				redisClient.hset(key, RedisConstant.HASH_KEY_SAMPLING_SUMVAULE, duration, 600);
			}
			redisClient.zadd(key + "|tp", Double.valueOf(duration), traceid + "#" + duration, 600);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
