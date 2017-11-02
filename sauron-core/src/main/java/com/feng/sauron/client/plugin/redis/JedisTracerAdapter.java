package com.feng.sauron.client.plugin.redis;

import com.feng.sauron.client.plugin.TracerAdapterFactory;
import com.feng.sauron.tracer.Tracer;
import com.feng.sauron.tracerImpl.TimerTracer;
import com.feng.sauron.utils.SauronUtils;

public class JedisTracerAdapter extends TracerAdapterFactory implements JedisTracerName {

	public JedisTracerAdapter() {
	}

	private JedisTracerAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
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
		return new JedisTracerAdapter(spanId, className, methodName, sourceAppName, paramClazz, params);
	}

	@Override
	public String printTraceLog() {

		try {

			if (methodName.contains("BinaryClient(")) {// 构造方法..

				if (params != null && params.length > 0) {

					String ip = params[0].toString();

					if (!ip.startsWith("10.")) {
						return null;
					}

					if (params.length == 1) {

						detail = ip + ":6379";

					} else if (params.length == 2) {

						detail = ip + ":" + params[1];
					}
				} else {

					detail = "localhost:6379";
				}

				paramClazz = null;// 不需要显示
				params = null;

			} else {

				detail = SauronUtils.getLastMethod(methodName);

				if (params != null && params.length > 0) {

					if (params[0] instanceof byte[]) {

						byte[] bytes = (byte[]) params[0];

						paramClazz = new Class<?>[] { String.class };
						params = new Object[] { new String(bytes) };
					}

					if (params[0] instanceof byte[][]) {

						byte[][] bytes = (byte[][]) params[0];
						int length = bytes.length > 5 ? 5 : bytes.length;// 最多打印5个参数
						paramClazz = new Class<?>[length];
						params = new Object[length];
						for (int i = 0; i < length; i++) {
							paramClazz[i] = String.class;
							params[i] = new String(bytes[i]);
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.printTraceLog();
	}
}
