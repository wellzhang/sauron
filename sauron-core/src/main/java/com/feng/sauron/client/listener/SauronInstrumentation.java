package com.feng.sauron.client.listener;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年10月11日 上午11:33:06
 */
public class SauronInstrumentation {


    private static final String JAVA_AGENT_CLASS_NAME = "com.feng.sauron.agent.SauronAgent";
    private static Instrumentation inst = null;


    private static class InnerClass {
        private static final SauronInstrumentation JVM_MONITOR = new SauronInstrumentation();
    }

    public static SauronInstrumentation getInstance() {
        return InnerClass.JVM_MONITOR;
    }


    private SauronInstrumentation() {
        initInstrumentation();
    }

    public void addTransformer(ClassFileTransformer transformer) {
        if (inst == null) {
            throw new IllegalStateException("Java Agent Instrumentation is not ready!");
        } else {
            inst.addTransformer(transformer, true);
        }
    }

    public Instrumentation initInstrumentation() {

        if (inst == null) {
            try {
                Class<?> agentClass = isInstrumentationAvailable();

                if (agentClass == null) {
                    System.err.println("agentClass is required for Instrumentation ...");
                    throw new IllegalStateException("agentClass is required for Instrumentation ...");
                }

                Method getInstrumentationMethod = agentClass.getMethod("getInstrumentation", new Class[0]);

                inst = (Instrumentation) getInstrumentationMethod.invoke(agentClass, new Object[0]);
            } catch (Exception e) {
                System.err.println("getInstrumentation error ...");
                throw new IllegalStateException("getInstrumentation error ...");
            }
        }
        return inst;
    }

    private Class<?> isInstrumentationAvailable() {
        Class<?> agentClass = null;
        try {
            agentClass = Class.forName(JAVA_AGENT_CLASS_NAME, true, ClassLoader.getSystemClassLoader());// 使用系统classloader才能加载到 带有inst 变量的类
        } catch (Exception e) {
        }
        return agentClass;
    }


}
