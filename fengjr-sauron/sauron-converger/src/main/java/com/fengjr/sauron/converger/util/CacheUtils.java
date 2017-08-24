//package com.fengjr.sauron.converger.util;
//
//import java.lang.reflect.Field;
//
//import redis.clients.jedis.JedisCluster;
//
//import com.fengjr.redis.client.RedisClusterClient;
//import com.fengjr.redis.client.RedisClusterClientFactory;
//import com.fengjr.sauron.commons.constant.RedisConstant;
//
//public class CacheUtils {
//
//	public final static RedisClusterClient redisClient = RedisClusterClientFactory.getRedisClusterClient("redisCache");
//
//	private static JedisClusterPipeline jcp = null;
//
//	public static JedisClusterPipeline getPipeLine() {
//
//		if (jcp == null) {
//			try {
//				synchronized (CacheUtils.class) {
//					Field declaredField = RedisClusterClient.class.getDeclaredField("jedisCluster");
//					declaredField.setAccessible(true);
//
//					if (redisClient != null) {
//						JedisCluster jedisCluster = (JedisCluster) declaredField.get(redisClient);
//						jcp = JedisClusterPipeline.pipelined(jedisCluster);
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return jcp;
//	}
//
//	public static void zadd(String key, String value, String traceid, int second) {
//		try {
//			CacheUtils.getPipeLine().zadd(key, Double.valueOf(value), traceid + "#" + value);
//		} catch (Exception e) {
//			e.printStackTrace();
//			jcp = null;
//			CacheUtils.getPipeLine().zadd(key, Double.valueOf(value), traceid + "#" + value);
//		}
//		CacheUtils.getPipeLine().expire(key, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
//	}
//}
