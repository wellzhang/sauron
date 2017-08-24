package com.feng.sauron.client.plugin.spring.defaultservlet;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import com.feng.sauron.client.plugin.TracerAdapterFactory;
import com.feng.sauron.tracer.Tracer;
import com.feng.sauron.tracerImpl.TimerTracer;

public class SpringTracerAdapter extends TracerAdapterFactory implements Tracer, SpringTracerName {

	public SpringTracerAdapter() {
	}

	private SpringTracerAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
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
		return new SpringTracerAdapter(spanId, className, methodName, sourceAppName, paramClazz, params);
	}

	@Override
	public String printTraceLog() {

		try {

			if (params != null && params[0] instanceof HttpServletRequest) {

				HttpServletRequest request = (HttpServletRequest) params[0];

				StringBuffer requestURL = request.getRequestURL();

				String domain = requestURL.toString().replace(request.getRequestURI(), "");

				detail = domain;

				paramClazz = new Class[] { URI.class, String.class };

				params = new Object[] { requestURL.toString(), request.getParameterMap() };

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return super.printTraceLog();
	}

}
