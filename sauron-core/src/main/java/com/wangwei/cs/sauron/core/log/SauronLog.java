package com.wangwei.cs.sauron.core.log;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTracerAdapterFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wei.wang@fengjr.com
 * @version 2017年1月19日 下午4:21:54
 * 
 */

public class SauronLog {

	private Logger sauronLogger;

	private SauronLog() {

		getLog();

	}

	public void getLog() {

		ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();

		String name = iLoggerFactory.getClass().getName();

		if (name.contains("Log4j")) {

			Log4jUtils.run();

		} else {

			LogBackUtils.run();
		}

		this.sauronLogger = LoggerFactory.getLogger(AbstractTracerAdapterFactory.class);

		System.out.println(sauronLogger);

	}

	private static class SauronLogInnerClass {
		private static final SauronLog SAURON_LOG_UTILS = new SauronLog();
	}

	@SuppressWarnings("unused")
	public static void run() {
		try {
			SauronLog sauronLogUtils = SauronLogInnerClass.SAURON_LOG_UTILS;
		} catch (Exception e) {
			System.out.println("sauronLogUtils start error ...");
			e.printStackTrace();
		}
	}

	public static Logger getSauronLogger() {

		return SauronLogInnerClass.SAURON_LOG_UTILS.sauronLogger;
	}

}
