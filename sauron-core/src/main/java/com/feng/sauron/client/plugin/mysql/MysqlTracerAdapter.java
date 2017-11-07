package com.feng.sauron.client.plugin.mysql;

import com.feng.sauron.client.plugin.AbstractTracerAdapterFactory;
import com.feng.sauron.tracer.Tracer;
import com.feng.sauron.tracer.impl.TimerTracer;
import com.feng.sauron.utils.StringMaker;

/**
 * Created by lianbin.wang on 11/4/16.
 */
public class MysqlTracerAdapter extends AbstractTracerAdapterFactory implements MysqlTracerName {

	public MysqlTracerAdapter() {
	}

	private MysqlTracerAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
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
		return new MysqlTracerAdapter(spanId, className, methodName, sourceAppName, paramClazz, params);
	}

	@Override
	public String printTraceLog() {

		try {
			if (params != null && params.length > 0) {
				String jdbcUrl = (String) params[0];
				StringMaker marker = new StringMaker(jdbcUrl);
				String mainInfo = marker.before('?').value();
				this.detail = mainInfo;
				paramClazz = null;
				params = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return super.printTraceLog();
	}
}
