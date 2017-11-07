package com.wangwei.cs.sauron.core.client.context;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTracerAdapterFactory;
import com.wangwei.cs.sauron.core.tracer.Tracer;
import com.wangwei.cs.sauron.core.utils.IdUtils;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年10月28日 上午11:37:58
 * 
 */
public class SauronSessionContext {

	private final static ThreadLocal<Stack<Tracer>> TRACERS = new ThreadLocal<Stack<Tracer>>();
	private final static ThreadLocal<String> traceId = new ThreadLocal<String>();

	private final static ThreadLocal<Boolean> TRACE_SWITCH = new ThreadLocal<Boolean>();
	private final static ThreadLocal<Set<String>> ENABLED_TRACER = new ThreadLocal<Set<String>>();
	private final static ThreadLocal<Map<String, HashSet<String>>> METHOD_TO_TRACE = new ThreadLocal<Map<String, HashSet<String>>>();
	private final static ThreadLocal<Boolean> IS_SAMPLED = new ThreadLocal<Boolean>();

	public static String getTraceId() {
		return traceId.get();
	}

	public static Stack<Tracer> getTracers() {
		return TRACERS.get();
	}

	public static void setTracers(Stack<Tracer> tracerStack) {
		TRACERS.set(tracerStack);
	}

	public static Tracer getCurrentTracerAdapter() throws NullPointerException {
		if (TRACERS.get() == null) {
			System.err.println("getCurrentTracerAdapter from Empty Tracer Stack! ...........................................................................");
			return null;
		}
		return TRACERS.get().peek();
	}

	public static void addTracerAdapter(Tracer tracer) {

		if (tracer == null) {
			System.err.println("tracer is null ...");
			return;
		}

		if (TRACERS.get() == null) {
			System.err.println("addTracerAdapter to Empty Tracer Stack! ...........................................................................");
			return;
		}
		TRACERS.get().push(tracer);
	}

	public static void removeTracerAdapter() {
		if (TRACERS.get() == null) {
			System.err.println("removeTracerAdapter from Empty Tracer Stack! ...........................................................................");
			return;
		}
		TRACERS.get().pop();
	}

	public static Tracer allocCurrentTracerAdapter(String tracerName, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] paramVal) {
		Tracer adapter = null;
		if (isTraceEntry()) {
			adapter = AbstractTracerAdapterFactory.get(tracerName, "0", className, methodName, sourceAppName, paramClazz, paramVal);
		} else {
			AbstractTracerAdapterFactory currentTracerAdapter = (AbstractTracerAdapterFactory) getCurrentTracerAdapter();
			String spanId = currentTracerAdapter.getNextSpanId();
			adapter = AbstractTracerAdapterFactory.get(tracerName, spanId, className, methodName, sourceAppName, paramClazz, paramVal);
			if (adapter == null) {
				currentTracerAdapter.countDownSpanCount();
			}
		}
		addTracerAdapter(adapter);
		return adapter;
	}

	public static Tracer allocCurrentTracerAdapter(String tracerName, String className, String methodName, String sourceAppName) {
		return allocCurrentTracerAdapter(tracerName, className, methodName, sourceAppName, null, null);
	}

	public static boolean isTraceEntry() {
		return TRACERS.get() == null || TRACERS.get().isEmpty();
	}

	private static String generateTraceId() {
		return IdUtils.getInstance().nextId();
	}

	public static void initSessionContext() {
		TRACERS.set(new Stack<Tracer>());
		traceId.set(generateTraceId());
		initTracerSwitchFromConfigCenter();
		initEnabledTracersFromConfigCenter();
		initMethodsToTraceFromConfigCenter();
		initSamplingRateFromConfigCenter();
	}

	public static void initSessionContext(String tracerId) {
		TRACERS.set(new Stack<Tracer>());
		traceId.set(tracerId);
		initTracerSwitchFromConfigCenter();
		initEnabledTracersFromConfigCenter();
		initMethodsToTraceFromConfigCenter();
		initSamplingRateFromConfigCenter();
	}

	private static void initTracerSwitchFromConfigCenter() {
		TRACE_SWITCH.set(SauronGlobalContext.isSwitchedOn());
	}

	public static boolean isSwitchedOn() {
		return TRACE_SWITCH.get();
	}

	private static void initEnabledTracersFromConfigCenter() {
		ENABLED_TRACER.set(SauronGlobalContext.getEnabledTracer());
	}

	private static void initMethodsToTraceFromConfigCenter() {
		METHOD_TO_TRACE.set(SauronGlobalContext.getMethodToTrace());
	}

	private static void initSamplingRateFromConfigCenter() {
		IS_SAMPLED.set(SauronGlobalContext.isSampled());
	}

	public static Map<String, HashSet<String>> getMethodToTrace() {
		return METHOD_TO_TRACE.get();
	}

	public static Set<String> getEnabledTracer() {
		return ENABLED_TRACER.get();
	}

	public static boolean isSampled() {
		return IS_SAMPLED.get();
	}

	public static boolean isMethodShouldBeTrace(String className, String methodName) {
		if (isTraceEntry()) {
			initSessionContext();
		}
		return isSampled() && isSwitchedOn();
	}
}
