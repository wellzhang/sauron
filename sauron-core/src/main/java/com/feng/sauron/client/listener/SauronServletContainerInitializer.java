package com.feng.sauron.client.listener;

import java.util.Set;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;

import com.feng.sauron.client.plugin.PreProcessTransformer;
import com.feng.sauron.client.plugin.jvm.JvmTracer;
import com.feng.sauron.client.plugin.jvm.SystemInfoTracer;
import com.feng.sauron.config.SauronConfig;
import com.feng.sauron.config.WatchableConfigClient;
import com.feng.sauron.utils.SauronLogUtils;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年11月15日 上午10:19:10
 */
@WebListener
public class SauronServletContainerInitializer implements ServletContainerInitializer, ServletContextListener, Switch {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        init();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        init();
    }


    private void init() {

        if (flag.get()) {

            try {//以下每个方法的顺序不要乱动，小心死锁 ，看似不影响，其实很重要

                WatchableConfigClient.getInstance().get(SauronConfig.getAPP_NAME(), "jvm-switch", "ON");

                SauronInitializer.init();

                SauronLogUtils.run();// 必须在最前面

                JavaAgentMain.run();

                SauronInstrumentation.getInstance().addTransformer(new PreProcessTransformer());

                JvmTracer.run();

                SystemInfoTracer.run();

                // CopyOfRedefineClasse.run();

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
    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        WatchableConfigClient.close();
        System.err.println("ConfigClient Closed!");
    }


}
