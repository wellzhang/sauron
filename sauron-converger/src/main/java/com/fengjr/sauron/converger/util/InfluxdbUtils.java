package com.fengjr.sauron.converger.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.BatchPoints.Builder;
import org.influxdb.dto.Query;

public class InfluxdbUtils {

	public static String dbName;
	private static String username;
	private static String password;
	private static String url;
	private static String retentionPolicy;
	private static AtomicBoolean flag = new AtomicBoolean(true);

	public static final String table = "sauron";
	public static final String table_jvm = "sauron_jvm";
	public static final String table_sys = "sauron_sys";
	public static final String table_h5 = "sauron_h5";

	static {

		try {
			Properties properties = new Properties();
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("influxdb-config.properties");
			properties.load(inputStream);
			username = (String) properties.get("username");
			password = (String) properties.get("password");
			url = (String) properties.get("url");
			dbName = (String) properties.get("dbname");
			retentionPolicy = dbName + "_retentionPolicy";
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static class InfluxDBInnerClass {
		private final static InfluxDB influxDB = InfluxDBFactory.connect(url, username, password);
	}

	public static InfluxDB getInfluxDB() {

		InfluxDB influxDB = InfluxDBInnerClass.influxDB;

		if (flag.get()) {

			influxDB.createDatabase(dbName);

			influxDB.createDatabase(dbName + "_jvm");
			
			influxDB.createDatabase(dbName + "_sys");

			influxDB.createDatabase(dbName + "_h5");

			influxDB.query(new Query("CREATE RETENTION POLICY " + retentionPolicy + " ON " + dbName + " DURATION 15d REPLICATION 1 DEFAULT", dbName));

			influxDB.query(new Query("CREATE RETENTION POLICY " + retentionPolicy + " ON " + dbName + "_sys" + " DURATION 365d REPLICATION 1 DEFAULT", dbName + "_sys"));
			
			influxDB.query(new Query("CREATE RETENTION POLICY " + retentionPolicy + " ON " + dbName + "_jvm" + " DURATION 30d REPLICATION 1 DEFAULT", dbName + "_jvm"));

			influxDB.query(new Query("CREATE RETENTION POLICY " + retentionPolicy + " ON " + dbName + "_h5" + " DURATION 15d REPLICATION 1 DEFAULT", dbName + "_h5"));

			flag.set(false);
		}

		return influxDB;
	}

	public static Builder getBuilder() {
		return BatchPoints.database(dbName).retentionPolicy(retentionPolicy).consistency(ConsistencyLevel.ALL);
	}

	public static Builder getH5Builder() {
		return BatchPoints.database(dbName + "_h5").retentionPolicy(retentionPolicy).consistency(ConsistencyLevel.ALL);
	}

	public static Builder getJvmBuilder() {
		return BatchPoints.database(dbName + "_jvm").retentionPolicy(retentionPolicy).consistency(ConsistencyLevel.ALL);
	}
	
	public static Builder getSysBuilder() {
		return BatchPoints.database(dbName + "_sys").retentionPolicy(retentionPolicy).consistency(ConsistencyLevel.ALL);
	}
}
