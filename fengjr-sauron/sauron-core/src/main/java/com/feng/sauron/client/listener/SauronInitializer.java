package com.feng.sauron.client.listener;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import sun.jvmstat.monitor.MonitoredHost;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年11月15日 上午10:19:54
 * 
 */
public class SauronInitializer {

	public static void init() throws Exception, Throwable {

		boolean flag = false;

		try {
			MonitoredHost.getMonitoredHost("localhost");
		} catch (Exception e) {
			flag = true;
		} catch (Throwable e) {
			flag = true;
		}

		if (flag) {

			System.out.println("检测到当前环境无 tools.jar , init ...");

			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

			if (contextClassLoader.getClass().getName().startsWith("org.apache.catalina")) {

				System.out.println("org.apache.catalina...");

				Method addRepository = contextClassLoader.getClass().getSuperclass().getDeclaredMethod("addURL", new Class[] { URL.class });
				addRepository.setAccessible(true);

				String property = System.getProperty("java.home");

				if (property.endsWith("jre")) {
					String replace = property.replace("jre", "lib/tools.jar");
					File jarFile = new File(replace);
					if (jarFile.exists()) {
						addRepository.invoke(contextClassLoader, jarFile.toURI().toURL());
					}
				}
			} else {

				ClassLoader parent = contextClassLoader.getParent();

				if (parent.getClass().getName().startsWith("java.net.URLClassLoader")) {

					System.out.println("java.net.URLClassLoader...");

					Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
					addURL.setAccessible(true);

					String property = System.getProperty("java.home");

					if (property.endsWith("jre")) {
						String replace = property.replace("jre", "lib/tools.jar");
						File jarFile = new File(replace);
						if (jarFile.exists()) {
							addURL.invoke(parent, jarFile.toURI().toURL());
						}
					}
				} else if (parent.getClass().getName().startsWith("sun.misc.Launcher")) {

					System.out.println("sun.misc.Launcher...");

					String property = System.getProperty("java.home");

					if (property.endsWith("jre")) {
						String replace = property.replace("jre", "lib/tools.jar");
						File jarFile = new File(replace);
						if (jarFile.exists()) {
							URL[] urls = new URL[1];
							urls[0] = jarFile.toURI().toURL();
							URLClassLoader urlClassLoader = new URLClassLoader(urls, parent);
							System.out.println(urlClassLoader);
							Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
							addURL.setAccessible(true);
							addURL.invoke(urlClassLoader, jarFile.toURI().toURL());

						}
					}

				}
			}
			System.out.println("init tools.jar end");
		}
	}
}
