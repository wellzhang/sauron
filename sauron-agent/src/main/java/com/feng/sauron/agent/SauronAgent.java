package com.feng.sauron.agent;

import java.lang.instrument.Instrumentation;

import com.feng.sauron.client.listener.Switch;
import com.feng.sauron.client.plugin.PreProcessTransformer;
import com.feng.sauron.client.plugin.jvm.JvmTracer;
import com.feng.sauron.client.plugin.jvm.SystemInfoTracer;
import com.feng.sauron.utils.SauronLogUtils;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年11月9日 下午3:01:03
 */

public class SauronAgent {

    private static volatile Instrumentation instrumentation;

    public static void premain(String options, Instrumentation inst) {

        instrumentation = inst;

        if ("web".equalsIgnoreCase(options)) {

            System.out.println("sauron agent init by web");

        } else if ("main".equalsIgnoreCase(options)) {

            System.out.println("sauron agent init by native");

            SauronLogUtils.run();

            JvmTracer.run();

            SystemInfoTracer.run();

            inst.addTransformer(new PreProcessTransformer());

            Switch.FLAG.set(false);

        } else {
            throw new RuntimeException("please config options ,eg : -javaagent:D:/xxxx/sauron-agent.jar=web");
        }
    }

    public static void agentmain(String options, Instrumentation inst) {
        instrumentation = inst;
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }
}
