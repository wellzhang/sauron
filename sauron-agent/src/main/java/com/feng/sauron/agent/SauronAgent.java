package com.feng.sauron.agent;

import java.lang.instrument.Instrumentation;

import com.feng.sauron.client.listener.Switch;
import com.feng.sauron.client.plugin.PreProcessTransformer;
import com.feng.sauron.client.plugin.jvm.JvmTracer;
import com.feng.sauron.client.plugin.jvm.SystemInfoTracer;
import com.feng.sauron.utils.SauronLogUtils;

public class SauronAgent {

    private static volatile Instrumentation instrumentation;

    public static void premain(String options, Instrumentation inst) {

        instrumentation = inst;

        if ("web".equalsIgnoreCase(options)) {

            System.out.println("sauron agent init by web");

        } else if ("native".equalsIgnoreCase(options)) {

            System.out.println("sauron agent init by native");

            SauronLogUtils.run();

            JvmTracer.run();

            SystemInfoTracer.run();

            inst.addTransformer(new PreProcessTransformer());

            Switch.flag.set(false);

            // CopyOfRedefineClasse.run();

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
