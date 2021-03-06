package com.feng.sauron.client.listener;

import java.io.File;
import java.lang.reflect.Method;

import com.feng.sauron.utils.SauronUtils;


import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;


/**
 * @author wei.wang@fengjr.com
 * @version 2015年11月9日 下午3:02:03
 */
public class AgentMainInitializer {


    private Object virtualmachineObject = null;

    private Class<?> virtualmachineClass = null;

    private AgentMainInitializer() {
        initAgentMain();
    }

    private static class InnerClass {
        private static final AgentMainInitializer INSTACNE = new AgentMainInitializer();
    }

    public static AgentMainInitializer run() {
        return InnerClass.INSTACNE;
    }

    public void initAgentMain() {

        // VirtualMachine virtualmachine = null;

        try {

            String pid = SauronUtils.getPid();

            System.out.println("processId = [" + pid + "]");

            MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");

            MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + pid));

            boolean attachable = MonitoredVmUtil.isAttachable(vm);

            if (attachable) {

                // virtualmachine = VirtualMachine.attach(pid);
                attach(pid);

                String agentPath = getAgentPath();

                System.out.println("agentPath = [" + agentPath + "]");

                if (agentPath == null) {
                    agentPath = "/home/sauron/sauron-agent.jar";
                }

                if (!agentPath.endsWith(".jar")) {// 指定agent.jar绝对路径，仅供应本地调试

                    agentPath = "/home/sauron/sauron-agent.jar";
                    File file = new File(agentPath);

                    if (file.exists()) {

                        agentPath = file.getAbsolutePath();
                        System.err.println("原agentPath不可用 ,使用指定agentPath: = [" + agentPath + "]");

                    } else {
                        System.err.println("原agentPath不可用 ,新指定agentPath: = [" + agentPath + "] 也不可用");

                    }
                }

                // virtualmachine.loadAgent(agentPath);

                loadAgent(agentPath);

                System.err.println("loadAgentMain success ...");

            } else {
                System.err.println("................................");
                System.err.println("................................");
                System.err.println("..sauron agent init failure ....");
                System.err.println("................................");
                System.err.println("................................");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                // if (virtualmachine != null) {
                // virtualmachine.detach();
                // }
                detach();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void attach(String pid) throws Exception {

        // VirtualMachine virtualmachine = VirtualMachine.attach(pid);

        // this.virtualmachineClass = Class.forName("com.sun.tools.attach.VirtualMachine", true, ClassLoader.getSystemClassLoader().getParent());

        this.virtualmachineClass = Class.forName("com.sun.tools.attach.VirtualMachine", true, this.getClass().getClassLoader());

        Method attach = virtualmachineClass.getDeclaredMethod("attach", String.class);

        this.virtualmachineObject = attach.invoke(virtualmachineClass, new Object[]{pid});

    }

    public void loadAgent(String agentPath) throws Exception {

        // virtualmachine.loadAgent(agentPath);

        Method loadAgent = virtualmachineClass.getDeclaredMethod("loadAgent", String.class);

        loadAgent.invoke(virtualmachineObject, new Object[]{agentPath});

    }

    public void detach() throws Exception {

        // virtualmachine.detach();

        Method detach = virtualmachineClass.getDeclaredMethod("detach", new Class[0]);

        detach.invoke(virtualmachineObject, new Object[0]);

    }

    private String getAgentPath() {

        String selfpath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile().replace("core", "agent");

        File file = new File(selfpath);

        if (file.exists()) {
            System.err.println("exists  " + selfpath);
            return file.getAbsolutePath();
        } else {
            System.err.println("file no exists ....  " + selfpath);
            return null;
        }
    }
}
