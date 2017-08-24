package com.feng.sauron.test.domain;

import com.fengjr.redis.client.RedisClusterClient;
import com.fengjr.redis.client.RedisClusterClientFactory;

public class CacheUtils {

	public final static RedisClusterClient redisClient = RedisClusterClientFactory.getRedisClusterClient("redisCache");
}
