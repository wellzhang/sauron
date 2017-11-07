package com.feng.sauron.client.plugin.local.annotation;

import com.feng.sauron.client.plugin.AbstractTracerAdapterFactory;
import com.feng.sauron.tracer.impl.TimerTracer;

public class AnnotationTracerAdapter extends AbstractTracerAdapterFactory implements AnnotationTracerName {

	public AnnotationTracerAdapter() {
	}

	private AnnotationTracerAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
		this.spanId = spanId;
		this.spanCount = 0;
		this.status = STATUS.SUCCESS;
		this.methodName = methodName;
		this.className = className;
		this.sourceAppName = sourceAppName;
		this.paramClazz = paramClazz;
		this.params = params;
		this.tracerPool.put(TimerTracer.class.getName(), new TimerTracer());
	}
	@Override
	public AnnotationTracerAdapter getAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
		return new AnnotationTracerAdapter(spanId, className, methodName, sourceAppName, paramClazz, params);
	}

}
