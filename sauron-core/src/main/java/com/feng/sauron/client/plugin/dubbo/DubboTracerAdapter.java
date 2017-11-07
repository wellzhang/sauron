package com.feng.sauron.client.plugin.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.feng.sauron.client.plugin.AbstractTracerAdapterFactory;
import com.feng.sauron.tracer.Tracer;
import com.feng.sauron.tracer.impl.TimerTracer;

public class DubboTracerAdapter extends AbstractTracerAdapterFactory implements DubboTracerName {

	public DubboTracerAdapter() {
	}

	private DubboTracerAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
		this.spanId = spanId;
		this.spanCount = 0;
		this.status = STATUS.SUCCESS;
		this.methodName = methodName;
		this.className = className;
		this.sourceAppName = sourceAppName;
		this.detail = methodName;
		this.type = TRACERNAME_STRING;
		this.paramClazz = paramClazz;
		this.params = params;
		tracerPool.put(TimerTracer.class.getName(), new TimerTracer());

	}

	@Override
	public Tracer getAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
		return new DubboTracerAdapter(spanId, className, methodName, sourceAppName, paramClazz, params);
	}

	@Override
	public String printTraceLog() {

		try {
			Invoker<?> invoker = null;

			if (params[0] instanceof Invoker) {

				invoker = (Invoker<?>) params[0];
			}

			Invocation invocation = null;

			if (params[1] instanceof Invocation) {

				invocation = (Invocation) params[1];
			}

			if (invoker != null) {

				if (Constants.CONSUMER_SIDE.equals(invoker.getUrl().getParameter(Constants.SIDE_KEY))) {

					type = DubboTransformer.dubbo_consumer_prifix;

				} else {

					type = DubboTransformer.dubbo_provider_prifix;
				}
			}

			if (invocation != null) {

				params = invocation.getArguments();

				paramClazz = invocation.getParameterTypes();
			}

		} catch (Exception e) {
		}

		return super.printTraceLog();
	}

}
