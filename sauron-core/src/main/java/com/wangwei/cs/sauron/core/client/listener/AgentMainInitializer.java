package com.wangwei.cs.sauron.core.client.listener;

import java.io.File;

import com.wangwei.cs.sauron.core.utils.SauronUtils;

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


        try {

            String pid = SauronUtils.getPid();

            System.out.println("processId = [" + pid + "]");

            MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");

            MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + pid));

            boolean attachable = MonitoredVmUtil.isAttachable(vm);

            if (attachable) {

                ToolsJarInitializer.attach(pid);

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

                ToolsJarInitializer.loadAgent(agentPath);

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
                ToolsJarInitializer.detach();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
