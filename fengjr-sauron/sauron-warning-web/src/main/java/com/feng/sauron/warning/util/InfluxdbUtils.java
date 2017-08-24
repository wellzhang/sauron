package com.feng.sauron.warning.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

public class InfluxdbUtils {

	public static String dbName_sys;
	public static String dbName_jvm;
	public static String dbName;
	private static String username;
	private static String password;
	private static String url;

	public static final String table = "sauron_home_count";

	static {

		try {
			Properties properties = new Properties();
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("influxdb-config.properties");
			properties.load(inputStream);
			username = (String) properties.get("influxdb.username");
			password = (String) properties.get("influxdb.password");
			url = (String) properties.get("influxdb.url");
			dbName = (String) properties.get("influxdb.app.dbname");
			dbName_jvm = (String) properties.get("influxdb.jvm.dbname");
			dbName_sys = (String) properties.get("influxdb.sys.dbname");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static class InfluxDBInnerClass {
		private final static InfluxDB influxDB = InfluxDBFactory.connect(url, username, password);
	}

	public static InfluxDB getInfluxDB() {

		InfluxDB influxDB = InfluxDBInnerClass.influxDB;

		return influxDB;
	}

}
