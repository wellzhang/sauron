package com.feng.sauron.utils;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feng.sauron.client.plugin.TracerAdapterFactory;

/**
 * @author wei.wang@fengjr.com
 * @version 2017年1月19日 下午4:21:54
 * 
 */

public class SauronLogUtils {

	private Logger sauronLogger;

	private SauronLogUtils() {

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

		this.sauronLogger = LoggerFactory.getLogger(TracerAdapterFactory.class);

		System.out.println(sauronLogger);

	}

	private static class SauronLogInnerClass {
		private static final SauronLogUtils SAURON_LOG_UTILS = new SauronLogUtils();
	}

	@SuppressWarnings("unused")
	public static void run() {
		try {
			SauronLogUtils sauronLogUtils = SauronLogInnerClass.SAURON_LOG_UTILS;
		} catch (Exception e) {
			System.out.println("sauronLogUtils start error ...");
			e.printStackTrace();
		}
	}

	public static Logger getSauronLogger() {

		return SauronLogInnerClass.SAURON_LOG_UTILS.sauronLogger;
	}

}
