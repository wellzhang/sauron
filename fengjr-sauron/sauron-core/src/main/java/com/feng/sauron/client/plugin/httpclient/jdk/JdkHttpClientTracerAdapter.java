package com.feng.sauron.client.plugin.httpclient.jdk;

import java.net.URL;

import com.feng.sauron.client.plugin.TracerAdapterFactory;
import com.feng.sauron.tracer.Tracer;
import com.feng.sauron.tracerImpl.TimerTracer;

public class JdkHttpClientTracerAdapter extends TracerAdapterFactory implements JdkHttpClientTracerName {

	public JdkHttpClientTracerAdapter() {
	}

	private JdkHttpClientTracerAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
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
		return new JdkHttpClientTracerAdapter(spanId, className, methodName, sourceAppName, paramClazz, params);
	}

	@Override
	public String printTraceLog() {

		try {
			if (params[0] instanceof URL) {
				URL url = (URL) params[0];
				String host = url.getHost();
				System.out.println(host);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.printTraceLog();
	}
}
