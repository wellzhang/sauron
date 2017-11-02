package com.feng.sauron.agent;

import java.lang.instrument.Instrumentation;

import com.feng.sauron.client.plugin.PreProcessTransformer;
import com.feng.sauron.client.plugin.jvm.JvmTracer;
import com.feng.sauron.client.plugin.jvm.SystemInfoTracer;
import com.feng.sauron.utils.SauronLogUtils;

public class SauronAgent {

	private static volatile Instrumentation instrumentation;

	public static void premain(String options, Instrumentation inst) {

		SauronLogUtils.run();

		JvmTracer.run();

		SystemInfoTracer.run();

		instrumentation = inst;

		inst.addTransformer(new PreProcessTransformer());

		// CopyOfRedefineClasse.run();

	}

	public static void agentmain(String options, Instrumentation inst) {
		instrumentation = inst;
	}

	public static Instrumentation getInstrumentation() {
		return instrumentation;
	}
}
