package com.wangwei.cs.sauron.core.client.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wangwei.cs.sauron.core.config.ConfigChangeWatcher;
import com.wangwei.cs.sauron.core.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wangwei.cs.sauron.core.config.SauronConfig;
import com.wangwei.cs.sauron.core.config.WatchableConfigClient;

/**
 * Created by Liuyb on 2015/10/28.
 */
public class SauronGlobalContext {
    private final static Logger logger = LoggerFactory.getLogger(SauronGlobalContext.class);
    private static boolean TRACE_SWITCH = true;
    private static Set<String> ENABLED_TRACER = new HashSet<String>();
    private static Map<String, HashSet<String>> METHOD_TO_TRACE = new HashMap<String, HashSet<String>>();
    private static boolean IS_SAMPLED = true;
    private static ReadWriteLock SWITCH_lock = new ReentrantReadWriteLock();
    private static ReadWriteLock TRACER_lock = new ReentrantReadWriteLock();
    private static ReadWriteLock METHOD_lock = new ReentrantReadWriteLock();
    private static ReadWriteLock SAMPRATE_lock = new ReentrantReadWriteLock();

    static {
        initializeGlobalContext();
    }

    private static void loadSwitchConfig(String tracerSwitch) {
        tracerSwitch = tracerSwitch.trim().toUpperCase();
        SWITCH_lock.writeLock().lock();
        if (tracerSwitch.equals(Constants.SAURON_SWITCH_STATUS_ON)) {
            TRACE_SWITCH = true;
        } else if (tracerSwitch.equals(Constants.SAURON_SWITCH_STATUS_OFF)) {
            TRACE_SWITCH = false;
        } else {// default value
            TRACE_SWITCH = true;
        }
        SWITCH_lock.writeLock().unlock();
    }

    private static void loadTracersConfig(String enableTracer) {
        ENABLED_TRACER = new HashSet<String>();
        TRACER_lock.writeLock().lock();
        if (!enableTracer.trim().isEmpty()) {
            String[] tracersArray = enableTracer.split(Constants.TRACER_SPLITER);

            for (String tracerName : tracersArray) {
                ENABLED_TRACER.add(tracerName);
            }
        }
        TRACER_lock.writeLock().unlock();
    }

    private static void loadMethodsConfig(String enableMethods) {
        METHOD_TO_TRACE = new HashMap<String, HashSet<String>>();
        METHOD_lock.writeLock().lock();
        if (!enableMethods.trim().isEmpty()) {
            // 拆分出多个TraceClass
            String[] methodString = enableMethods.trim().split(Constants.CLASS_SPLITER);
            // 表达式对象
            Pattern p = Pattern.compile(Constants.METHOD_RESOLVE_REGEXP);
            for (String method : methodString) {
                // 创建 Matcher 对象
                Matcher m = p.matcher(method);
                // 是否找到匹配
                boolean found = m.find();
                if (found) {
                    String clazzName = m.group(1);
                    String methodName = m.group(3);
                    if (clazzName.isEmpty()) {
                        logger.debug("处理描述符时出错，跳过该条转换");
                        continue;
                    }
                    HashSet<String> methodNameSet = new HashSet<String>();
                    if (methodName != null) {
                        String[] methods = methodName.split("Constants.METHOD_SPLITER");
                        for (String oneMethodName : methods) {
                            methodNameSet.add(clazzName + "." + oneMethodName);
                        }
                        METHOD_TO_TRACE.put(clazzName, methodNameSet);
                    } else {

                        METHOD_TO_TRACE.put(clazzName, null);
                    }
                }
            }
        }
        METHOD_lock.writeLock().unlock();
    }

    private static void loadSamplingRateConfig(String samplingRate) {
        int smplRate = Integer.valueOf(samplingRate);
        SAMPRATE_lock.writeLock().lock();
        if (smplRate > 100 || smplRate < 0) {
            smplRate = 100;
            logger.debug("Illegal Value Presents,RESET Sampling Rate To Default!");
        }

        long random = Math.round(Math.random() * 100 + 1);
        if (random <= smplRate) {
            IS_SAMPLED = true;
        } else {
            IS_SAMPLED = false;
        }
        SAMPRATE_lock.writeLock().unlock();
    }

    public static void initializeGlobalContext() {
        String methods = WatchableConfigClient.getInstance().getAndWatch(SauronConfig.getAppName(), Constants.SAURON_METHODNAME, "", new ConfigChangeWatcher() {
            @Override
            public void onValueChanged(String newVal) {
                loadMethodsConfig(newVal);
            }
        });
        String tracers = WatchableConfigClient.getInstance().getAndWatch(SauronConfig.getAppName(), Constants.SAURON_TRACERS, Constants.SAURON_DEFAULT_SAURON_TRACERS, new ConfigChangeWatcher() {
            @Override
            public void onValueChanged(String newVal) {
                loadTracersConfig(newVal);
            }
        });
        String samprate = WatchableConfigClient.getInstance().getAndWatch(SauronConfig.getAppName(), Constants.SAURON_SAMPLING_RATE, Constants.SAURON_DEFAULT_SAMPLING_RATE, new ConfigChangeWatcher() {
            @Override
            public void onValueChanged(String newVal) {
                loadSamplingRateConfig(newVal);
            }
        });
        String allSwitch = WatchableConfigClient.getInstance().getAndWatch(SauronConfig.getAppName(), Constants.SAURON_SWITCH, Constants.SAURON_SWITCH_STATUS_ON, new ConfigChangeWatcher() {
            @Override
            public void onValueChanged(String newVal) {
                loadSwitchConfig(newVal);
            }
        });
        firstLoadConfiguration(allSwitch, tracers, samprate, methods);
    }

    public static void firstLoadConfiguration(String allSwitch, String tracers, String samprate, String methods) {
        loadMethodsConfig(methods);
        loadTracersConfig(tracers);
        loadSamplingRateConfig(samprate);
        loadSwitchConfig(allSwitch);
    }

    public static boolean isSwitchedOn() {
        SWITCH_lock.readLock().lock();
        boolean SWITCH = TRACE_SWITCH;
        SWITCH_lock.readLock().unlock();
        return SWITCH;
    }

    public static Set<String> getEnabledTracer() {
        return ENABLED_TRACER;
    }

    public static Map<String, HashSet<String>> getMethodToTrace() {
        return METHOD_TO_TRACE;
    }

    public static boolean isSampled() {
        SAMPRATE_lock.readLock().lock();
        boolean isSampled = IS_SAMPLED;
        SAMPRATE_lock.readLock().unlock();
        return isSampled;
    }

}
