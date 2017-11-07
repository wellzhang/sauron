package com.feng.sauron.tracerImpl;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feng.sauron.client.plugin.TracerAdapterFactory;
import com.feng.sauron.config.SauronConfig;
import com.feng.sauron.utils.IdUtils;
import com.feng.sauron.utils.JsonUtils;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年5月3日 下午4:18:38
 * 
 */
@Deprecated
public class SauronTracerByMap {

	private final static Logger logger = LoggerFactory.getLogger(TracerAdapterFactory.class);// 要使用TracerAdapterFactory 的 logger

	private final static ThreadLocal<HashMap<String, TimerTracer>> tracers = new ThreadLocal<HashMap<String, TimerTracer>>();

	static {
		HashMap<String, TimerTracer> hashMap = tracers.get();
		if (hashMap == null) {
			tracers.set(new HashMap<String, TimerTracer>());
		}
	}

	private SauronTracerByMap() {
	}

	public static void start(String key) {

		try {
			TimerTracer timerTracer = new TimerTracer();
			timerTracer.startTimer();

			HashMap<String, TimerTracer> hashMap = new HashMap<String, TimerTracer>();
			hashMap.put(key, timerTracer);
			tracers.set(hashMap);
		} catch (Exception e) {
			System.err.println("SauronTracer.start exception ...");
		}

	}

	public static void end(String key, String result, boolean isSuccess) {

		try {
			if (tracers.get().isEmpty()) {
				System.err.println("SauronTracer.end 方法需要和SauronTracer.start() 配合使用... ");
				return;
			}

			if (key == null | "".equals(key)) {
				System.err.println("SauronTracer.end 方法需要标识key... ");
				return;
			}

			TimerTracer timerTracer = tracers.get().get(key);

			timerTracer.stopTimer();

			String printTraceLog = timerTracer.printTraceLog();

			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];

			String methodName = stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName();

			int lineNumber = stackTraceElement.getLineNumber();

			String tracerid = IdUtils.getInstance().nextId();

			if (key == null || "".equals(key)) {

				key = stackTraceElement.getMethodName();
			}

			String makeJsonBySb = makeJson(key, isSuccess, result, methodName, lineNumber, tracerid, printTraceLog);

			logger.info(makeJsonBySb);

		} catch (Exception e) {
			System.err.println("SauronTracer.end exception ...");
		}

	}

	public static void end(String key, String result) {
		end(key, result, true);
	}

	public static void alarm(String key, String result) {

		try {

			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];

			String methodName = stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName();

			int lineNumber = stackTraceElement.getLineNumber();

			String tracerid = IdUtils.getInstance().nextId();

			if (key == null || "".equals(key)) {
				key = stackTraceElement.getMethodName();
			}

			String makeJsonBySb = makeJsonForAlarm(key, result, methodName, lineNumber, tracerid, System.currentTimeMillis());

			logger.info(makeJsonBySb);

		} catch (Exception e) {
			System.err.println("SauronTracer.alarm exception ...");
		}

	}

	private static String makeJson(String key, boolean isSuccess, String result, String methodName, int lineNumber, String tracerid, String time) {

		StringBuilder sb = new StringBuilder("codeBulk|v3|");

		sb.append("{\"sauron\":{");

		sb.append("\"AppName\":\"").append(SauronConfig.getAPP_NAME()).append("\"");
		sb.append(",");
		sb.append("\"Traceid\":\"").append(tracerid).append("\"");
		sb.append(",");
		sb.append("\"Key\":\"").append(key).append("\"");
		sb.append(",");
		sb.append("\"MethodName\":\"").append(methodName).append("\"");
		sb.append(",");
		sb.append("\"ATracerAdapter\":{").append(time).append("}");
		sb.append(",");
		sb.append("\"LineNumber\":\"").append(lineNumber).append("\"");
		sb.append(",");
		sb.append("\"Result\":\"").append(result.replaceAll("(\r\n|\r|\n|\n\r|\\\\|\")", " ")).append("\"");
		sb.append(",");
		sb.append("\"IsSuccess\":\"").append(isSuccess ? 0 : 1).append("\"");

		sb.append("}}");

		String json = sb.toString();

		return json;
	}

	private static String makeJsonForAlarm(String key, String result, String methodName, int lineNumber, String tracerid, long time) {

		StringBuilder sb = new StringBuilder("alarm|v3|");

		sb.append("{\"sauron\":{");

		sb.append("\"AppName\":\"").append(SauronConfig.getAPP_NAME()).append("\"");
		sb.append(",");
		sb.append("\"Traceid\":\"").append(tracerid).append("\"");
		sb.append(",");
		sb.append("\"Key\":\"").append(key).append("\"");
		sb.append(",");
		sb.append("\"MethodName\":\"").append(methodName).append("\"");
		sb.append(",");
		sb.append("\"Time\":\"").append(time).append("\"");
		sb.append(",");
		sb.append("\"LineNumber\":\"").append(lineNumber).append("\"");
		sb.append(",");
		sb.append("\"Result\":\"").append(result.replaceAll("(\r\n|\r|\n|\n\r|\\\\|\")", " ")).append("\"");

		sb.append("}}");

		String json = sb.toString();

		return json;
	}

	public static void main(String[] args) {
		try {
			SauronTracerByMap.start("key1");
			Thread.sleep(2000L);

			SauronTracerByMap.start("key2");

			Thread.sleep(3000L);

			HashMap<String, Object> sauron = new HashMap<>();
			sauron.put("errorCode\"\"\"", 1);
			String json = JsonUtils.toJSon(sauron);
			SauronTracerByMap.end("key1", json, true);

			SauronTracerByMap.end("key2", json, true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
