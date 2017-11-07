package com.feng.sauron.client.plugin.web;

import com.feng.sauron.client.plugin.AbstractTracerAdapterFactory;
import com.feng.sauron.client.plugin.web.printlog.WebPrintLog;
import com.feng.sauron.tracer.Tracer;
import com.feng.sauron.tracer.impl.TimerTracer;

public class WebTracerAdapter extends AbstractTracerAdapterFactory implements Tracer, WebTracerName {

	public WebTracerAdapter() {
	}

	private WebTracerAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
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
		return new WebTracerAdapter(spanId, className, methodName, sourceAppName, paramClazz, params);
	}

	@Override
	public String printTraceLog() {

		try {
			WebPrintLog webPrintLog = WebPrintLog.getInstances();
			String print = webPrintLog.print(this);
			if (print != null) {
				return print;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return super.printTraceLog();
	}

}
