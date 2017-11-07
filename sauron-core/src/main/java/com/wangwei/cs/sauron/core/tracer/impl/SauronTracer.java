package com.wangwei.cs.sauron.core.tracer.impl;

import java.util.HashMap;
import java.util.Stack;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTracerAdapterFactory;
import com.wangwei.cs.sauron.core.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wangwei.cs.sauron.core.config.SauronConfig;
import com.wangwei.cs.sauron.core.utils.IdUtils;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年5月3日 下午4:18:38
 * 
 */
public class SauronTracer {

	private final static Logger logger = LoggerFactory.getLogger(AbstractTracerAdapterFactory.class);// 要使用TracerAdapterFactory 的 logger

	private final static ThreadLocal<Stack<TimerTracer>> TRACERS = new ThreadLocal<Stack<TimerTracer>>();

	private SauronTracer() {
	}

	/*
	 * key 仅用来对照 是否匹配,不参与最终逻辑，
	 */
	public static void start(String key) {

		try {
			TimerTracer timerTracer = new TimerTracer();
			timerTracer.startTimer();

			Stack<TimerTracer> stack = new Stack<TimerTracer>();
			stack.push(timerTracer);
			TRACERS.set(stack);

		} catch (Exception e) {
			System.err.println("SauronTracer.start exception ...");
		}
	}

	/*
	 * key 仅用来对照 是否匹配,不参与最终逻辑，
	 */
	public static void start() {

		try {
			TimerTracer timerTracer = new TimerTracer();
			timerTracer.startTimer();

			Stack<TimerTracer> stack = new Stack<TimerTracer>();
			stack.push(timerTracer);
			TRACERS.set(stack);

		} catch (Exception e) {
			System.err.println("SauronTracer.start exception ...");
		}

	}

	public static void end(String key, String result, boolean isSuccess) {// 就近匹配，不考虑用户 start 中key 是否匹配， start 中的key 仅用来对照 是否匹配

		try {
			if (TRACERS.get().isEmpty()) {
				System.err.println("SauronTracer.end 方法需要和SauronTracer.start() 配合使用... ");
				return;
			}

			if (key == null | "".equals(key)) {
				System.err.println("SauronTracer.end 方法需要标识key... ");
				return;
			}

			TimerTracer pop = TRACERS.get().pop();

			pop.stopTimer();

			String printTraceLog = pop.printTraceLog();

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

		sb.append("\"AppName\":\"").append(SauronConfig.getAppName()).append("\"");
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

		sb.append("\"AppName\":\"").append(SauronConfig.getAppName()).append("\"");
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
			SauronTracer.start("key1");
			Thread.sleep(2000L);

			SauronTracer.start("key2");

			Thread.sleep(3000L);

			HashMap<String, Object> sauron = new HashMap<>();
			sauron.put("errorCode\"\"\"", 1);
			String json = JsonUtils.toJSon(sauron);
			SauronTracer.end("key1", json, true);

			SauronTracer.end("key2", json, true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
