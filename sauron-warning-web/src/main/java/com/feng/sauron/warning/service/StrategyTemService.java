package com.feng.sauron.warning.service;

import com.feng.sauron.warning.dao.StrategyTemMapper;
import com.feng.sauron.warning.domain.StrategyTem;
import com.feng.sauron.warning.util.ConfigChangeWatcher;
import com.feng.sauron.warning.util.DateUtils;
import com.feng.sauron.warning.util.WatchableConfigClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年12月31日 上午10:25:36
 * 
 */

@Service
public class StrategyTemService {

	private static final Logger logger = LoggerFactory.getLogger(StrategyTemService.class);
	private final static String nodeName = "sauron_warning_sync_1";
	private static ConcurrentHashMap<String, StrategyTem> sMap = null;
	private static String format = DateUtils.format(new Date()) + "." + new Random().nextInt(1000);

	static {
		try {
			defaultWatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Resource
	StrategyTemMapper strategyTemMapper;

	public static void removeAllStrategyTemCache() {
		sMap = null;
		logger.info("清除 StrategyTemCache ... ");
	}

	public static void defaultWatch() {

		WatchableConfigClient.getInstance().create(nodeName, "sauron.warning.strategyTem", "default");
		WatchableConfigClient.getInstance().getAndWatch(nodeName, "sauron.warning.strategyTem", "default", new ConfigChangeWatcher() {

			@Override
			public void onValueChanged(String newVal) {
				logger.info("检测到其他server 更新了数据库 , 开始刷新缓存strategy");
				if (!format.equalsIgnoreCase(newVal)) {
					removeAllStrategyTemCache();
				} else {
					logger.info("此次更新strategyTem 来自 服务本身 ,已经刷新过,忽略...");
				}
			}
		});
	}

	public static void watch() {

		try {
			String format_now = DateUtils.format(new Date()) + "." + new Random().nextInt(1000);
			WatchableConfigClient.getInstance().set(nodeName, "sauron.warning.strategyTem", format_now);
			format = format_now;
		} catch (Exception e) {
		}
	}

	public StrategyTem selectBytemplate(String template) {

		if (sMap == null) {
			sMap = new ConcurrentHashMap<>();
			logger.info("strategyTem缓存为空  开始拉取数据库...");
			List<StrategyTem> selectAll = strategyTemMapper.selectAll();
			for (StrategyTem strategyTem : selectAll) {
				sMap.put(strategyTem.getTemplate(), strategyTem);
			}
			logger.info("从数据库拉取strategyTem结束...");
		}
		return sMap.get(template);
	}

	public List<StrategyTem> findByPager(int pageNo, int pageSize, String template, long strategyId, String channelId) {
		pageNo = pageNo - 1 > 0 ? pageNo - 1 : 0;
		return strategyTemMapper.selectByPager(pageNo * pageSize, pageSize, template, strategyId, channelId);
	}

	public int selectCount(String template, long strategyId, String channelId) {
		return strategyTemMapper.selectCount(template, strategyId, channelId);
	}

	public void addNewStrategyTem(StrategyTem strategyTem) {
		strategyTemMapper.insertSelective(strategyTem);
	}

	public void removeByTemplate(String template) {
		strategyTemMapper.deleteByPrimaryKey(template);
	}

	public StrategyTem findByTemplate(String template) {
		return strategyTemMapper.selectByPrimaryKey(template);
	}

	public void updateStrategyTem(StrategyTem strategyTem) {
		strategyTemMapper.updateByPrimaryKeySelective(strategyTem);
	}
}
