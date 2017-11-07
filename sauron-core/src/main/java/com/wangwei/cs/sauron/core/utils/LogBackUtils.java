package com.wangwei.cs.sauron.core.utils;

import java.nio.charset.Charset;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTracerAdapterFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年10月28日 下午6:20:21
 * 
 */
public class LogBackUtils {

	private LogBackUtils() {

		LoggerContext loggerContext = null;
		try {
			loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();// 容易出现 log4j 强转 logback
		} catch (Exception e) {
			// 容易出现 log4j 强转 logback , 此时直接使用LoggerContext
			System.err.println("this system is using log4j ...");
			loggerContext = new LoggerContext();
		}

		Logger tracerAdapter = loggerContext.getLogger(AbstractTracerAdapterFactory.class);
		tracerAdapter.detachAndStopAllAppenders();

		// appender
		RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();
		fileAppender.setContext(loggerContext);
		fileAppender.setName("sauron-rolling");

		// fileAppender.setFile(file);
		fileAppender.setPrudent(true);// setPrudent和setFile不可以同时存在... prudent 可以控制文件权限冲突

		fileAppender.setAppend(true);

		// policy
		TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<ILoggingEvent>();
		policy.setContext(loggerContext);
		policy.setMaxHistory(6);
		policy.setFileNamePattern("/export/log/flume-agent/sauron/metrics.%d{yyyy-MM-dd_HH}.log");
		policy.setParent(fileAppender);

		policy.start();
		fileAppender.setRollingPolicy(policy);

		// encoder
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(loggerContext);
		encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss}|%msg%n");
		Charset forName = Charset.forName("utf-8");
		encoder.setCharset(forName);
		encoder.start();
		fileAppender.setEncoder(encoder);

		fileAppender.start();

		AsyncAppender asyncAppender = new AsyncAppender();

		asyncAppender.setContext(loggerContext);
		asyncAppender.addAppender(fileAppender);
		asyncAppender.setName("sauron-asynAppender");
		asyncAppender.start();

		tracerAdapter.addAppender(asyncAppender);
		tracerAdapter.setLevel(Level.toLevel("INFO"));
		tracerAdapter.setAdditive(false);

	}

	private static class LogbackInnerClass {
		private static final LogBackUtils LOG_BACK_UTILS = new LogBackUtils();
	}

	public static void run() {

		try {

			System.out.println("------------------------当前日志组件为：" + LogbackInnerClass.LOG_BACK_UTILS);

		} catch (Exception e) {

			System.out.println("LogBackUtils start error ...");

			e.printStackTrace();
		}
	}

}
