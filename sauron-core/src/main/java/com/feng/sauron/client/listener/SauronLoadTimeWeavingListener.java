package com.feng.sauron.client.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.feng.sauron.client.plugin.PreProcessTransformer;
import com.feng.sauron.client.plugin.jvm.JvmTracer;
import com.feng.sauron.client.plugin.jvm.SystemInfoTracer;
import com.feng.sauron.config.SauronConfig;
import com.feng.sauron.config.WatchableConfigClient;
import com.feng.sauron.utils.SauronLogUtils;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年11月15日 上午10:18:45
 * 
 */
@WebListener
public class SauronLoadTimeWeavingListener implements ServletContextListener, Switch {

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		if (flag.get()) {

			try {//以下每个方法的顺序不要乱动，小心死锁 ，看似不影响，其实很重要

				WatchableConfigClient.getInstance().get(SauronConfig.getAPP_NAME(), "jvm-switch", "ON");

				SauronInitializer.init();

				SauronLogUtils.run();// 必须在最前面

				JavaAgentMain.run();

				JavaAgentWeaver javaAgentWeaver = new JavaAgentWeaver();

				javaAgentWeaver.addTransformer(new PreProcessTransformer());

				JvmTracer.run();

				SystemInfoTracer.run();

				System.err.println("sauron.init.success");

			} catch (Exception ex) {
				System.err.println("sauron.init.failure");
				ex.printStackTrace();
			} catch (Throwable e) {
				System.err.println("sauron.init.failure");
				e.printStackTrace();
			}

			flag.set(false);
		}

		// CopyOfRedefineClasse.run();

	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		WatchableConfigClient.close();
		System.err.println("ConfigClient Closed!");
	}

}
