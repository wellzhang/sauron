package com.feng.sauron.client.listener;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年10月11日 上午11:33:06
 * 
 */
public class JavaAgentWeaver {

	private static final String JAVA_CORE_AGENT_CLASS_NAME = "com.feng.sauron.client.agent.SauronAgent";
	private static final String JAVA_AGENT_CLASS_NAME = "com.feng.sauron.agent.SauronAgent";
	private static Instrumentation inst = null;

	public JavaAgentWeaver() {
		getInstrumentation();
		if (inst == null) {
			throw new IllegalStateException("Java Agent is not found!");
		}
	}

	public void addTransformer(ClassFileTransformer transformer) {
		if (inst == null) {
			throw new IllegalStateException("Java Agent Instrumentation is not ready!");
		} else {
			inst.addTransformer(transformer, true);
		}
	}

	public Instrumentation getInstrumentation() {
		try {

			Class<?> agentClass = isInstrumentationAvailable();
			// 顺序不能乱，先 agent 后 core_agent
			if (agentClass == null) {
				agentClass = isInstrumentationAvailable_core();
			}

			if (agentClass == null) {
				System.err.println("Java Agent is required for Instrumentation.");
				return null;
			}

			Method getInstrumentationMethod = agentClass.getMethod("getInstrumentation", new Class[0]);

			inst = (Instrumentation) getInstrumentationMethod.invoke(agentClass, new Object[0]);

			if (inst == null) {
				System.err.println("Instrumentation is not functional!");
			}

		} catch (Exception e) {
			System.err.println("getInstrumentation error ...");
			throw new IllegalStateException("getInstrumentation error ...");
		}
		return inst;
	}

	public static Class<?> isInstrumentationAvailable() {
		Class<?> agentClass = null;
		try {
			agentClass = Class.forName(JAVA_AGENT_CLASS_NAME, true, ClassLoader.getSystemClassLoader());// 使用系统classloader才能加载到 带有inst 变量的类
		} catch (Exception e) {
		}
		return agentClass;
	}

	public static Class<?> isInstrumentationAvailable_core() {
		Class<?> agentClass = null;
		try {
			agentClass = Class.forName(JAVA_CORE_AGENT_CLASS_NAME, true, ClassLoader.getSystemClassLoader());// 使用系统classloader才能加载到 带有inst 变量的类
		} catch (Exception e) {
		}
		return agentClass;
	}
}
