package com.wangwei.cs.sauron.core.client.plugin.jvm;

import java.io.File;
import java.util.HashMap;

import org.slf4j.Logger;

import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

import com.wangwei.cs.sauron.core.config.SauronConfig;
import com.wangwei.cs.sauron.core.utils.JsonUtils;
import com.wangwei.cs.sauron.core.utils.SauronLogUtils;
import com.wangwei.cs.sauron.core.utils.SauronUtils;
import com.sun.tools.attach.VirtualMachine;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年11月9日 下午3:02:03
 * 
 */
public class SystemInfoTracer {

	// public final static Logger logger = LoggerFactory.getLogger(AbstractTracerAdapterFactory.class);//当 当前系统使用的环境为 logback 的时候使用
	public final static Logger logger = SauronLogUtils.getSauronLogger();// 万能用法

	private SystemInfoTracer() {

		// 在 非 tomcat 环境下 MonitoredVmUtil的相关操作 会报 无法同步 目标vm 的错 ，异步可以暂时解决

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					Thread.sleep(300000L);// 等待 所有类 已经被装载并织入之后 再进行操作， ， MonitoredVmUtil 和 transformer 相关的类 ，会有 死锁风险， 此bug 不是每次都出现
				} catch (Exception e) {
					e.printStackTrace();
				}

				print();
			}
		});

		thread.setDaemon(true);
		thread.setName("systeminfotracer");
		thread.start();

	}

	private static class SystemInfoTracerInnerClass {
		private static final SystemInfoTracer JVM_MONITOR = new SystemInfoTracer();
	}

	public static SystemInfoTracer run() {
		return SystemInfoTracerInnerClass.JVM_MONITOR;
	}

	public void print() {

		VirtualMachine virtualmachine = null;
		try {

			MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");

			MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + SauronUtils.getPid()));

			printSystemInfo(vm, SauronUtils.getPid());

		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if (virtualmachine != null) {
					virtualmachine.detach();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void printSystemInfo(MonitoredVm vm, String pid) {

		try {

			HashMap<String, Object> hashMap = new HashMap<>();

			hashMap.put("appName", SauronConfig.getAppName());

			hashMap.put("pid", SauronUtils.getPid());

			hashMap.put("userDir", System.getProperty("user.dir"));

			hashMap.put("mainClass", MonitoredVmUtil.mainClass(vm, true));

			hashMap.put("jvmArgs", MonitoredVmUtil.jvmArgs(vm));

			// ------------------------------------------------------------

			hashMap.put("os.name", System.getProperty("os.name", ""));
			hashMap.put("os.arch", System.getProperty("os.arch", ""));
			hashMap.put("os.version", System.getProperty("os.version", ""));
			hashMap.put("file.encoding", System.getProperty("file.encoding", ""));
			hashMap.put("sun.desktop", System.getProperty("sun.desktop", ""));
			hashMap.put("number_of_processors", System.getenv("NUMBER_OF_PROCESSORS"));
			hashMap.put("user.name", System.getProperty("user.name", ""));

			hashMap.put("java.runtime.name", System.getProperty("java.runtime.name", ""));
			hashMap.put("sun.boot.library.path", System.getProperty("sun.boot.library.path", ""));
			hashMap.put("java.vm.name", System.getProperty("java.vm.name", ""));
			hashMap.put("java.runtime.version", System.getProperty("java.runtime.version", ""));
			hashMap.put("java.endorsed.dirs", System.getProperty("java.endorsed.dirs", ""));
			hashMap.put("sun.java.command", System.getProperty("sun.java.command", ""));
			hashMap.put("java.home", System.getProperty("java.home", ""));

			hashMap.put("catalina.base", System.getProperty("catalina.base", ""));
			hashMap.put("catalina.home", System.getProperty("catalina.home", ""));

			hashMap.put("agentPath", getAgentPath());

			StringBuffer json = new StringBuffer("{\"sauron\":");

			json.append(JsonUtils.toLongJSon(hashMap));

			json.append("}");

			logger.info("system|v3|" + json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static String getAgentPath() {

		String selfpath = SystemInfoTracer.class.getProtectionDomain().getCodeSource().getLocation().getPath();

		File file = new File(selfpath);

		if (file.exists()) {

			return file.getAbsolutePath();

		} else {

			return null;
		}
	}
}
