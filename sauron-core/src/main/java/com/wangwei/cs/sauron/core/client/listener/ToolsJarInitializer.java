package com.wangwei.cs.sauron.core.client.listener;

import com.sun.tools.attach.VirtualMachine;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年11月15日 上午10:19:54
 */
public class ToolsJarInitializer {


    private static Object virtualmachineObject = null;

    private static Class<?> virtualmachineClass = null;


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
        } catch (Exception e) {
            System.err.println("tools.jar init error ...");
            e.printStackTrace();
        } catch (Throwable e) {
            System.err.println("tools.jar init error ...");
            e.printStackTrace();
        }

    }

    public static void attach(String pid) throws Exception {

        // VirtualMachine virtualmachine = VirtualMachine.attach(pid);

        // this.virtualmachineClass = Class.forName("com.sun.tools.attach.VirtualMachine", true, ClassLoader.getSystemClassLoader().getParent());

        virtualmachineClass = Class.forName("com.sun.tools.attach.VirtualMachine", true,  ToolsJarInitializer.class.getClassLoader());

        Method attach = virtualmachineClass.getDeclaredMethod("attach", String.class);

        virtualmachineObject = attach.invoke(virtualmachineClass, new Object[]{pid});

    }

    public static void loadAgent(String agentPath) throws Exception {

        // virtualmachine.loadAgent(agentPath);

        Method loadAgent = virtualmachineClass.getDeclaredMethod("loadAgent", String.class);

        loadAgent.invoke(virtualmachineObject, new Object[]{agentPath});

    }

    public static void detach() throws Exception {

        // virtualmachine.detach();

        Method detach = virtualmachineClass.getDeclaredMethod("detach", new Class[0]);

        detach.invoke(virtualmachineObject, new Object[0]);

    }
}
