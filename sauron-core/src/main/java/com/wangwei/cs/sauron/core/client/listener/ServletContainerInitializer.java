package com.wangwei.cs.sauron.core.client.listener;

import java.util.Set;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;

import com.wangwei.cs.sauron.core.client.plugin.PreProcessTransformer;
import com.wangwei.cs.sauron.core.client.plugin.jvm.JvmTracer;
import com.wangwei.cs.sauron.core.client.plugin.jvm.SystemInfoTracer;
import com.wangwei.cs.sauron.core.config.SauronConfig;
import com.wangwei.cs.sauron.core.config.WatchableConfigClient;
import com.wangwei.cs.sauron.core.log.SauronLog;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年11月15日 上午10:19:10
 */
@WebListener
public class ServletContainerInitializer implements javax.servlet.ServletContainerInitializer, ServletContextListener, Switch {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        init();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        init();
    }


    private void init() {

        if (FLAG.get()) {

            try {//以下每个方法的顺序不要乱动，小心死锁 ，看似不影响，其实很重要

                WatchableConfigClient.getInstance().get(SauronConfig.getAppName(), "jvm-switch", "ON");

                ToolsJarInitializer.init();

                SauronLog.run();// 必须在最前面

                AgentMainInitializer.run();

                InstrumentationInitializer.getInstance().addTransformer(new PreProcessTransformer());

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

            FLAG.set(false);
        }
    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        WatchableConfigClient.close();
        System.err.println("ConfigClient Closed!");
    }


}
