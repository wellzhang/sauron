package com.feng.sauron.client.plugin.spring.mvc;

import com.feng.sauron.client.plugin.TracerAdapterFactory;
import com.feng.sauron.tracer.Tracer;
import com.feng.sauron.tracerImpl.TimerTracer;

public class SpringMVCTracerAdapter extends TracerAdapterFactory implements Tracer, SpringMVCTracerName {

	public SpringMVCTracerAdapter() {
	}

	private SpringMVCTracerAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
		this.spanId = spanId;
		this.spanCount = 0;
		this.status = STATUS.SUCCESS;
		this.methodName = methodName;
		this.className = className;
		this.sourceAppName = sourceAppName;
		this.detail = TRACERNAME_STRING;
		this.type = TRACERNAME_STRING;
		this.paramClazz = paramClazz;
		this.params = params;
		tracerPool.put(TimerTracer.class.getName(), new TimerTracer());
	}

	@Override
	public Tracer getAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
		return new SpringMVCTracerAdapter(spanId, className, methodName, sourceAppName, paramClazz, params);
	}

}
