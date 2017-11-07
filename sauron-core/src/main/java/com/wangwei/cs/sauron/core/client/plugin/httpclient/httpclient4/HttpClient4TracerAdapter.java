package com.wangwei.cs.sauron.core.client.plugin.httpclient.httpclient4;

import java.util.HashMap;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTracerAdapterFactory;
import org.apache.http.client.methods.HttpUriRequest;

import com.wangwei.cs.sauron.core.client.context.SauronSessionContext;
import com.wangwei.cs.sauron.core.client.plugin.PrintTraceLog;
import com.wangwei.cs.sauron.core.config.SauronConfig;
import com.wangwei.cs.sauron.core.tracer.Tracer;
import com.wangwei.cs.sauron.core.tracer.impl.TimerTracer;
import com.wangwei.cs.sauron.core.utils.Constants;

public class HttpClient4TracerAdapter extends AbstractTracerAdapterFactory implements HttpClient4TracerName {

	public HttpClient4TracerAdapter() {
	}

	private HttpClient4TracerAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
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
		return new HttpClient4TracerAdapter(spanId, className, methodName, sourceAppName, paramClazz, params);
	}

	@Override
	public void beforeMethodExecute() {// 添加 header
		super.beforeMethodExecute();
		try {
			if (params[0] instanceof HttpUriRequest) {
				HttpUriRequest httpUriRequest = (HttpUriRequest) params[0];

				httpUriRequest.setHeader(Constants.SAURON_REQUEST_TRACEID, SauronSessionContext.getTraceId());
				httpUriRequest.setHeader(Constants.SAURON_REQUEST_SPANID, spanId);
				httpUriRequest.setHeader(Constants.SAURON_REQUEST_SOURCE_APPNAME, SauronConfig.getAppName());

			}
		} catch (Exception e) {
		}

	}

	@Override
	public String printTraceLog() {

		try {
			HashMap<String, PrintTraceLog> hashMap = HttpClient4Transformer.getTraceClzMap().get(className);

			PrintTraceLog printTraceLog = null;

			for (String methodString : hashMap.keySet()) {

				if (methodName.contains(methodString)) {
					printTraceLog = hashMap.get(methodString);
					break;
				}
			}

			if (printTraceLog != null) {
				String print = printTraceLog.print(this);
				if (print != null) {
					return print;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.printTraceLog();
	}
}
