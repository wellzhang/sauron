package com.feng.sauron.warning.monitor.dubbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.SessionFailRetryLoop;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.URL;
import com.feng.sauron.warning.domain.Contact;
import com.feng.sauron.warning.domain.DubboRules;
import com.feng.sauron.warning.domain.RuleReceiversKey;
import com.feng.sauron.warning.service.AlarmService;
import com.feng.sauron.warning.service.base.ContactsService;
import com.feng.sauron.warning.service.base.DubboRulesService;
import com.feng.sauron.warning.service.base.RuleReceiverService;
import com.feng.sauron.warning.task.LeaderSelectionClient;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年5月11日 下午6:38:02
 * 
 */

public class DubboAliveMonitor implements CuratorListener, ConnectionStateListener {

	private DubboRulesService dubboRulesService;

	private ContactsService contactsService;

	private RuleReceiverService ruleReceiverService;

	private LeaderSelectionClient leaderSelectionClient;

	private AlarmService alarmService;

	private final static Logger log = LoggerFactory.getLogger(DubboAliveMonitor.class);

	private CuratorFramework client;
	private ConcurrentHashMap<String, Set<String>> server_iport = null;
	private ConcurrentHashMap<String, Set<String>> app_server = null;
	private ConcurrentHashMap<String, Set<String>> app_iport = null;

	public static final String LOSE = "LOSE";
	public static final String ADD = "ADD";
	public static final String LOSE_ALL = "LOSE_ALL";

	private static final String APPLICATION = "application";
	private static final String SERVICE_STRING = "interface";
	public static final String CHAR_STRING = "#";

	private static final String DUBBO_STRING = "/dubbo";
	private static final String PROVIDERS_STRING = "/providers";

	public ConcurrentHashMap<String, List<ChildrenChangeWatcher>> childrenChangeWatchMap = new ConcurrentHashMap<>();

	private static int connectionTimeoutMs = 5000;

	@SuppressWarnings("unused")
	private DubboAliveMonitor() {
	}

	public CuratorFramework getClient() {
		return client;
	}

	public ConcurrentHashMap<String, Set<String>> getApp_iport() {
		return app_iport;
	}

	public DubboAliveMonitor(String zkString, DubboRulesService dubboRulesService, ContactsService contactsService, LeaderSelectionClient leaderSelectionClient, AlarmService alarmService) {

		this.client = initZookeeper(zkString);
		this.dubboRulesService = dubboRulesService;
		this.contactsService = contactsService;
		this.leaderSelectionClient = leaderSelectionClient;
		this.alarmService = alarmService;

		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
	}

	public void start(Long zkid) {

		while (true) {
			try {

				Thread.sleep(1000 * 30);

				System.out.println("开始扫描待发送消息...");

				for (String app : app_server.keySet()) {

					// Set<String> set = app_server.get(app);

					String appString = app.substring(app.indexOf(CHAR_STRING) + 1, app.lastIndexOf(CHAR_STRING));

					String msg = app + CHAR_STRING + app_iport.get(appString);

					sendMsg(msg, zkid);

					app_server.remove(app);
				}

				System.out.println("扫描结束...");

			} catch (Exception e) {
				if (e instanceof InterruptedException) {
					System.err.println("线程被打断...终止zk节点监控，zkid：" + zkid);
					break;
				} else {
					e.printStackTrace();
				}
			}
		}
	}

	public void initData(final Long zkid) {

		try {

			List<String> childrenAndWatch = getChildrenAndWatch(DUBBO_STRING, new ChildrenChangeWatcher() {

				@Override
				public void onChildrenChanged(String path) {

					try {
						client.getChildren().watched().forPath(path);
					} catch (Exception e) {
						e.printStackTrace();
					}

					initChildrenData(getChildren(DUBBO_STRING), zkid);
				}

				@SuppressWarnings("deprecation")
				@Override
				public Boolean isAlive() {
					return client.isStarted();
				}
			});

			initChildrenData(childrenAndWatch, zkid);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void initChildrenData(List<String> children, final Long zkid) {

		server_iport = new ConcurrentHashMap<String, Set<String>>();

		app_iport = new ConcurrentHashMap<String, Set<String>>();

		app_server = new ConcurrentHashMap<String, Set<String>>();

		for (String string : children) {

			String pathString = DUBBO_STRING + "/" + string + PROVIDERS_STRING;

			List<String> children2 = getChildrenAndWatch(pathString, new ChildrenChangeWatcher() {

				@Override
				public void onChildrenChanged(String path) {

					try {
						client.getChildren().watched().forPath(path);
					} catch (Exception e) {
						e.printStackTrace();
					}

					ChildrenNodeChange(path, zkid);
				}

				@SuppressWarnings("deprecation")
				@Override
				public Boolean isAlive() {
					return client.isStarted();
				}
			});

			if (children2.size() == 0) {
				server_iport.put(string, new HashSet<String>());
				continue;
			}

			for (String string2 : children2) {
				URL valueOf = URL.valueOf(URL.decode(string2));
				String application = valueOf.getParameter(APPLICATION);
				String service = valueOf.getParameter(SERVICE_STRING);

				String iport = valueOf.getIp() + ":" + valueOf.getPort();

				if (server_iport.containsKey(service)) {
					server_iport.get(service).add(application + CHAR_STRING + iport);
				} else {
					HashSet<String> hashSet = new HashSet<>();
					hashSet.add(application + CHAR_STRING + iport);
					server_iport.put(service, hashSet);
				}

				if (app_iport.containsKey(application)) {
					app_iport.get(application).add(iport);
				} else {
					HashSet<String> hashSet = new HashSet<>();
					hashSet.add(iport);
					app_iport.put(application, hashSet);
				}
			}
		}
	}

	private synchronized void ChildrenNodeChange(String path, Long zkid) {

		try {
			List<String> childrenAndWatch = getChildren(path);

			String service = path.replace(DUBBO_STRING + "/", "").replace(PROVIDERS_STRING, "");

			Set<String> set = server_iport.get(service);

			if (set == null) {
				set = new HashSet<>();
			}

			if (childrenAndWatch == null || childrenAndWatch.size() == 0) {

				if (set.size() > 0) {

					StringBuilder sb = new StringBuilder(LOSE_ALL + CHAR_STRING);
					String appString = set.iterator().next().split(CHAR_STRING)[0];

					sb.append(appString).append(CHAR_STRING).append(set.toString().replace(CHAR_STRING, "|")).append(CHAR_STRING).append(service);

					sendMsg(sb.toString(), zkid);

				}
				server_iport.remove(service);
				return;
			}

			HashSet<String> tmpSet = new HashSet<>();

			for (String string : childrenAndWatch) {
				URL tmpUrl = URL.valueOf(URL.decode(string));
				String applicationTmp = tmpUrl.getParameter(APPLICATION);
				String iportTmp = tmpUrl.getIp() + ":" + tmpUrl.getPort();
				tmpSet.add(applicationTmp + CHAR_STRING + iportTmp);
			}

			String shortService = service.substring(service.lastIndexOf(".") + 1);

			for (String string : set) {
				if (!tmpSet.contains(string)) {

					if (app_server.containsKey(LOSE + CHAR_STRING + string)) {
						app_server.get(LOSE + CHAR_STRING + string).add(shortService);
					} else {
						HashSet<String> hashSet = new HashSet<>();
						hashSet.add(shortService);
						app_server.put(LOSE + CHAR_STRING + string, hashSet);
					}

					String[] split = string.split(CHAR_STRING);
					String appString = split[0];
					String iportString = split[1];

					if (app_iport.containsKey(appString)) {
						app_iport.get(appString).remove(iportString);
					}

					// System.out.println("丢失节点: " + string + " 目前节点数：" + tmpSet.size() + tmpSet.toString());
				}
			}
			for (String string : tmpSet) {
				if (!set.contains(string)) {

					if (app_server.containsKey(ADD + CHAR_STRING + string)) {
						app_server.get(ADD + CHAR_STRING + string).add(shortService);
					} else {
						HashSet<String> hashSet = new HashSet<>();
						hashSet.add(shortService);
						app_server.put(ADD + CHAR_STRING + string, hashSet);
					}

					String[] split = string.split(CHAR_STRING);
					String appString = split[0];
					String iportString = split[1];

					if (app_iport.containsKey(appString)) {
						app_iport.get(appString).add(iportString);
					} else {
						HashSet<String> hashSet = new HashSet<>();
						hashSet.add(iportString);
						app_iport.put(appString, hashSet);
					}

					// System.out.println("新增节点: " + string + " 目前节点数：" + tmpSet.size() + tmpSet.toString());
				}
			}
			server_iport.put(service, tmpSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendMsg(String msg, Long zkid) {

		try {

			if (msg == null || msg.length() == 0) {
				log.info("msg is blank , exit...");
				return;
			}
			log.info(msg);

			if (!leaderSelectionClient.hasLeaderShip()) {
				log.info("当前非主节点.. 不发送");
				return;
			}

			String[] split = msg.split(CHAR_STRING);

			String app = split[1];

			DubboRules dubboRules = dubboRulesService.selectByzkid(zkid, app);

			if (dubboRules == null) {
				return;
			}

			List<RuleReceiversKey> findReceiversByRule = ruleReceiverService.findReceiversByRule(dubboRules.getId(), RuleReceiversKey.Type.Dubbo.val());
			List<String> phoneNumByAppli = new ArrayList<>();

			for (RuleReceiversKey ruleReceiversKey : findReceiversByRule) {
				Long contactId = ruleReceiversKey.getContactId();
				Contact findContactById = contactsService.findContactById(contactId);
				if (findContactById != null) {
					String mobile = findContactById.getMobile();
					phoneNumByAppli.add(mobile);
				}
			}

			String buildSmsText_dubboAliveMonitor = AlarmService.buildSmsText_dubboAliveMonitor(split);

			alarmService.sendSms(phoneNumByAppli, buildSmsText_dubboAliveMonitor, dubboRules.getTemplate());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private CuratorFramework initZookeeper(String zkServers) {

		try {
			CuratorFramework client = CuratorFrameworkFactory.builder().connectString(zkServers).connectionTimeoutMs(connectionTimeoutMs).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
			SessionFailRetryLoop retryLoop = client.getZookeeperClient().newSessionFailRetryLoop(SessionFailRetryLoop.Mode.RETRY);
			retryLoop.start();
			client.getCuratorListenable().addListener(this);
			client.getConnectionStateListenable().addListener(this);
			client.start();
			return client;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

		switch (connectionState) {
		case CONNECTED:
			log.info("zookeeper CONNECTED");
			break;
		case SUSPENDED:
			log.info("zookeeper SUSPENDED");
			break;
		case RECONNECTED:
			log.info("zookeeper RECONNECTED");
			break;
		case LOST:
			log.info("zookeeper LOST");
			break;
		default:
			break;
		}
	}

	public void eventReceived(CuratorFramework curatorFramework, CuratorEvent event) throws Exception {
		final WatchedEvent watchedEvent = event.getWatchedEvent();

		if (watchedEvent != null) {

			log.info("Watched event: {}" + watchedEvent);
			String path = event.getPath();
			if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
				switch (watchedEvent.getType()) {
				case NodeChildrenChanged:
					this.handleChildrenNodeChange(path);
					break;
				default:
					break;
				}
			}
		}
	}

	private void handleChildrenNodeChange(String path) {

		try {

			List<ChildrenChangeWatcher> list = childrenChangeWatchMap.get(path);

			if (list == null || list.size() == 0) {
				return;
			}

			List<ChildrenChangeWatcher> deadList = new ArrayList<>();

			for (int i = 0; i < list.size(); i++) {
				ChildrenChangeWatcher childrenChangeWatcher = list.get(i);

				Boolean alive = childrenChangeWatcher.isAlive();
				if (alive) {
					childrenChangeWatcher.onChildrenChanged(path);
				} else {
					deadList.add(childrenChangeWatcher);
				}
			}
			list.removeAll(deadList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ShutdownHook extends Thread {
		public void run() {
			CloseableUtils.closeQuietly(client);
		}
	}

	public List<String> getChildren(String path) {
		return getChildrenAndWatch(path, null);
	}

	public List<String> getChildrenAndWatch(String path, ChildrenChangeWatcher changeWatcher) {

		if (path == null || "".equals(path) || client == null) {
			return Collections.emptyList();
		}

		try {
			GetChildrenBuilder children = client.getChildren();

			if (changeWatcher != null) {

				if (childrenChangeWatchMap.containsKey(path)) {
					childrenChangeWatchMap.get(path).add(changeWatcher);
				} else {
					List<ChildrenChangeWatcher> arrayList = new ArrayList<>();
					arrayList.add(changeWatcher);
					childrenChangeWatchMap.put(path, arrayList);
				}
			}
			return children.watched().forPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

}
