package com.feng.sauron.client.plugin;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.feng.sauron.client.context.SauronSessionContext;
import com.feng.sauron.client.plugin.dubbo.DubboTracerAdapter;
import com.feng.sauron.client.plugin.httpclient.httpclient4.HttpClient4TracerAdapter;
import com.feng.sauron.client.plugin.local.annotation.AnnotationTracerAdapter;
import com.feng.sauron.client.plugin.local.methodname.MethodNameTracerAdapter;
import com.feng.sauron.client.plugin.mybatis.MybatisTracerAdapter;
import com.feng.sauron.client.plugin.mybatis.interceptor.MybatisInterceptorTracerAdapter;
import com.feng.sauron.client.plugin.mysql.MysqlTracerAdapter;
import com.feng.sauron.client.plugin.redis.JedisTracerAdapter;
import com.feng.sauron.client.plugin.rocketmq.RocketMQProducerTracerAdapter;
import com.feng.sauron.client.plugin.spring.defaultservlet.SpringTracerAdapter;
import com.feng.sauron.client.plugin.spring.mvc.SpringMVCTracerAdapter;
import com.feng.sauron.client.plugin.web.WebTracerAdapter;
import com.feng.sauron.config.SauronConfig;
import com.feng.sauron.tracer.Tracer;
import com.feng.sauron.utils.JsonUtils;
import com.feng.sauron.utils.SauronLogUtils;
import com.feng.sauron.utils.SauronUtils;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年10月28日 上午11:02:04
 */
public abstract class TracerAdapterFactory implements Tracer {

	// public final static Logger logger = LoggerFactory.getLogger(TracerAdapterFactory.class);//当 当前系统使用的环境为 logback 的时候使用
	public final static Logger logger = SauronLogUtils.getSauronLogger();// 万能用法

	protected Class<?> returnType;
	public Object returnValue;
	public Class<?>[] paramClazz;
	public Object[] params;
	public String spanId;
	public STATUS status;
	public String methodName;
	public String className;
	public String sourceAppName = SauronConfig.getAPP_NAME();
	public String appName = SauronConfig.getAPP_NAME();
	public String type = SauronConfig.getAPP_NAME();
	public String detail = SauronConfig.getAPP_NAME();
	public long spanCount = 0;
	public Exception exception;
	// Tracer的实例对象池
	public Map<String, Tracer> tracerPool = new HashMap<String, Tracer>();

	private final static HashMap<String, TracerAdapterFactory> tracerMap = new HashMap<>();

	static {
		try {
			// local
			tracerMap.put(AnnotationTracerAdapter.TRACERNAME_STRING, AnnotationTracerAdapter.class.newInstance());
			tracerMap.put(MethodNameTracerAdapter.TRACERNAME_STRING, MethodNameTracerAdapter.class.newInstance());

			// spring
			tracerMap.put(SpringTracerAdapter.TRACERNAME_STRING, SpringTracerAdapter.class.newInstance());
			tracerMap.put(SpringMVCTracerAdapter.TRACERNAME_STRING, SpringMVCTracerAdapter.class.newInstance());

			// mybatis
			tracerMap.put(MybatisTracerAdapter.TRACERNAME_STRING, MybatisTracerAdapter.class.newInstance());
			tracerMap.put(MybatisInterceptorTracerAdapter.TRACERNAME_STRING, MybatisInterceptorTracerAdapter.class.newInstance());

			// web
			tracerMap.put(WebTracerAdapter.TRACERNAME_STRING, WebTracerAdapter.class.newInstance());

			// httpclient4
			tracerMap.put(HttpClient4TracerAdapter.TRACERNAME_STRING, HttpClient4TracerAdapter.class.newInstance());
			// rocketmq
			tracerMap.put(RocketMQProducerTracerAdapter.TRACERNAME_STRING, RocketMQProducerTracerAdapter.class.newInstance());

			// jdbc
			tracerMap.put(MysqlTracerAdapter.TRACERNAME_STRING, MysqlTracerAdapter.class.newInstance());

			// dubbo
			tracerMap.put(DubboTracerAdapter.TRACERNAME_STRING, DubboTracerAdapter.class.newInstance());

			// redis
			tracerMap.put(JedisTracerAdapter.TRACERNAME_STRING, JedisTracerAdapter.class.newInstance());

			// jdkHttp
			// tracerMap.put(JdkHttpClientTracerAdapter.TRACERNAME_STRING, JdkHttpClientTracerAdapter.class.newInstance());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract Tracer getAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params);

	public static Tracer get(String tracerName, String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {

		TracerAdapterFactory tracerAdapterFactory = tracerMap.get(tracerName);

		if (tracerAdapterFactory != null) {
			try {
				Tracer tracer = tracerAdapterFactory.getAdapter(spanId, className, methodName, sourceAppName, paramClazz, params);
				return tracer;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	public void setFailedStatus(STATUS status, Exception ex) {
		this.status = status;
		this.exception = ex;
	}

	public String getSpanId() {
		return spanId;
	}

	public String getNextSpanCount() {
		return "" + (++spanCount);
	}

	public void countDownSpanCount() {
		--spanCount;
	}

	public String getNextSpanId() {
		return getSpanId() + "." + getNextSpanCount();
	}

	@Override
	public void beforeMethodExecute() {
		for (String tracerName : tracerPool.keySet()) {
			if (isEnabledToTrace(tracerName))
				tracerPool.get(tracerName).beforeMethodExecute();
		}
	}

	@Override
	public void afterMethodExecute() {
		for (String tracerName : tracerPool.keySet()) {
			if (isEnabledToTrace(tracerName))
				tracerPool.get(tracerName).afterMethodExecute();
		}
	}

	@Override
	public void catchMethodException(Exception ex) {
		for (String tracerName : tracerPool.keySet()) {
			if (isEnabledToTrace(tracerName))
				tracerPool.get(tracerName).catchMethodException(ex);
		}
		setFailedStatus(STATUS.FAILED, ex);
	}

	@Override
	public void catchMethodExceptionFinally() {
		try {
			for (String tracerName : tracerPool.keySet()) {
				if (isEnabledToTrace(tracerName))
					tracerPool.get(tracerName).catchMethodExceptionFinally();
			}

			String printTraceLog = printTraceLog();
			if (printTraceLog != null) {
				logger.info(printTraceLog);
			}
			SauronSessionContext.removeTracerAdapter();
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println(e.fillInStackTrace());
		} catch (Throwable e) {
			System.out.println(e.fillInStackTrace());
			// e.printStackTrace();
		} finally {
		}
	}

	@Override
	public void catchMethodExceptionFinally(Class<?> returnType, Object returnValue) {
		this.returnType = returnType;
		this.returnValue = returnValue;

		try {
			for (String tracerName : tracerPool.keySet()) {
				if (isEnabledToTrace(tracerName))
					tracerPool.get(tracerName).catchMethodExceptionFinally();
			}

			String printTraceLog = printTraceLog();
			if (printTraceLog != null) {
				logger.info(printTraceLog);
			}
			SauronSessionContext.removeTracerAdapter();
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println(e.fillInStackTrace());
		} catch (Throwable e) {
			// e.printStackTrace();
			System.out.println(e.fillInStackTrace());
		} finally {
		}
	}

	@Override
	public String printTraceLog() {

		StringBuilder traceLog = new StringBuilder();

		traceLog.append("app|v3|{\"sauron\":{")//
				.append("\"traceId\":\"").append(SauronSessionContext.getTraceId()).append("\",")//
				.append("\"spanId\":\"").append(spanId).append("\",")//
				.append("\"appName\":\"").append(appName).append("\",")//
				.append("\"source\":\"").append(sourceAppName).append("\",")//
				.append("\"type\":\"").append(type).append("\",")//
				.append("\"detail\":\"").append(detail).append("\",")//
				.append("\"methodName\":\"").append(SauronUtils.getShortMethodName(methodName)).append("\"");//

		if (status.equals(STATUS.SUCCESS)) {
			traceLog.append(",\"result\":\"0\",\"exception\":\"\",");
		} else {
			String exceptString = "";
			if (exception != null) {
				exceptString = exception.toString().replaceAll("(\r\n|\r|\n|\n\r|\\\\|\")", " ");//此处不要对exception进行操作，以免造成 堆栈 信息被覆盖，，仅仅打印即可

			}
			traceLog.append(",\"result\":\"1\"").append(",\"exception\":\"").append(exceptString).append("\",");
		}

		traceLog.append("\"tracer\":{");
		for (String tracerName : tracerPool.keySet()) {
			if (isEnabledToTrace(tracerName))
				traceLog.append(tracerPool.get(tracerName).printTraceLog()).append(",");
		}
		if (traceLog.toString().endsWith(",")) {
			traceLog.deleteCharAt(traceLog.length() - 1);
		}

		traceLog.append("},");

		traceLog.append("\"params\":{");

		if (paramClazz != null && paramClazz.length != 0) {
			for (int i = 0; i < paramClazz.length; i++) {
				if (paramClazz[i].getSimpleName().contains("ServletResponse")) {
					continue;
				}
				traceLog.append("\"").append(paramClazz[i].getSimpleName()).append("_").append(i).append("\":").append(JsonUtils.toJSon(params[i])).append(",");
			}
		}

		if (traceLog.toString().endsWith(",")) {
			traceLog.deleteCharAt(traceLog.length() - 1);
		}
		traceLog.append("}");

		traceLog.append("}}");

		return traceLog.toString();

	}

	public boolean isEnabledToTrace(String tracerName) {
		if (!SauronSessionContext.getEnabledTracer().contains(tracerName)) {
			return false;
		}
		return true;
	}

	public enum STATUS {
		SUCCESS, FAILED
	}

}
