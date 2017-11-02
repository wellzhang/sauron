package com.fengjr.sauron.converger.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.BatchPoints.Builder;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfluxdbUtilsForH5 {

	public static String dbName_h5;
	private static String username_h5;
	private static String password_h5;
	private static String url_h5;
	private static String retentionPolicy_h5;

	private static AtomicBoolean flag_h5 = new AtomicBoolean(true);

	public static final String table_h5_ori = "sauron_h5_ori";
	public static final String table_h5_perf = "sauron_h5_perf";

	private static ConcurrentHashSet<String> tables = new ConcurrentHashSet<>();

	private static Logger logger = LoggerFactory.getLogger(InfluxdbUtils.class);

	static {

		try {
			Properties properties = new Properties();
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("influxdb-config.properties");
			properties.load(inputStream);

			username_h5 = (String) properties.get("username_h5");
			password_h5 = (String) properties.get("password_h5");
			url_h5 = (String) properties.get("url_h5");
			dbName_h5 = (String) properties.get("dbname_h5");
			retentionPolicy_h5 = dbName_h5 + "_retentionPolicy_h5";

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static class InfluxDBInnerClass {
		private final static InfluxDB influxDB_h5 = InfluxDBFactory.connect(url_h5, username_h5, password_h5);
	}

	public static InfluxDB getInfluxDB_h5() {

		InfluxDB influxDB = InfluxDBInnerClass.influxDB_h5;

		if (flag_h5.get()) {

			influxDB.createDatabase(dbName_h5);

			influxDB.query(new Query("CREATE RETENTION POLICY " + retentionPolicy_h5 + " ON " + dbName_h5 + " DURATION 7d REPLICATION 3 DEFAULT", dbName_h5));

			flag_h5.set(false);
		}

		return influxDB;
	}

	public static Builder getH5OriBuilder() {
		return BatchPoints.database(dbName_h5).retentionPolicy(retentionPolicy_h5).consistency(ConsistencyLevel.ALL);
	}
	
	public static void createContinuousQueryForH5() {

		try {
			if (!tables.contains(table_h5_ori)) {
				QueryResult query = getInfluxDB_h5().query(
						new Query("CREATE CONTINUOUS QUERY cq_" + table_h5_ori 
								+ " ON " 
								+ dbName_h5   
								+ " BEGIN " 
								+ "SELECT " + "count(\"duration\") AS count , " 
								+ "max(\"duration\") AS tp100 , " 
								+ "min(\"duration\") AS tp0 , " 
								+ "mean(\"duration\") AS avg ,  "
								+ "percentile(\"duration\", 90) AS tp90,  " 
								+ "percentile(\"duration\", 99) AS tp99 , " 
								+ "percentile(\"duration\", 99.9) AS tp999  " 
								+ "INTO \"" 
								+ table_h5_perf 
								+ "\" FROM  " + table_h5_ori 
								+ " GROUP BY time(1m) , method , hostName , appName " 
								+ "END",
								dbName_h5  ));

				tables.add(table_h5_ori);
				logger.info("createContinuousQuery : " + table_h5_ori + query);
			}
		} catch (Exception e) {
			logger.info("createContinuousQuery : " + table_h5_ori, e);
		}
	}

	public static void main(String[] args) throws Exception {

		InfluxDB influxDB = getInfluxDB_h5();

		QueryResult query = influxDB.query(new Query("show servers", dbName_h5));
		
		System.out.println(query);
		
		

		 

	}
}
