//package com.feng.sauron.test.domain;
//
//import java.io.File;
//import java.lang.management.ClassLoadingMXBean;
//import java.lang.management.CompilationMXBean;
//import java.lang.management.GarbageCollectorMXBean;
//import java.lang.management.ManagementFactory;
//import java.lang.management.MemoryMXBean;
//import java.lang.management.MemoryManagerMXBean;
//import java.lang.management.MemoryPoolMXBean;
//import java.lang.management.MemoryUsage;
//import java.lang.management.OperatingSystemMXBean;
//import java.lang.management.RuntimeMXBean;
//import java.lang.management.ThreadMXBean;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//import javassist.ClassPool;
//import javassist.CtClass;
//
//import javax.management.MBeanServerConnection;
//import javax.management.ObjectName;
//import javax.management.remote.JMXConnector;
//import javax.management.remote.JMXConnectorFactory;
//import javax.management.remote.JMXServiceURL;
//
//import com.sun.tools.attach.VirtualMachine;
//
//public class TestSist {
//
//	public static void main(String[] args) throws Exception {
//
//		String name = ManagementFactory.getRuntimeMXBean().getName();
//
//		String pid = name.split("@")[0];
//
//		System.out.println("本程序 pid = " + pid);
//
//		String newPid = "";
//
//		if (args == null || args.length == 0) {
//			System.out.println("+++++++++++       未输入pid  将使用自身pid        +++++++++");
//			newPid = pid;
//		} else {
//			newPid = args[0];
//		}
//
//		
//		newPid = "10328";
//		
//		System.out.println("附着的 Pid :" + newPid);
//
//		VirtualMachine virtualmachine = VirtualMachine.attach(newPid);
//
//		// 让JVM加载jmx Agent，后续 考虑 Java Instrutment
//		String javaHome = virtualmachine.getSystemProperties().getProperty("java.home");
//		String jmxAgent = javaHome + File.separator + "lib" + File.separator + "management-agent.jar";
//
//		virtualmachine.loadAgent(jmxAgent, "com.sun.management.jmxremote");
//
//		String address = virtualmachine.getAgentProperties().get("com.sun.management.jmxremote.localConnectorAddress").toString();
//
//		// Detach 拿到地址 即退出附着
//		virtualmachine.detach();
//
//		JMXServiceURL url = new JMXServiceURL(address);
//
//		JMXConnector connector = JMXConnectorFactory.connect(url);
//
//		final MBeanServerConnection mBeanServerConnection = connector.getMBeanServerConnection();
//
//		Set<ObjectName> queryNames = mBeanServerConnection.queryNames(null, null);
//
//		for (ObjectName objectName : queryNames) {
//			System.out.println(objectName.getCanonicalName());
//		}
//		
//		
//		//基于开发商    动态存在的 mbean
//
//		Set<ObjectName> querynames_garbage_collector = mBeanServerConnection.queryNames(ObjectName.getInstance(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=*"), null);
//		for (ObjectName objectName : querynames_garbage_collector) {
//			System.out.println("###################################################################");
//			GarbageCollectorMXBean garbageCollectorMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), objectName.getCanonicalName(), GarbageCollectorMXBean.class);
//			getRuntimeInfo(garbageCollectorMXBean);
//		}
//
//		Set<ObjectName> querynames_memory_manager = mBeanServerConnection.queryNames(ObjectName.getInstance(ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE + ",name=*"), null);
//		for (ObjectName objectName : querynames_memory_manager) {
//			System.out.println("###################################################################");
//			MemoryManagerMXBean memoryManagerMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), objectName.getCanonicalName(), MemoryManagerMXBean.class);
//			getRuntimeInfo(memoryManagerMXBean);
//		}
//
//		Set<ObjectName> querynames_memory_pool = mBeanServerConnection.queryNames(ObjectName.getInstance(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=*"), null);
//		for (ObjectName objectName : querynames_memory_pool) {
//			System.out.println("###################################################################");
//			MemoryPoolMXBean memoryPoolMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), objectName.getCanonicalName(), MemoryPoolMXBean.class);
//			getMemoryPoolInfo(memoryPoolMXBean);
//		}
//
//		Thread thread = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//
//				while (true) {
//
//					try {
//						System.out.println("****************");
//						Thread.sleep(10000L);
//						long fullGC = getFullGC(mBeanServerConnection);
//						System.out.println("fullGC : " + fullGC);
//
//						long youngGC = getYoungGC(mBeanServerConnection);
//						System.out.println("youngGC : " + youngGC);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//				}
//
//			}
//		});
//
//		thread.start();
//		
//		
//		//基于平台的   有固定的  object name   
//
//		RuntimeMXBean runtimeMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
//		getRuntimeInfo(runtimeMXBean);
//
//		ClassLoadingMXBean classLoadingMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ManagementFactory.CLASS_LOADING_MXBEAN_NAME, ClassLoadingMXBean.class);
//		getClassLoadingInfo(classLoadingMXBean);
//
//		CompilationMXBean compilationMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ManagementFactory.COMPILATION_MXBEAN_NAME, CompilationMXBean.class);
//		getCompilationInfo(compilationMXBean);
//
//		MemoryMXBean memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
//		getMemoryInfo(memoryMXBean);
//
//		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
//		getOperatingSystemInfo(operatingSystemMXBean);
//
//		ThreadMXBean threadMXBean = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
//		getThreadInfo(threadMXBean);
//
//		Thread.sleep(100000000);
//	}
//
//	public static long getYoungGC(MBeanServerConnection mbeanServer) {
//		try {
//			ObjectName objectName;
//			if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=ParNew"))) {
//				objectName = new ObjectName("java.lang:type=GarbageCollector,name=ParNew");
//			} else if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=Copy"))) {
//				objectName = new ObjectName("java.lang:type=GarbageCollector,name=Copy");
//			} else {
//				objectName = new ObjectName("java.lang:type=GarbageCollector,name=PS Scavenge");
//			}
//			return (Long) mbeanServer.getAttribute(objectName, "CollectionCount");
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	public static long getFullGC(MBeanServerConnection mbeanServer) {
//		try {
//			ObjectName objectName;
//			if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=ConcurrentMarkSweep"))) {
//				objectName = new ObjectName("java.lang:type=GarbageCollector,name=ConcurrentMarkSweep");
//			} else if (mbeanServer.isRegistered(new ObjectName("java.lang:type=GarbageCollector,name=MarkSweepCompact"))) {
//				objectName = new ObjectName("java.lang:type=GarbageCollector,name=MarkSweepCompact");
//			} else {
//				objectName = new ObjectName("java.lang:type=GarbageCollector,name=PS MarkSweep");
//			}
//			return (Long) mbeanServer.getAttribute(objectName, "CollectionCount");
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	public static void getRuntimeInfo(Object mxBean) throws Exception {
//
//		System.out.println("-------------------------------------------------------------------");
//		Method[] declaredMethods = mxBean.getClass().getDeclaredMethods();
//
//		for (int i = 0; i < declaredMethods.length; i++) {
//			Method method = declaredMethods[i];
//			method.setAccessible(true);
//			if (method.isAccessible()) {
//				if (method.getName().toString().startsWith("get") && !"getClass".equals(method.getName().toString()) && method.getParameterTypes().length == 0) {
//					try {
//						Object invoke = method.invoke(mxBean, new Object[0]);
//						System.out.println(method.getName().toString() + " :: " + invoke);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//	}
//
//	public static void getClassLoadingInfo(ClassLoadingMXBean classLoadingMXBean) throws Exception {
//		getRuntimeInfo(classLoadingMXBean);
//	}
//
//	public static void getCompilationInfo(CompilationMXBean compilationMXBean) throws Exception {
//		getRuntimeInfo(compilationMXBean);
//	}
//
//	public static void getGarbageCollectorInfo(GarbageCollectorMXBean garbageCollectorMXBean) throws Exception {
//		getRuntimeInfo(garbageCollectorMXBean);
//	}
//
//	public static void getMemoryManagerInfo(MemoryManagerMXBean memoryManagerMXBean) throws Exception {
//		getRuntimeInfo(memoryManagerMXBean);
//	}
//
//	public static void getMemoryPoolInfo(MemoryPoolMXBean memoryPoolMXBean) throws Exception {
//
//		if (memoryPoolMXBean.isUsageThresholdSupported()) {
//			long usageThreshold = memoryPoolMXBean.getUsageThreshold();
//			long usageThresholdCount = memoryPoolMXBean.getUsageThresholdCount();
//			System.out.println("getUsageThreshold() :: " + usageThreshold);
//			System.out.println("getUsageThresholdCount() :: " + usageThresholdCount);
//		}
//
//		Method[] declaredMethods = memoryPoolMXBean.getClass().getDeclaredMethods();
//
//		for (int i = 0; i < declaredMethods.length; i++) {
//			Method method = declaredMethods[i];
//			method.setAccessible(true);
//			if (method.isAccessible()) {
//				if (method.getName().toString().startsWith("get") && method.getParameterTypes().length == 0) {
//
//					if (!memoryPoolMXBean.isUsageThresholdSupported() && ("getUsageThreshold".equals(method.getName().toString()) || "getUsageThresholdCount".equals(method.getName().toString()))) {
//						continue;
//					}
//
//					if (!memoryPoolMXBean.isCollectionUsageThresholdSupported() && ("getCollectionUsageThreshold".equals(method.getName().toString()) || "getCollectionUsageThresholdCount".equals(method.getName().toString()))) {
//						continue;
//					}
//
//					try {
//						Object invoke = method.invoke(memoryPoolMXBean, new Object[0]);
//						System.out.println(method.getName().toString() + " :: " + invoke);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//
//	}
//
//	public static void getMemoryInfo(MemoryMXBean memoryMXBean) throws Exception {
//
//		System.out.println("------------------------------------------------------");
//
//		MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
//		long committed = heapMemoryUsage.getCommitted();
//		long init = heapMemoryUsage.getInit();
//		long max = heapMemoryUsage.getMax();
//		long used = heapMemoryUsage.getUsed();
//
//		System.out.print("堆内存初始化大小:" + init / 1024 + ",最大堆内存:" + max / 1024 + "KB,当前分配量:" + committed / 1024 + "KB,当前使用量:" + used / 1024 + "KB,");
//		System.out.println("堆内存使用率:" + (int) used * 100 / committed + "%");// 堆使用率
//
//		MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
//		long committed_s = nonHeapMemoryUsage.getCommitted();
//		long init_s = nonHeapMemoryUsage.getInit();
//		long max_s = nonHeapMemoryUsage.getMax();
//		long used_s = nonHeapMemoryUsage.getUsed();
//
//		System.out.print("栈内存初始化大小:" + init_s / 1024 + ",最大栈内存:" + max_s / 1024 + "KB,当前分配量:" + committed_s / 1024 + "KB,当前使用量:" + used_s / 1024 + "KB,");
//		System.out.println("栈内存使用率:" + (int) used_s * 100 / committed_s + "%");// 栈使用率
//
//		getRuntimeInfo(memoryMXBean);
//
//	}
//
//	public static void getOperatingSystemInfo(OperatingSystemMXBean operatingSystemMXBean) throws Exception {
//		getRuntimeInfo(operatingSystemMXBean);
//	}
//
//	public static void getThreadInfo(ThreadMXBean threadMXBean) throws Exception {
//		getRuntimeInfo(threadMXBean);
//	}
//
//	public static String getPid() {
//		String name = ManagementFactory.getRuntimeMXBean().getName();
//		String pid = name.split("@")[0];
//		return pid;
//	}
//
//	public static void getMx(String[] args) throws Exception {
//
//		ClassPool classPool = ClassPool.getDefault();
//
//		CtClass ctClass = classPool.getCtClass(ExampleClass.class.getName());
//
//		Object[] annotations = ctClass.getAnnotations();
//
//		for (int i = 0; i < annotations.length; i++) {
//			Object object = annotations[i];
//
//			String substring = object.toString().substring(1);
//			System.out.println(substring);
//
//		}
//
//		ArrayList<Object> arrayList = new ArrayList<>();
//
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getName());
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getBootClassPath());
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getClassPath());
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getInputArguments());
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getLibraryPath());
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getManagementSpecVersion());
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getObjectName());
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getSpecName());
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getSpecVendor());
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getStartTime());
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getUptime());
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getVmName());
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getVmVendor());
//		arrayList.add(ManagementFactory.getRuntimeMXBean().getVmVersion());
//
//		arrayList.add("------------------------------------------------------");
//
//		List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
//
//		for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
//			System.out.println(garbageCollectorMXBean.getCollectionCount());
//			System.out.println(garbageCollectorMXBean.getCollectionTime());
//			String[] memoryPoolNames = garbageCollectorMXBean.getMemoryPoolNames();
//			for (String string : memoryPoolNames) {
//				System.out.print(string);
//			}
//
//			System.out.println(garbageCollectorMXBean.getName());
//			System.out.println(garbageCollectorMXBean.getObjectName());
//
//		}
//
//		arrayList.add(ManagementFactory.getGarbageCollectorMXBeans());
//
//		for (Object object : arrayList) {
//			System.out.println(object);
//		}
//	}
//}
