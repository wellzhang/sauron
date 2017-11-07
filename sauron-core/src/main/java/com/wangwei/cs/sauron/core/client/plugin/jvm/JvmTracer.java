package com.wangwei.cs.sauron.core.client.plugin.jvm;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wangwei.cs.sauron.core.utils.JsonUtils;
import com.wangwei.cs.sauron.core.utils.SauronUtils;
import org.slf4j.Logger;

import com.wangwei.cs.sauron.core.config.SauronConfig;
import com.wangwei.cs.sauron.core.config.WatchableConfigClient;
import com.wangwei.cs.sauron.core.utils.SauronLogUtils;
import com.sun.management.OperatingSystemMXBean;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年11月9日 下午2:02:03
 */

@SuppressWarnings("restriction")
public class JvmTracer {

    public final static Logger logger = SauronLogUtils.getSauronLogger();// 万能用法

    private static Thread thread = null;

    public static void main(String[] args) {
        try {
            JvmTracer.run();
            Thread.sleep(5000000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JvmTracer() {
        try {
            printJvmInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class  InnerClass {
        private static final JvmTracer INSTACNE = new JvmTracer();
    }

    public static JvmTracer run() {
        return InnerClass.INSTACNE;
    }

    private void printJvmInfo() {

        final String userDir = System.getProperty("user.dir");

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                try {
                    Thread.sleep(180000L);// 等待 所有类 已经被装载并织入之后 再进行操作， ， MonitoredVmUtil 和 transformer 相关的类 ，会有 死锁风险， 此bug 不是每次都出现
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                while (true) {
                    try {

                        String jvmSwitch = WatchableConfigClient.getInstance().get(SauronConfig.getAppName(), "jvm-switch", "ON");

                        if ("ON".equals(jvmSwitch)) {

                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("appName", SauronConfig.getAppName());
                            hashMap.put("userDir", userDir);
                            hashMap.put("pid", SauronUtils.getPid());

                            List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();

                            for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {

                                String string = garbageCollectorMXBean.getObjectName().toString();

                                int indexOf = string.indexOf("name=");
                                if (indexOf > 0) {
                                    string = format(string.substring(indexOf + "name=".length()).replace(" ", ""));

                                }
                                hashMap.put(string + ".count", garbageCollectorMXBean.getCollectionCount());
                                hashMap.put(string + ".time", garbageCollectorMXBean.getCollectionTime());
                            }

                            // -------------------------------------------------------------------------------------------------------------

                            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

                            hashMap.put("HeapMemoryUsage.committed", memoryMXBean.getHeapMemoryUsage().getCommitted());
                            hashMap.put("HeapMemoryUsage.max", memoryMXBean.getHeapMemoryUsage().getMax());
                            hashMap.put("HeapMemoryUsage.used", memoryMXBean.getHeapMemoryUsage().getUsed());

                            hashMap.put("NonHeapMemoryUsage.max", memoryMXBean.getNonHeapMemoryUsage().getMax());
                            hashMap.put("NonHeapMemoryUsage.used", memoryMXBean.getNonHeapMemoryUsage().getUsed());
                            hashMap.put("NonHeapMemoryUsage.committed", memoryMXBean.getNonHeapMemoryUsage().getCommitted());

                            // -------------------------------------------------------------------------------------------------------------

                            List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
                            for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
                                MemoryUsage usage = memoryPoolMXBean.getUsage();

                                String string = memoryPoolMXBean.getObjectName().toString();

                                int indexOf = string.indexOf("name=");
                                if (indexOf > 0) {
                                    string = format(string.substring(indexOf + "name=".length()).replace(" ", ""));
                                }
                                hashMap.put(string + ".max", usage.getMax());
                                hashMap.put(string + ".used", usage.getUsed());
                                hashMap.put(string + ".committed", usage.getCommitted());
                            }

                            // -------------------------------------------------------------------------------------------------------------
                            OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

                            hashMap.put("Cpu" + ".ProcessCpuTime", operatingSystemMXBean.getProcessCpuTime());
                            hashMap.put("Cpu" + ".ProcessCpuLoad", operatingSystemMXBean.getProcessCpuLoad());
                            hashMap.put("Cpu" + ".SystemCpuLoad", operatingSystemMXBean.getSystemCpuLoad());
                            hashMap.put("Cpu" + ".FreePhysicalMemorySize", operatingSystemMXBean.getFreePhysicalMemorySize());
                            hashMap.put("Cpu" + ".TotalPhysicalMemorySize", operatingSystemMXBean.getTotalPhysicalMemorySize());
                            hashMap.put("Cpu" + ".TotalSwapSpaceSize", operatingSystemMXBean.getTotalSwapSpaceSize());
                            hashMap.put("Cpu" + ".FreeSwapSpaceSize", operatingSystemMXBean.getFreeSwapSpaceSize());

                            // --------线程-----------------------------------------------------------------------------------------------------

                            hashMap.put("Thread.waiting", 0);
                            hashMap.put("Thread.timed_waiting", 0);
                            hashMap.put("Thread.runnable", 0);
                            hashMap.put("Thread.blocked", 0);
                            hashMap.put("Thread.terminated", 0);

                            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();

                            hashMap.put("Thread.Total", allStackTraces.size());

                            for (Thread thread : allStackTraces.keySet()) {

                                String state = thread.getState().toString().toLowerCase();
                                if (hashMap.containsKey("Thread." + state)) {
                                    int count = (int) hashMap.get("Thread." + state);
                                    hashMap.put("Thread." + state, count + 1);
                                }
                            }

                            // --------------------------------------------------------------------------------------------------------------
                            // ---------连接数-----------------------------------------------------------------------------------------------

                            Map<String, Integer> all = ConncUtils.getAll();

                            hashMap.putAll(all);

                            // --------------------------------------------------------------------------------------------------------------

                            StringBuffer json = new StringBuffer("{\"Sauron\":");

                            json.append(JsonUtils.toLongJSon(hashMap));

                            json.append("}");

                            logger.info("jvm|v3|" + json.toString());

                            int parseInt = 60 * 1000;

                            try {
                                String andWatch = WatchableConfigClient.getInstance().get(SauronConfig.getAppName(), "jvm-interval", "60"); // 停顿时间 单位 是s
                                parseInt = Integer.parseInt(andWatch) * 1000;
                            } catch (Exception e) {
                                e.printStackTrace();
                                parseInt = 60 * 1000;
                            }
                            Thread.sleep(parseInt);
                        } else {
                            Thread.sleep(60 * 1000);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName("jvmtracer");
        thread.start();
    }

    public static String format(String string) {

        if (string.contains("SurvivorSpace")) {
            string = "SurvivorSpace";
        } else if (string.contains("OldGen")) {
            string = "OldGen";
        } else if (string.contains("EdenSpace")) {
            string = "EdenSpace";
        } else if (string.contains("PermGen")) {
            string = "PermGen";
        } else if (string.contains("shared-ro")) {
            string = "PermGen_shared_ro";
        } else if (("Copy").equals(string) || ("G1YoungGeneration").equals(string) || ("PSScavenge").equals(string) || ("ParNew").equals(string)) {
            string = "YoungGc";
        } else if (("MarkSweepCompact").equals(string) || ("G1OldGeneration").equals(string) || ("PSMarkSweep").equals(string) || ("ConcurrentMarkSweep").equals(string)) {
            string = "FullGc";
        }

        return string;

    }

    public static void test() {

        ThreadGroup parentThread;
        for (parentThread = Thread.currentThread().getThreadGroup(); parentThread.getParent() != null; parentThread = parentThread.getParent()) {
            System.out.println(parentThread.getName() + "--");
        }

        int totalThread = parentThread.activeCount();
        System.out.println(totalThread);
    }
}
