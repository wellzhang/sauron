package com.feng.sauron.warning.service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import com.fengjr.cachecloud.client.IRedis;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.dao.StrategyMapper;
import com.feng.sauron.warning.dao.StrategyTemMapper;
import com.feng.sauron.warning.domain.Strategy;
import com.feng.sauron.warning.task.LeaderSelectionClient;
import com.feng.sauron.warning.util.ConfigChangeWatcher;
import com.feng.sauron.warning.util.DateUtils;
import com.feng.sauron.warning.util.WatchableConfigClient;


/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年12月31日 上午10:25:36
 * 
 */

@Service
public class StrategyCheckService {

	//private static RedisClusterClient redisClient = RedisClusterClientFactory.getRedisClusterClient("redisCache");
	@Autowired
	private IRedis redisClient;
	private static final Logger logger = LoggerFactory.getLogger(StrategyCheckService.class);

	private static ConcurrentHashMap<String, Strategy> sMap = null;

	private static String format = DateUtils.format(new Date()) + "." + new Random().nextInt(1000);

	@Resource
	StrategyMapper strategyMapper;

	@Resource
	StrategyTemMapper strategyTemMapper;

	@Resource
	LeaderSelectionClient leaderSelectionClient;

	public static void removeAllStrategyCache() {
		sMap = null;
		logger.info("清除 StrategyCache ... ");
	}

	private final static String nodeName = "sauron_warning_sync_1";

	static {
		try {
			defaultWatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void defaultWatch() {

		WatchableConfigClient.getInstance().create(nodeName, "sauron.warning.strategy", "default");

		WatchableConfigClient.getInstance().getAndWatch(nodeName, "sauron.warning.strategy", "default", new ConfigChangeWatcher() {

			@Override
			public void onValueChanged(String newVal) {
				if (!format.equalsIgnoreCase(newVal)) {
					logger.info("检测到其他服务节点更新了strategy , 开始刷新缓存strategy缓存");
					removeAllStrategyCache();
				} else {
					logger.info("此次更新strategy 来自 服务本身 ,已经刷新过,忽略...");
				}
			}
		});
	}

	public static void watch() {

		try {
			String format_now = DateUtils.format(new Date()) + "." + new Random().nextInt(1000);
			WatchableConfigClient.getInstance().set(nodeName, "sauron.warning.strategy", format_now);
			format = format_now;
		} catch (Exception e) {
		}
	}

	public synchronized void updateStrategyCache(Long strategyId, Strategy strategy) {

		try {
			if (strategyId == null) {
				logger.info("strategyId :{} , 本次修改未更新到缓存，清空缓存 ，重新拉取所有数据", strategyId);
				removeAllStrategyCache();
			}
			init();
			if (strategy == null) {
				sMap.remove(strategyId);
			} else {
				if (strategyId.intValue() == strategy.getId().intValue()) {
					sMap.put(String.valueOf(strategyId), strategy);
				} else {
					removeAllStrategyCache();
				}
			}

			logger.info("更新 StrategyCache ... ");
			watch();
		} catch (Exception e) {
			e.printStackTrace();
			removeAllStrategyCache();
			init();
		}
	}

	public Strategy getDefault() {
		return getStrategyById(0L);
	}

	public Strategy getStrategyById(Long strategyId) {
		String valueOf = String.valueOf(strategyId);
		init();
		return sMap.get(valueOf);
	}

	public void init() {
		if (sMap == null) {
			sMap = new ConcurrentHashMap<>();
			logger.info("strategy缓存为空  开始拉取数据库...");
			List<Strategy> selectAll = strategyMapper.selectAll();
			for (Strategy strategy : selectAll) {
				sMap.put(String.valueOf(strategy.getId()), strategy);
			}
			logger.info("从数据库拉取strategy结束...");
		}
	}

	private boolean checkWarnDay(Strategy strategy) {

		if (strategy == null) {
			strategy = getDefault();
		}

		String warnDaysInWeek = strategy.getWarnDaysInWeek();
		if (!warnDaysInWeek.contains(String.valueOf(DateUtils.getWeekOfDate(new Date())))) {
			return false;
		}

		String startTimeInDay = strategy.getStartTimeInDay();
		String endTimeInDay = strategy.getEndTimeInDay();

		Date date = new Date();

		String format = DateUtils.format(date, "HH:mm");

		if (startTimeInDay.compareTo(endTimeInDay) < 0) {
			if (format.compareTo(startTimeInDay) < 0 || format.compareTo(endTimeInDay) > 0) {
				return false;
			}
		} else {
			if (format.compareTo(startTimeInDay) < 0 && format.compareTo(endTimeInDay) > 0) {
				return false;
			}
		}

		return true;
	}

	public ResponseDTO<Object> check2(String template, String phone, Long strategyId) {

		ResponseDTO<Object> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_FAILURE);

		try {
			if (StringUtils.isBlank(template) || StringUtils.isBlank(phone) || strategyId == null) {
				return responseDTO;
			}

			Strategy strategy = getStrategyById(strategyId);

			if (strategy == null) {
				strategy = getDefault();
			}

			if (!checkWarnDay(strategy)) {
				return responseDTO;
			}

			Integer minInterval = strategy.getMinInterval();
			Integer warnCountWithTime = strategy.getWarnCountWithTime();
			Integer warnMaxCount = strategy.getWarnMaxCount();

			String bakChar = strategy.getBakChar();
			Long bakNum = strategy.getBakNum();

			String warnCount = "template_" + template + "_" + strategy.getId() + "_" + phone;
			String minWarn = "min_interval_" + template + "_" + strategy.getId() + "_" + phone;
			String maxCountPerInterval = "max_count_per_interval_" + template + "_" + strategy.getId() + "_" + phone;

			StringBuffer sb = new StringBuffer();

			if (StringUtils.isNumeric(bakChar) && bakNum != null) {

				if (redisClient.exists(maxCountPerInterval)) {
					String string = redisClient.get(maxCountPerInterval);
					Integer count = 0;
					if (StringUtils.isNotBlank(string)) {
						count = Integer.parseInt(string);
					}
					if (count >= bakNum) {
						logger.info("{}分钟内已经发送过{}条,超过最大发送条数：{},不再发送 , template :{} ,strategyId:{}, strategyName:{}, phone :{}", bakChar, count, bakNum, template, strategy.getId(), strategy.getStgyName(), phone);

						sb.append(bakChar).append("分钟内已经发送过");
						sb.append(count).append("条,超过最大发送条数：");
						sb.append(bakNum).append(",不再发送");
						sb.append(", template :").append(template);
						sb.append(", strategyId :").append(strategy.getId());
						sb.append(", strategyName :").append(strategy.getStgyName());
						sb.append(", phone :").append(phone);
						responseDTO.setMsg(sb.toString());

						return responseDTO;
					}

				} else {
					redisClient.set(maxCountPerInterval, "0", Integer.parseInt(bakChar) * 60);
				}
			}

			if (redisClient.exists(warnCount)) {
				redisClient.incr(warnCount);
			} else {
				redisClient.set(warnCount, "1", warnCountWithTime * 60);
			}

			if (redisClient.exists(minWarn)) {
				sb.append(minInterval).append("分钟内已经发送过，不再发送...");
				sb.append("template:").append(template);
				sb.append(", strategyId:").append(strategy.getId());
				sb.append(", strategyName:").append(strategy.getStgyName());
				sb.append(", phone:").append(phone);
				logger.info(sb.toString());
				responseDTO.setMsg(sb.toString());

				return responseDTO;
			} else {
				String string = redisClient.get(warnCount);
				Integer count = 1;
				if (StringUtils.isNotBlank(string)) {
					count = Integer.parseInt(string);
				}

				if (count >= warnMaxCount) {

					if (StringUtils.isNumeric(bakChar) && bakNum != null) {

						if (redisClient.exists(maxCountPerInterval)) {
							redisClient.incr(maxCountPerInterval);
							String string_maxCountPerInterval = redisClient.get(maxCountPerInterval);
							Integer count_string_maxCountPerInterval = 0;
							if (StringUtils.isNumeric(string_maxCountPerInterval)) {
								count_string_maxCountPerInterval = Integer.parseInt(string_maxCountPerInterval);
							}

							if (count_string_maxCountPerInterval > bakNum) {
								logger.info("{}分钟内已经发送过{}条,超过最大发送条数：{},不再发送 , template :{} ,strategyId:{}, strategyName:{}, phone :{}", bakChar, count, bakNum, template, strategy.getId(), strategy.getStgyName(), phone);

								sb.append(bakChar).append("分钟内已经发送过");
								sb.append(count).append("条,超过最大发送条数：");
								sb.append(bakNum).append(",不再发送");
								sb.append(", template :").append(template);
								sb.append(", strategyId :").append(strategy.getId());
								sb.append(", strategyName :").append(strategy.getStgyName());
								sb.append(", phone :").append(phone);
								responseDTO.setMsg(sb.toString());

								return responseDTO;
							}
						} else {
							redisClient.set(maxCountPerInterval, "1", Integer.parseInt(bakChar) * 60);
						}
					}

					if (minInterval.intValue() > 0) {
						redisClient.set(minWarn, minWarn, minInterval * 60);
					}
					redisClient.remove(warnCount);

					sb.append("达到指标,允许发送...");
					sb.append(warnCountWithTime).append("分钟内连续");
					sb.append(count).append("次达标,且上一次发送短信在");
					sb.append(minInterval).append("分钟之前");
					sb.append(", template:").append(template);
					sb.append(", strategyId:").append(strategy.getId());
					sb.append(", strategyName:").append(strategy.getStgyName());
					sb.append(", phone:").append(phone);

					logger.info(sb.toString());
					responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
					responseDTO.setMsg(sb.toString());

					return responseDTO;
				} else {

					sb.append("未超过预设次数,不发送...");
					sb.append(" current count:").append(count);
					sb.append(", warnMaxCount:").append(warnMaxCount);
					sb.append(", template:").append(template);
					sb.append(", strategyId:").append(strategy.getId());
					sb.append(", strategyName:").append(strategy.getStgyName());
					sb.append(", phone:").append(phone);
					logger.info(sb.toString());
					responseDTO.setMsg(sb.toString());

					return responseDTO;
				}
			}
		} catch (Exception e) {
			logger.info("check error template :{} ,strategyId:{}, phone :{} ,error :{}", template, strategyId, phone, e);
			return responseDTO;
		}
	}

	public boolean check(String template, String phone, Long strategyId) {

		try {
			if (StringUtils.isBlank(template) || StringUtils.isBlank(phone) || strategyId == null) {
				return false;
			}

			Strategy strategy = getStrategyById(strategyId);

			if (strategy == null) {
				strategy = getDefault();
			}

			if (!checkWarnDay(strategy)) {
				return false;
			}

			Integer minInterval = strategy.getMinInterval();
			Integer warnCountWithTime = strategy.getWarnCountWithTime();
			Integer warnMaxCount = strategy.getWarnMaxCount();

			String bakChar = strategy.getBakChar();
			Long bakNum = strategy.getBakNum();

			String warnCount = "template_" + template + "_" + strategy.getId() + "_" + phone;
			String minWarn = "min_interval_" + template + "_" + strategy.getId() + "_" + phone;
			String maxCountPerInterval = "max_count_per_interval_" + template + "_" + strategy.getId() + "_" + phone;

			if (StringUtils.isNumeric(bakChar) && bakNum != null) {

				if (redisClient.exists(maxCountPerInterval)) {
					String string = redisClient.get(maxCountPerInterval);
					Integer count = 0;
					if (StringUtils.isNotBlank(string)) {
						count = Integer.parseInt(string);
					}
					if (count >= bakNum) {
						logger.info("{}分钟内已经发送过{}条,超过最大发送条数：{},不再发送 , template :{} ,strategyId:{}, strategyName:{}, phone :{}", bakChar, count, bakNum, template, strategy.getId(), strategy.getStgyName(), phone);
						return false;
					}

				} else {
					redisClient.set(maxCountPerInterval, "0", Integer.parseInt(bakChar) * 60);
				}
			}

			if (redisClient.exists(warnCount)) {
				redisClient.incr(warnCount);
			} else {
				redisClient.set(warnCount, "1", warnCountWithTime * 60);
			}

			if (redisClient.exists(minWarn)) {
				logger.info("{}分钟内已经发送过，不再发送...template :{} , strategyId:{} ,strategyName:{} , phone:{}", minInterval, template, strategy.getId(), strategy.getStgyName(), phone);
				return false;
			} else {
				String string = redisClient.get(warnCount);
				Integer count = 1;
				if (StringUtils.isNumeric(string)) {
					count = Integer.parseInt(string);
				}

				if (count >= warnMaxCount) {

					if (StringUtils.isNumeric(bakChar) && bakNum != null) {

						if (redisClient.exists(maxCountPerInterval)) {
							redisClient.incr(maxCountPerInterval);
							String string_maxCountPerInterval = redisClient.get(maxCountPerInterval);
							Integer count_string_maxCountPerInterval = 0;
							if (StringUtils.isNumeric(string_maxCountPerInterval)) {
								count_string_maxCountPerInterval = Integer.parseInt(string_maxCountPerInterval);
							}

							if (count_string_maxCountPerInterval > bakNum) {
								logger.info("{}分钟内已经发送过{}条,超过最大发送条数：{},不再发送 , template :{} ,strategyId:{}, strategyName:{}, phone :{}", bakChar, count, bakNum, template, strategy.getId(), strategy.getStgyName(), phone);
								return false;
							}
						} else {
							redisClient.set(maxCountPerInterval, "1", Integer.parseInt(bakChar) * 60);
						}
					}

					if (minInterval.intValue() > 0) {
						redisClient.set(minWarn, minWarn, minInterval * 60);
					}

					redisClient.remove(warnCount);
					logger.info("达到指标 ，允许发送...{}分钟内 连续{}次达标,且上一次发送短信在{}分钟之前,  template :{} , strategyId:{} ,strategyName:{} , phone:{}", warnCountWithTime, count, minInterval, template, strategy.getId(), strategy.getStgyName(), phone);
					return true;
				} else {
					logger.info("未超过预设次数,不发送... current count :{} , warnMaxCount :{} , template :{} , strategyId:{} , strategyName:{}  , phone:{}", count, warnMaxCount, template, strategy.getId(), strategy.getStgyName(), phone);
					return false;
				}
			}
		} catch (Exception e) {
			logger.info("check error template :{} ,strategyId:{}, phone :{} ,error :{}", template, strategyId, phone, e);
			return false;
		}
	}

}
