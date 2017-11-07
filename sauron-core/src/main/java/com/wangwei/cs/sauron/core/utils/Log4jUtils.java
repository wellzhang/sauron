package com.wangwei.cs.sauron.core.utils;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTracerAdapterFactory;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.wangwei.cs.sauron.core.config.SauronConfig;

/**
 * @author wei.wang@fengjr.com
 * @version 2017年1月19日 下午4:20:02
 * 
 */
public class Log4jUtils {

	private Log4jUtils() {

		Logger logger = Logger.getLogger(AbstractTracerAdapterFactory.class); // 生成新的Logger

		logger.removeAllAppenders(); // 清空Appender，特別是不想使用现存实例时一定要初始化

		logger.setLevel(Level.INFO); // 设定Logger級別。

		logger.setAdditivity(false); // 设定是否继承父Logger。默认为true，继承root输出；设定false后将不出书root。

		RollingFileAppender appender = new RollingFileAppender(); // 生成新的Appender

		PatternLayout layout = new PatternLayout();

		layout.setConversionPattern("%d{yyyy-MM-dd HH:mm:ss}|%m%n"); // log的输出形式

		appender.setLayout(layout);

		appender.setFile("/export/log/flume-agent/sauron/metrics." + SauronConfig.getAppName() + "_" + SauronUtils.getPid() + ".log"); // log输出路径

		appender.setEncoding("UTF-8"); // log的字符编码

		appender.setAppend(true); // 日志合并方式： true:在已存在log文件后面追加 false:新log覆盖以前的log

		appender.activateOptions(); // 适用当前配置

		// appender.setImmediateFlush(false); 为false 时候 会遇到打印一半的情况

		appender.setMaxBackupIndex(6);

		appender.setMaxFileSize("100MB");

		AsyncAppender asyncAppender = new AsyncAppender();

		asyncAppender.setBufferSize(2048);

		asyncAppender.addAppender(appender);

		logger.addAppender(asyncAppender); // 将新的Appender加到Logger中

	}

	private static class Log4jInnerClass {
		private static final Log4jUtils LOG_LOG4J_UTILS = new Log4jUtils();
	}

	public static void run() {

		try {

			System.out.println("------------------------当前日志组件为：" + Log4jInnerClass.LOG_LOG4J_UTILS);

		} catch (Exception e) {

			System.out.println("log4jUtils start error ...");

			e.printStackTrace();
		}
	}

}
