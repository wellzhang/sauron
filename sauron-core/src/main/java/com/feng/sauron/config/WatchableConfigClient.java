package com.feng.sauron.config;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.curator.SessionFailRetryLoop;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Liuyb on 2015/10/28.
 */
public class WatchableConfigClient implements CuratorListener, ConnectionStateListener {
    public static final String NAMESPACE = "CONFIG_CENTER";
    private static final String PROFILE_NAME = "config-center.properties";
    private static final String ZOOKEEPER_SERVERS = "zookeeper.servers";
    private final static Logger log = LoggerFactory.getLogger(WatchableConfigClient.class);
    private static Lock lock = new ReentrantLock();
    private static String zkServers;
    private static WatchableConfigClient configCenterClient;

    static {
        loadProperties();
    }

    private static CuratorFramework client;
    private int connectionTimeoutMs = 5000;
    private Map<String, String> localCache = new ConcurrentHashMap<String, String>(64);
    private Map<String, List<ConfigChangeWatcher>> watchers = new HashMap<String, List<ConfigChangeWatcher>>();

    private WatchableConfigClient() {
        if (client == null) {
            init();
        }
    }

    public static WatchableConfigClient getInstance() {

        try {
            lock.lock();
            if (configCenterClient == null) {
                configCenterClient = new WatchableConfigClient();
            }
            return configCenterClient;
        } finally {
            lock.unlock();
        }
    }

    private static void loadProperties() {
        Properties p = new Properties();
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROFILE_NAME);
            p.load(inputStream);
            zkServers = p.getProperty(ZOOKEEPER_SERVERS);
            if (zkServers == null) {
                throw new IllegalArgumentException("配置文件中Zookeeper服务IP为空！");
            }
        } catch (Exception e) {
            log.error("加载配置中心配置文件错误", e);
        }
    }

    /**
     * 初始化连接
     */
    private void init() {
        log.info("configer center init start!");
        initZookeeper();
        // Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
    }

    public String get(String appName, String key, String defaultValue) {

        if (appName == null || appName.length() == 0) {
            throw new IllegalArgumentException("appName can not be empty!");
        }
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("key can not be empty!");
        }
        key = (appName + "." + key);

        String value = localCache.get(key);
        if (value == null) {
            try {
                value = this.loadKey(ZKPaths.makePath(appName, key));
                log.info("[CONFIG_CENTER] load key:" + key + ",value=" + value);
            } catch (Exception e) {
                if (e instanceof KeeperException.NoNodeException) {
                    log.info("zookeeper 内无key:" + key + ",使用默认值");
                } else {
                    log.error("从zookeeper加载key:" + key + " 错误", e);
                }
            }
            if (value == null) {
                value = defaultValue;
            }
            localCache.put(key, value);
        }
        return value;
    }

    public String getAndWatch(String appName, String key, String defaultValue, ConfigChangeWatcher watcher) {

        if (appName == null || appName.length() == 0) {
            throw new IllegalArgumentException("appName can not be empty!");
        }
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("key can not be empty!");
        }
        key = (appName + "." + key);

        if (watcher != null) {
            if (watchers.containsKey(key)) {
                List<ConfigChangeWatcher> watcherList = watchers.get(key);
                if (!watcherList.contains(watcher)) {
                    watcherList.add(watcher);
                }
            } else {
                List<ConfigChangeWatcher> watcherList = new LinkedList<ConfigChangeWatcher>();
                watcherList.add(watcher);
                watchers.put(key, watcherList);
            }
        }

        String value = localCache.get(key);
        if (value == null) {
            try {
                value = this.loadKey(ZKPaths.makePath(appName, key));
                log.info("[CONFIG_CENTER] load key:" + key + ",value=" + value);
            } catch (Exception e) {
                if (e instanceof KeeperException.NoNodeException) {
                    log.info("zookeeper 内无key:" + key + ",使用默认值");
                } else {
                    log.error("从zookeeper加载key:" + key + " 错误", e);
                }
            }
            if (value == null) {
                value = defaultValue;
            }
            localCache.put(key, value);
        }
        return value;
    }

    private void initZookeeper() {
        client = CuratorFrameworkFactory.builder().namespace(NAMESPACE).connectString(zkServers).connectionTimeoutMs(connectionTimeoutMs).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

        SessionFailRetryLoop retryLoop = client.getZookeeperClient().newSessionFailRetryLoop(SessionFailRetryLoop.Mode.RETRY);

        retryLoop.start();

        client.getCuratorListenable().addListener(this);
        client.getConnectionStateListenable().addListener(this);

        client.start();

    }

    private String loadKey(final String nodePath) throws Exception {
        String value = getString(client.getData().watched().forPath(nodePath));
        log.info("加载节点----》path:" + nodePath + ",value:" + value);
        return value;

    }

    /**
     * 数据改变后更新本地缓存
     *
     * @param path
     */
    private void handleDataChange(String path) {
        try {
            String key = ZKPaths.getNodeFromPath(path);
            if (localCache.containsKey(key)) {
                String dataStr = getString(client.getData().watched().forPath(path));
                if (dataStr == null) {
                    return;
                }
                localCache.put(key, dataStr);
                try {
                    if (watchers.containsKey(key)) {
                        List<ConfigChangeWatcher> watcherList = watchers.get(key);
                        for (ConfigChangeWatcher watcher : watcherList) {
                            watcher.onValueChanged(dataStr);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                log.info("[CONFIG_CENTER] local data changed, key : " + key + ", new data :　" + dataStr);
            }
        } catch (Exception e) {
            log.error("ConfigCenterClient.handleDataChange.error", e);
        }
    }

    private String getString(byte[] data) {
        String dataStr = null;
        try {
            dataStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
            // 转码失败后使用本地编码转码
            dataStr = new String(data);
        }
        return dataStr;
    }

    @Override
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

    @Override
    public void eventReceived(CuratorFramework curatorFramework, CuratorEvent event) throws Exception {
        final WatchedEvent watchedEvent = event.getWatchedEvent();

        if (watchedEvent != null) {
            log.info("Watched event: {}" + watchedEvent);
            String path = event.getPath();
            log.info("event.path:" + path);
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                switch (watchedEvent.getType()) {
                    case NodeDataChanged:
                        this.handleDataChange(path);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public static void close() {
        if (client != null) {
            client.close();
        }
    }
}
