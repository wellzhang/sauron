package com.feng.sauron.client.listener;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.feng.sauron.client.plugin.PreProcessTransformer;
import com.feng.sauron.client.plugin.jvm.JvmTracer;
import com.feng.sauron.client.plugin.jvm.SystemInfoTracer;
import com.feng.sauron.config.SauronConfig;
import com.feng.sauron.config.WatchableConfigClient;
import com.feng.sauron.utils.SauronLogUtils;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年11月15日 上午10:19:10
 * 
 */
public class SauronServletContainerInitializer implements ServletContainerInitializer, Switch {

	@Override
	public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {

		if (flag.get()) {

			try {//以下每个方法的顺序不要乱动，小心死锁 ，看似不影响，其实很重要

				WatchableConfigClient.getInstance().get(SauronConfig.getAPP_NAME(), "jvm-switch", "ON");

				SauronInitializer.init();

				SauronLogUtils.run();

				JavaAgentMain.run();

				JavaAgentWeaver javaAgentWeaver = new JavaAgentWeaver();

				javaAgentWeaver.addTransformer(new PreProcessTransformer());

				JvmTracer.run();

				SystemInfoTracer.run();

				System.err.println("sauron.init.success");

				// CallbackRedefineClasse.run();

			} catch (Exception ex) {
				System.err.println("sauron.init.failure");
				ex.printStackTrace();
			} catch (Throwable e) {
				System.err.println("sauron.init.failure");
				e.printStackTrace();
			}

			flag.set(false);
		}
	}

}
