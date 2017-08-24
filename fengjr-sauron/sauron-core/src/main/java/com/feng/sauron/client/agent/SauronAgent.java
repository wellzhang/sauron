package com.feng.sauron.client.agent;

import java.lang.instrument.Instrumentation;

import com.feng.sauron.client.listener.SauronInitializer;
import com.feng.sauron.client.plugin.PreProcessTransformer;
import com.feng.sauron.client.plugin.jvm.JvmTracer;
import com.feng.sauron.client.plugin.jvm.SystemInfoTracer;
import com.feng.sauron.utils.LogBackUtils;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年10月28日 下午2:16:10
 */
public class SauronAgent {

    private static volatile Instrumentation instrumentation;

    public static void premain(String options, Instrumentation inst) {

        try {
            SauronInitializer.init();
        } catch (Exception e) {
        } catch (Throwable e) {
        }

        LogBackUtils.run();

        JvmTracer.run();

        SystemInfoTracer.run();

        instrumentation = inst;

        inst.addTransformer(new PreProcessTransformer());
    }

    public static void agentmain(String options, Instrumentation inst) {
        instrumentation = inst;
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }
}
