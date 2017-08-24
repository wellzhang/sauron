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

public class InfluxdbUtilsForPhone {

	public static String dbName_phone;
	private static String username_phone;
	private static String password_phone;
	private static String url_phone;
	private static String retentionPolicy_phone;

	private static AtomicBoolean flag_phone = new AtomicBoolean(true);

	public static final String table_phone_ori = "sauron_phone_perf";

	static {

		try {
			Properties properties = new Properties();
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("influxdb-config.properties");
			properties.load(inputStream);

			username_phone = (String) properties.get("username_phone");
			password_phone = (String) properties.get("password_phone");
			url_phone = (String) properties.get("url_phone");
			dbName_phone = (String) properties.get("dbname_phone");
			retentionPolicy_phone = dbName_phone + "_retentionPolicy_phone";

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static class InfluxDBInnerClass {
		private final static InfluxDB influxDB_phone = InfluxDBFactory.connect(url_phone, username_phone, password_phone);
	}

	public static InfluxDB getInfluxDB_phone() {

		InfluxDB influxDB = InfluxDBInnerClass.influxDB_phone;

		if (flag_phone.get()) {

			influxDB.createDatabase(dbName_phone);

			influxDB.query(new Query("CREATE RETENTION POLICY " + retentionPolicy_phone + " ON " + dbName_phone + " DURATION 15d REPLICATION 1 DEFAULT", dbName_phone));

			flag_phone.set(false);
		}

		return influxDB;
	}

	public static Builder getPhoneOriBuilder() {
		return BatchPoints.database(dbName_phone).retentionPolicy(retentionPolicy_phone).consistency(ConsistencyLevel.ALL);
	}

}
