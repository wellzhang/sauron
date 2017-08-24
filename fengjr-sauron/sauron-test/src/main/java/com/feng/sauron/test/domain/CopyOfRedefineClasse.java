package com.feng.sauron.test.domain;
//package com.feng.sauron.client.listener;
//
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.lang.Thread.State;
//import java.lang.management.ManagementFactory;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import sun.jvmstat.monitor.MonitoredHost;
//import sun.jvmstat.monitor.MonitoredVm;
//import sun.jvmstat.monitor.MonitoredVmUtil;
//import sun.jvmstat.monitor.VmIdentifier;
//import sun.jvmstat.monitor.event.MonitorStatusChangeEvent;
//import sun.jvmstat.monitor.event.VmEvent;
//import sun.jvmstat.monitor.event.VmListener;
//import sun.tools.attach.HotSpotVirtualMachine;
//
//import com.sun.tools.attach.VirtualMachine;
//
///**
// * @author wei.wang@fengjr.com
// * @version 2016年11月15日 下午1:56:11
// * 
// */
//public class CopyOfRedefineClasse {
//
//	public static void main(String[] args) {
//		try {
//			CopyOfRedefineClasse.run();
//			Thread.sleep(5000000L);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private CopyOfRedefineClasse() {
//		try {
//			printJvmInfo();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static class jvmMonitorInnerClass {
//
//		private static final CopyOfRedefineClasse JVM_MONITOR = new CopyOfRedefineClasse();
//	}
//
//	public static CopyOfRedefineClasse run() {
//		return jvmMonitorInnerClass.JVM_MONITOR;
//	}
//
//	private void printJvmInfo() {
//
//		Thread thread2 = new Thread();
//
//		String localPid = getLocalPid();
//
//		final VirtualMachine virtualMachine = getVirtualMachine(localPid);
//
//		Runnable runnable2 = new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					while (true) {
//
//						// getProcess();
//						test();
//						System.out.println("--------------------------------------------------------------------------------------------------------------");
//						threadtest(virtualMachine);
//
//						try {
//							Thread.sleep(10000L);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				} finally {
//
//				}
//			}
//		};
//
//		thread2 = new Thread(runnable2);
//		thread2.setDaemon(true);
//		thread2.start();
//	}
//
//	public static String getLocalPid() {
//
//		String name = ManagementFactory.getRuntimeMXBean().getName();
//
//		String pid = name.split("@")[0];
//
//		System.out.println("processId = [" + pid + "]");
//
//		return pid;
//	}
//
//	public static VirtualMachine getVirtualMachine(String pid) {
//
//		if (pid == null) {
//			return null;
//		}
//
//		try {
//
//			MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
//
//			MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + pid));
//
//			boolean attachable = MonitoredVmUtil.isAttachable(vm);
//
//			if (attachable) {
//
//				VirtualMachine attach = VirtualMachine.attach(pid);
//
//				return attach;
//
//			}
//		} catch (Exception e) {
//		} catch (Throwable e) {
//		}
//
//		return null;
//
//	}
//
//	public static int getProcess() {
//
//		try {
//
//			Thread.sleep(5000);
//
//			// 获取监控主机
//			MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
//			// 取得所有在活动的虚拟机集合
//			Set<?> vmlist = new HashSet<Object>(local.activeVms());
//			// 遍历集合，输出PID和进程名
//			for (Object process : vmlist) {
//				MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + process));
//
//				VmListener vmListener = new VmListener() {
//
//					@Override
//					public void monitorsUpdated(VmEvent paramVmEvent) {
//
//					}
//
//					@Override
//					public void monitorStatusChanged(MonitorStatusChangeEvent paramMonitorStatusChangeEvent) {
//
//					}
//
//					@Override
//					public void disconnected(VmEvent paramVmEvent) {
//
//					}
//				};
//
//				System.out.println("----------------------------------------------------------------------------------------------");
//
//				vm.addVmListener(vmListener);
//
//				String jvmArgs = MonitoredVmUtil.jvmArgs(vm);
//
//				System.out.println("jvmArgs: " + jvmArgs);
//
//				String mainClass = MonitoredVmUtil.mainClass(vm, true);
//
//				System.out.println("mainClass: " + mainClass);
//
//				System.out.println("processid: " + process);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return -1;
//	}
//
//	public static void test() {
//
//		ThreadGroup parentThread;
//		for (parentThread = Thread.currentThread().getThreadGroup(); parentThread.getParent() != null; parentThread = parentThread.getParent()) {
//			System.out.println(parentThread.getName() + "--");
//		}
//
//		int totalThread = parentThread.activeCount();
//		System.out.println(totalThread);
//	}
//
//	public static void threadtest(VirtualMachine virtualmachine) {
//
//		if (virtualmachine == null) {
//			return;
//		}
//
//		try {
//			HotSpotVirtualMachine machine = (HotSpotVirtualMachine) virtualmachine;
//
//			InputStream is = machine.heapHisto("-all");
//
//			String id = machine.id();
//
//			System.out.println(id);
//
//			ByteArrayOutputStream os = new ByteArrayOutputStream();
//			int readed;
//			byte[] buff = new byte[1024];
//
//			int ii = 0;
//
//			while ((readed = is.read(buff)) > 0) {
//				if (ii++ < 10) {
//					os.write(buff, 0, readed);
//				}
//			}
//			is.close();
//
//			System.out.println(os);
//
//			int activeCount = Thread.activeCount();
//
//			System.out.println(activeCount + ":activeCount");
//
//			Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
//
//			System.out.println(allStackTraces.size());
//
//			for (Thread thread : allStackTraces.keySet()) {
//
//				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//
//				String name2 = thread.getName();
//
//				System.out.println("thread.getName:" + name2);
//
//				boolean alive = thread.isAlive();
//
//				System.out.println("thread.isAlive:" + alive);
//
//				boolean daemon = thread.isDaemon();
//
//				System.out.println("thread.isDaemon:" + daemon);
//
//				State state = thread.getState();
//
//				System.out.println("thread.getState:" + state.name());
//
//				System.out.println("00000000000000000000000000000000000000000000000000000000000000000");
//
//				StackTraceElement[] stackTraceElements = allStackTraces.get(thread);
//
//				for (StackTraceElement stackTraceElement : stackTraceElements) {
//					String className = stackTraceElement.getClassName();
//					String fileName = stackTraceElement.getFileName();
//					int lineNumber = stackTraceElement.getLineNumber();
//					String methodName = stackTraceElement.getMethodName();
//
//					// System.out.println("className:" + className);
//					// System.out.println("fileName:" + fileName);
//					// System.out.println("lineNumber:" + lineNumber);
//					// System.out.println("methodName:" + methodName);
//					// System.out.println("=============================================================");
//				}
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//	}
//}
