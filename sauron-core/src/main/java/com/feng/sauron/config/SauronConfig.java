package com.feng.sauron.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Liuyb on 2015/10/20.
 */

public class SauronConfig {
	private final static Logger log = LoggerFactory.getLogger(SauronConfig.class);
	private static String APP_NAME;

	static {
		try {
			Properties properties = new Properties();
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("sauron-config.properties");
			properties.load(inputStream);
			APP_NAME = (String) properties.get("sauron.appname");
			if (APP_NAME == null) {
				throw new IllegalArgumentException("Sauron配置文件数据不全或存在问题！");
			}
		} catch (Exception e) {
			log.info("sauron-config.properties未配置或出错导致无法初始化，赋值默认名称：Sauron！");
			APP_NAME = "sauron";
		}
	}

	public static String getAPP_NAME() {
		return APP_NAME;
	}

}
