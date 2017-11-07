package com.feng.sauron.test.domain;

import java.lang.management.ManagementFactory;


import com.sun.management.OperatingSystemMXBean;
import com.wangwei.cs.sauron.annotations.TraceClass;
import com.wangwei.cs.sauron.annotations.TraceMethod;

@TraceClass
public class ExampleClass2 {
	@TraceMethod
	public void doSomeWork() throws Exception {

		if (Math.round(Math.random() * 100 % 5) == 1) {
			Integer.parseInt("ss");
		}
	}

	public static void main(String[] args) {

		while (true) {
			try {
				OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

				long nanoBefore = System.nanoTime();
				long cpuBefore = operatingSystemMXBean.getProcessCpuTime();
				System.out.println(cpuBefore);
				// Call an expensive task, or sleep if you are monitoring a remote process

				int i = 2;
				while (i != 0) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							int ii = 2;
							while (true) {
								ii += ii;
							}

						}
					}).start();

					i *= i;
				}
				Thread.sleep(5);

				long cpuAfter = operatingSystemMXBean.getProcessCpuTime();
				System.out.println(cpuAfter);
				long nanoAfter = System.nanoTime();
				long percent;
				if (nanoAfter > nanoBefore) {
					percent = ((cpuAfter - cpuBefore) * 100L) / (nanoAfter - nanoBefore);
				} else {
					percent = 0;
				}
				System.out.println(percent + "%");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
