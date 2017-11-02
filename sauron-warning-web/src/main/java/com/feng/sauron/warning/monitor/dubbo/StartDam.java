package com.feng.sauron.warning.monitor.dubbo;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.feng.sauron.warning.domain.ZookeeperIps;
import com.feng.sauron.warning.service.AlarmService;
import com.feng.sauron.warning.service.base.ContactsService;
import com.feng.sauron.warning.service.base.DubboRulesService;
import com.feng.sauron.warning.service.base.ZookeeperIpsService;
import com.feng.sauron.warning.task.LeaderSelectionClient;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2016年5月16日 下午4:47:09
 * 
 */

public class StartDam {

	@Autowired
	ZookeeperIpsService zookeeperIpsService;

	@Resource
	LeaderSelectionClient leaderSelectionClient;

	@Resource
	DubboRulesService dubboRulesService;

	@Resource
	ContactsService contactsService;

	@Resource
	AlarmService alarmService;

	private static ConcurrentHashMap<String, ConcurrentHashMap<String, Set<String>>> zk_app_iport = new ConcurrentHashMap<>();

	private static ConcurrentHashMap<String, Thread> zk_thread = new ConcurrentHashMap<>();

	private static ConcurrentHashMap<String, DubboAliveMonitor> zk_monitor = new ConcurrentHashMap<>();

	private StartDam() {
	}

	@SuppressWarnings("unused")
	private void init() {

		try {

			List<ZookeeperIps> zkIps = zookeeperIpsService.selectAll();

			for (ZookeeperIps zookeeperIps : zkIps) {
				createThread(zookeeperIps);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createThread(final ZookeeperIps zookeeperIps) {

		try {

			if (zookeeperIps == null) {
				return;
			}

			if (zk_thread.containsKey(zookeeperIps.getZkIp())) {
				return;
			}

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {

					DubboAliveMonitor dubboAliveMonitor = new DubboAliveMonitor(zookeeperIps.getZkIp(), dubboRulesService, contactsService, leaderSelectionClient, alarmService);

					dubboAliveMonitor.initData(zookeeperIps.getId());

					zk_app_iport.put(zookeeperIps.getZkIp(), dubboAliveMonitor.getApp_iport());

					zk_monitor.put(zookeeperIps.getZkIp(), dubboAliveMonitor);

					dubboAliveMonitor.start(zookeeperIps.getId());

				}
			});

			thread.setDaemon(true);
			thread.start();

			zk_thread.put(zookeeperIps.getZkIp(), thread);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroyThread(final ZookeeperIps zookeeperIps) {

		if (zookeeperIps == null || zookeeperIps.getZkIp() == null || zookeeperIps.getZkIp().length() == 0) {
			return;
		}
		try {

			Thread thread2 = zk_thread.remove(zookeeperIps.getZkIp());
			if (thread2 != null) {
				thread2.interrupt();
				thread2 = null;
			}

			DubboAliveMonitor dubboAliveMonitor = zk_monitor.remove(zookeeperIps.getZkIp());
			CloseableUtils.closeQuietly(dubboAliveMonitor.getClient());
			dubboAliveMonitor = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void close() {

		try {

			System.err.println("批量关闭zk连接..");
			for (String zkString : zk_monitor.keySet()) {

				DubboAliveMonitor dubboAliveMonitor = zk_monitor.get(zkString);
				CuratorFramework client = dubboAliveMonitor.getClient();
				CloseableUtils.closeQuietly(client);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean getZkAppisAlive(String zkIp, String applicationName) {

		if (zk_app_iport.size() == 0) {
			return false;
		}

		if (zkIp == null || applicationName == null || zkIp.length() == 0 || applicationName.length() == 0) {
			return false;
		}

		ConcurrentHashMap<String, Set<String>> concurrentHashMap = zk_app_iport.get(zkIp);
		if (concurrentHashMap != null) {
			Set<String> set = concurrentHashMap.get(applicationName);
			if (set != null && set.size() > 0) {
				return true;
			}
		}
		return false;
	}

}
