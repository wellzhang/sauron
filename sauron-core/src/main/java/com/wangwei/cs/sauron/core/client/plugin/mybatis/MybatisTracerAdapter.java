package com.wangwei.cs.sauron.core.client.plugin.mybatis;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTracerAdapterFactory;
import com.wangwei.cs.sauron.core.tracer.Tracer;
import com.wangwei.cs.sauron.core.tracer.impl.TimerTracer;

public class MybatisTracerAdapter extends AbstractTracerAdapterFactory implements MybatisTracerName {

	public MybatisTracerAdapter() {
	}

	private MybatisTracerAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {

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
		return new MybatisTracerAdapter(spanId, className, methodName, sourceAppName, paramClazz, params);
	}

}
