package com.fengjr.sauron.converger.kafka.archived;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.fengjr.cachecloud.client.IRedis;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fengjr.sauron.commons.constant.RedisConstant;
import com.fengjr.sauron.commons.constant.TPEnum;
import com.fengjr.sauron.commons.utils.DateUtils;
import com.fengjr.sauron.converger.kafka.decoder.LogMsgDecoder;
import com.fengjr.sauron.converger.util.InfluxdbUtils;
import com.fengjr.sauron.converger.util.InfluxdbUtilsForPhone;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bingquan.an@fengjr.com on 2015/10/30.
 */
public class ArchivedMinute {

	private Logger logger = LoggerFactory.getLogger(ArchivedMinute.class);
	// static final ExecutorService pool = new ThreadPoolExecutor(1, 2, 1, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(20000), Executors.defaultThreadFactory(), new
	// ThreadPoolExecutor.CallerRunsPolicy());

	static final ScheduledExecutorService scheduledPool = new ScheduledThreadPoolExecutor(10, Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());

	//static RedisClusterClient redisClient = RedisClusterClientFactory.getRedisClusterClient("redisCache");
	@Autowired
	private IRedis redisClient;
	public static final String COLLECTION_NAME_METRICS_MINUTE = "metrics_minute";
	public static final String COLLECTION_NAME_TP_MINUTE = "metrics_tp_minute";
	public static final String COLLECTION_NAME = "metrics_ori_data";
	public static final String COLLECTION_NAME_CODEBULK = "metrics_ori_data_codebulk";
	public static final String COLLECTION_NAME_ALARM = "metrics_ori_data_codebulk_alarm";

	@SuppressWarnings("unused")
	private void run() {

		scheduledPool.scheduleAtFixedRate(new Task(), DateUtils.nextMinuteDelayMs(), 60 * 1000, TimeUnit.MILLISECONDS);
	}

	class Task implements Runnable {

		@Override
		public void run() {
			try {
				archeved_new();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				archeved_phone();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// user defines code block
			try {
				archevedCodeBulk();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				archeved_h5();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void archeved_phone() {

			ConcurrentHashSet<String> phoneDataCache = LogMsgDecoder.phoneDataCache;

			for (String string : phoneDataCache) {

				try {

					String[] split = string.split("\\|");
					String hostName = split[0];
					String appName = split[1];
					String methodName = split[2];
					String logtime = split[3];// 是否需要判断
					String screen = split[4];// 是否需要判断
					String methodType = split[5];// 是否需要判断
					String platform = split[6];// 是否需要判断

					String key = string + "|tp";

					long totalCount = redisClient.zcard(key);
					int index_tp0 = totalCount <= 0 ? 0 : (int) (totalCount - 1);
					int index_tp50 = (int) Math.floor(totalCount * TPEnum.TP_50.getValue());
					int index_tp90 = (int) Math.floor(totalCount * TPEnum.TP_90.getValue());
					int index_tp99 = (int) Math.floor(totalCount * TPEnum.TP_99.getValue());
					// int index_tp999 = (int) Math.floor(totalCount * TPEnum.TP_999.getValue());
					int index_tp100 = 0;

					String tp0 = pop(redisClient.zrevrange(key, index_tp0, index_tp0));
					String tp50 = pop(redisClient.zrevrange(key, index_tp50, index_tp50));
					String tp90 = pop(redisClient.zrevrange(key, index_tp90, index_tp90));
					String tp99 = pop(redisClient.zrevrange(key, index_tp99, index_tp99));
					// String tp999 = pop(redisClient.zrevrange(key, index_tp999, index_tp999));
					String tp100 = pop(redisClient.zrevrange(key, index_tp100, index_tp100));
					String traceId = pop_traceId(redisClient.zrevrange(key, index_tp100, index_tp100));

					Date logtimedDate = DateUtils.String2Date(logtime, DateUtils.MINUTE_DATESTRING_PATTERN);

					int sampling_avgvalue = 0;
					Integer sampling_sumvalue = null;
					Integer sampling_count = null;
					try {
						sampling_sumvalue = Integer.valueOf(redisClient.hget(string, RedisConstant.HASH_KEY_SAMPLING_SUMVAULE));
						sampling_count = Integer.valueOf(redisClient.hget(string, RedisConstant.HASH_KEY_SAMPLING_COUNT));
						sampling_avgvalue = sampling_sumvalue / sampling_count;
					} catch (Exception e) {
						sampling_avgvalue = Integer.parseInt(tp50);
						// logger.info("sauron method:{},sampling_sumvalue:{}, sampling_count:{}", methodName, sampling_sumvalue, sampling_count, e);
					}

					redisClient.expire(key, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
					redisClient.expire(string, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);

					BatchPoints batchPoints = InfluxdbUtilsForPhone.getPhoneOriBuilder().tag("appName", appName).tag("hostName", hostName).tag("methodType", methodType).tag("method", methodName)
							.tag("platform", platform).tag("screen", screen).build();

					Point point = Point.measurement(InfluxdbUtilsForPhone.table_phone_ori).time(logtimedDate.getTime(), TimeUnit.MILLISECONDS).field("tp0", Integer.valueOf(tp0))
							.field("tp90", Integer.valueOf(tp90)).field("tp99", Integer.valueOf(tp99)).field("tp999", Integer.valueOf(tp50)).field("tp100", Integer.valueOf(tp100))
							.field("count", totalCount).field("avg", sampling_avgvalue).field("traceId", traceId).build();

					batchPoints.point(point);

					InfluxDB influxDB = InfluxdbUtilsForPhone.getInfluxDB_phone();

					influxDB.write(batchPoints);

				} catch (Exception e) {
					logger.info("archived_new error", e);
				} finally {
					phoneDataCache.remove(string);
				}

			}
		}
	}

	public void archeved_new() {

		ConcurrentHashSet<String> appDataCache = LogMsgDecoder.appDataCache;

		for (String string : appDataCache) {

			try {

				String[] split = string.split("\\|");
				String hostName = split[0];
				String appName = split[1];
				String methodName = split[2];
				String logtime = split[3];// 是否需要判断
				String source = split[4];// 是否需要判断
				String type = split[5];// 是否需要判断
				String detail = split[6];// 是否需要判断

				String key = string + "|tp";
				String keyError = string + "|ERR";
				String keyErrorTraceID = string + "|ERR_TRACEID";

				long totalCount = redisClient.zcard(key);
				int index_tp0 = totalCount <= 0 ? 0 : (int) (totalCount - 1);
				int index_tp70 = (int) Math.floor(totalCount * TPEnum.TP_70.getValue());
				int index_tp90 = (int) Math.floor(totalCount * TPEnum.TP_90.getValue());
				int index_tp99 = (int) Math.floor(totalCount * TPEnum.TP_99.getValue());
				int index_tp999 = (int) Math.floor(totalCount * TPEnum.TP_999.getValue());
				int index_tp100 = 0;

				String tp0 = pop(redisClient.zrevrange(key, index_tp0, index_tp0));
				String tp70 = pop(redisClient.zrevrange(key, index_tp70, index_tp70));
				String tp90 = pop(redisClient.zrevrange(key, index_tp90, index_tp90));
				String tp99 = pop(redisClient.zrevrange(key, index_tp99, index_tp99));
				String tp999 = pop(redisClient.zrevrange(key, index_tp999, index_tp999));
				String tp100 = pop(redisClient.zrevrange(key, index_tp100, index_tp100));
				String traceId = pop_traceId(redisClient.zrevrange(key, index_tp100, index_tp100));
				String errorTraceId = redisClient.get(keyErrorTraceID);
				if (errorTraceId == null || errorTraceId.length() == 0) {
					errorTraceId = traceId;
				}

				Date logtimedDate = DateUtils.String2Date(logtime, DateUtils.MINUTE_DATESTRING_PATTERN);

				int sampling_avgvalue = Integer.parseInt(tp70);
				// Integer sampling_sumvalue = null;
				// Integer sampling_count = null;

				// try {
				// sampling_sumvalue = Integer.valueOf(redisClient.hget(string, RedisConstant.HASH_KEY_SAMPLING_SUMVAULE));
				// sampling_count = Integer.valueOf(redisClient.hget(string, RedisConstant.HASH_KEY_SAMPLING_COUNT));
				// sampling_avgvalue = sampling_sumvalue / sampling_count;
				// } catch (Exception e) {
				// sampling_avgvalue = Integer.parseInt(tp50);
				// // logger.info("sauron method:{},sampling_sumvalue:{}, sampling_count:{}", methodName, sampling_sumvalue, sampling_count, e);
				// }
				int keyErrNum = 0;
				try {
					if (redisClient.exists(keyError)) {
						keyErrNum = Integer.valueOf(redisClient.get(keyError));
						redisClient.expire(keyError, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
					}
				} catch (Exception ex) {
				}

				redisClient.expire(key, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
				redisClient.expire(string, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);

				BatchPoints batchPoints = InfluxdbUtils.getBuilder()//
						.tag("appName", appName)//
						.tag("hostName", hostName)//
						.tag("method", methodName)//
						.tag("source", source)//
						.tag("type", type)//
						.tag("detail", detail)//
						.build();//
				Point point = Point.measurement("sauron")//
						.time(logtimedDate.getTime(), TimeUnit.MILLISECONDS)//
						.field("tp0", Integer.valueOf(tp0))//
						.field("tp90", Integer.valueOf(tp90))//
						.field("tp99", Integer.valueOf(tp99))//
						.field("tp999", Integer.valueOf(tp999))//
						.field("tp100", Integer.valueOf(tp100))//
						.field("count", totalCount)//
						.field("avg", sampling_avgvalue)//
						.field("traceId", traceId)//
						.field("exception", keyErrNum)//
						.field("errorTraceId", errorTraceId)//
						.build();//

				batchPoints.point(point);
				InfluxDB influxDB = InfluxdbUtils.getInfluxDB();
				influxDB.write(batchPoints);
			} catch (Exception e) {
				logger.info("archived_new error", e);
			} catch (Throwable th) {
				logger.info("archived_new error", th);
			} finally {
				appDataCache.remove(string);
			}

		}
	}

	public void archeved_h5() {

		ConcurrentHashSet<String> h5DataCache = LogMsgDecoder.h5DataCache;

		for (String string : h5DataCache) {

			try {

				String[] split = string.split("\\|");
				String hostName = split[0];
				String appName = split[1];
				String methodName = split[2];
				String logtime = split[3];// 是否需要判断

				String key = string + "|tp";

				long totalCount = redisClient.zcard(key);
				int index_tp0 = totalCount <= 0 ? 0 : (int) (totalCount - 1);
				int index_tp50 = (int) Math.floor(totalCount * TPEnum.TP_50.getValue());
				int index_tp90 = (int) Math.floor(totalCount * TPEnum.TP_90.getValue());
				int index_tp99 = (int) Math.floor(totalCount * TPEnum.TP_99.getValue());
				int index_tp999 = (int) Math.floor(totalCount * TPEnum.TP_999.getValue());
				int index_tp100 = 0;

				String tp0 = pop_h5(redisClient.zrevrange(key, index_tp0, index_tp0));
				String tp50 = pop_h5(redisClient.zrevrange(key, index_tp50, index_tp50));
				String tp90 = pop_h5(redisClient.zrevrange(key, index_tp90, index_tp90));
				String tp99 = pop_h5(redisClient.zrevrange(key, index_tp99, index_tp99));
				String tp999 = pop_h5(redisClient.zrevrange(key, index_tp999, index_tp999));
				String tp100 = pop_h5(redisClient.zrevrange(key, index_tp100, index_tp100));

				Date logtimedDate = DateUtils.String2Date(logtime, DateUtils.MINUTE_DATESTRING_PATTERN);

				int sampling_avgvalue = 0;
				Integer sampling_sumvalue = null;
				Integer sampling_count = null;
				try {
					sampling_sumvalue = Integer.valueOf(redisClient.hget(string, RedisConstant.HASH_KEY_SAMPLING_SUMVAULE));
					sampling_count = Integer.valueOf(redisClient.hget(string, RedisConstant.HASH_KEY_SAMPLING_COUNT));
					sampling_avgvalue = sampling_sumvalue / sampling_count;
				} catch (Exception e) {
					sampling_avgvalue = Integer.parseInt(tp50);
				}

				redisClient.expire(key, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
				redisClient.expire(string, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);

				BatchPoints batchPoints = InfluxdbUtils.getH5Builder().tag("appName", appName).tag("hostName", hostName).tag("method", methodName).build();

				Point point = Point.measurement(InfluxdbUtils.table_h5).time(logtimedDate.getTime(), TimeUnit.MILLISECONDS).field("tp0", Integer.valueOf(tp0)).field("tp90", Integer.valueOf(tp90))
						.field("tp99", Integer.valueOf(tp99)).field("tp999", Integer.valueOf(tp999)).field("tp100", Integer.valueOf(tp100)).field("count", totalCount).field("avg", sampling_avgvalue)
						.build();

				batchPoints.point(point);

				InfluxDB influxDB = InfluxdbUtils.getInfluxDB();

				influxDB.write(batchPoints);

			} catch (Exception e) {
				logger.info("archived_h5 error", e);
			} finally {
				h5DataCache.remove(string);
			}

		}
	}

	public void archevedCodeBulk() {

		ConcurrentHashSet<String> appDataCache = LogMsgDecoder.userCodeBulkCache;

		for (String string : appDataCache) {

			try {

				String[] split = string.split("\\|");

				String hostName = split[0];
				String appName = split[1];
				String methodName = split[2];
				String logtime = split[3];// 是否需要判断

				String key = string + "|tp";

				long totalCount = redisClient.zcard(key);
				int index_tp0 = totalCount <= 0 ? 0 : (int) (totalCount - 1);
				int index_tp50 = (int) Math.floor(totalCount * TPEnum.TP_50.getValue());
				int index_tp90 = (int) Math.floor(totalCount * TPEnum.TP_90.getValue());
				int index_tp99 = (int) Math.floor(totalCount * TPEnum.TP_99.getValue());
				int index_tp999 = (int) Math.floor(totalCount * TPEnum.TP_999.getValue());
				int index_tp100 = 0;

				String tp0 = pop(redisClient.zrevrange(key, index_tp0, index_tp0));
				String tp50 = pop(redisClient.zrevrange(key, index_tp50, index_tp50));
				String tp90 = pop(redisClient.zrevrange(key, index_tp90, index_tp90));
				String tp99 = pop(redisClient.zrevrange(key, index_tp99, index_tp99));
				String tp999 = pop(redisClient.zrevrange(key, index_tp999, index_tp999));
				String tp100 = pop(redisClient.zrevrange(key, index_tp100, index_tp100));
				String traceId = pop_traceId(redisClient.zrevrange(key, index_tp100, index_tp100));

				Date logtimedDate = DateUtils.String2Date(logtime, DateUtils.MINUTE_DATESTRING_PATTERN);

				int sampling_avgvalue = 0;
				Integer sampling_sumvalue = null;
				Integer sampling_count = null;
				try {
					sampling_sumvalue = Integer.valueOf(redisClient.hget(string, RedisConstant.HASH_KEY_SAMPLING_SUMVAULE));
					sampling_count = Integer.valueOf(redisClient.hget(string, RedisConstant.HASH_KEY_SAMPLING_COUNT));
					sampling_avgvalue = sampling_sumvalue / sampling_count;
				} catch (Exception e) {
					sampling_avgvalue = Integer.parseInt(tp50);
					logger.info("sauron method:{},sampling_sumvalue:{}, sampling_count:{}", methodName, sampling_sumvalue, sampling_count, e);
				}

				redisClient.expire(key, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);
				redisClient.expire(string, RedisConstant.EXPIRED_HASH_FREQUENCY_TARGET);

				BatchPoints batchPoints = InfluxdbUtils.getBuilder().tag("appName", appName).tag("hostName", hostName).tag("method", methodName).build();

				Point point = Point.measurement("sauron_codebulk").time(logtimedDate.getTime(), TimeUnit.MILLISECONDS).field("tp0", Integer.valueOf(tp0)).field("tp90", Integer.valueOf(tp90))
						.field("tp99", Integer.valueOf(tp99)).field("tp999", Integer.valueOf(tp999)).field("tp100", Integer.valueOf(tp100)).field("count", totalCount).field("avg", sampling_avgvalue)
						.field("traceId", traceId).build();

				batchPoints.point(point);

				InfluxDB influxDB = InfluxdbUtils.getInfluxDB();

				influxDB.write(batchPoints);

			} catch (Exception e) {
				logger.info("archived_new error", e);
			} finally {
				appDataCache.remove(string);
			}

		}
	}

	private String pop(Set<String> set) {
		String value = "";
		for (String item : set) {
			value = item.split("#")[1];
			break;
		}
		return value;
	}

	private String pop_h5(Set<String> set) {
		String value = "";
		for (String item : set) {
			value = item;
			break;
		}
		return value;
	}

	private String pop_traceId(Set<String> set) {
		String value = "";
		for (String item : set) {
			value = item.split("#")[0];
			break;
		}
		return value;
	}

}
