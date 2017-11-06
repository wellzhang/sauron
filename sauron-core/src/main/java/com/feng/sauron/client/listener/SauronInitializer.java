package com.feng.sauron.client.listener;


import com.feng.sauron.utils.SauronUtils;
import com.sun.tools.attach.VirtualMachine;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年11月15日 上午10:19:54
 */
public class SauronInitializer {


    public static void init() throws Throwable {

        try {
            String javaHome = System.getProperty("java.home");
            String toolsJarURL = "file:" + javaHome + "/../lib/tools.jar";
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            if (sysloader.getResourceAsStream("/com/sun/tools/attach/VirtualMachine.class") == null) {
                method.invoke(sysloader, new URL(toolsJarURL));
                Thread.currentThread().getContextClassLoader().loadClass("com.sun.tools.attach.VirtualMachine");
                Thread.currentThread().getContextClassLoader().loadClass("com.sun.tools.attach.AttachNotSupportedException");
            }
        } catch (Exception e) {
            System.out.println("Java home points to " + System.getProperty("java.home") + " make sure it is not a JRE path");
            e.printStackTrace();
        } catch (Throwable e) {
            System.out.println("Java home points to " + System.getProperty("java.home") + " make sure it is not a JRE path");
            e.printStackTrace();
        }

        try {
            System.out.println(VirtualMachine.class.getName());
            System.out.println("tools.jar init success ...");
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.err.println("tools.jar init error ...");

    }
}
