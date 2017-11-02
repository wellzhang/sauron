package com.fengjr.sauron.converger.kafka.handler;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import com.fengjr.sauron.commons.utils.DateUtils;
import com.fengjr.sauron.commons.utils.JsonUtils;
import com.fengjr.sauron.converger.util.InfluxdbUtils;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/8.
 */
public class JvmHandler implements BaseHandler {

	public static void main(String[] args) {
		JvmHandler jvmHandler = new JvmHandler();

		String line = "{\"Sauron\":{\"userDir\":\"C:\\\\new_fengjr_git\\\\fengjr-sauron\\\\sauron-core\",\"appName\":\"sauron\",\"FullGc.time\":0}}";
		String hostName = "10.255.73.161";
		String logTime = "2016-11-23 10:36:06";
		String version = "v3";

		jvmHandler.handle(line, hostName, logTime, version);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(String line, String hostName, String logTime, String version) {

		try {

			Map<String, Object> mapData = JsonUtils.getObject(line, Map.class);

			mapData.put("hostName", hostName);
			mapData.put("logTime", logTime);
			mapData.put("version", version);
			regJvm(mapData);
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	@SuppressWarnings("unchecked")
	private void regJvm(Map<String, Object> logData) {
		try {

			Map<String, Object> sauronMap = (Map<String, Object>) logData.get("Sauron");

			String logtime = (String) logData.get("logTime");

			Date logtimedDate = DateUtils.String2Date(logtime);

			String hostName = (String) logData.get("hostName");

			String userDir = (String) sauronMap.get("userDir");

			String appName = (String) sauronMap.get("appName");

			String pid = (String) sauronMap.get("pid");

			InfluxDB influxDB = InfluxdbUtils.getInfluxDB();

			BatchPoints batchPoints = InfluxdbUtils.getJvmBuilder().tag("hostName", hostName).tag("appName", appName.replace("-", "_")).tag("userDir", userDir).tag("pid", pid).build();

			Point.Builder time = Point.measurement(InfluxdbUtils.table_jvm).time(logtimedDate.getTime(), TimeUnit.MILLISECONDS);

			for (String key : sauronMap.keySet()) {

				if ((!"userDir".equals(key)) && (!"appName".equals(key)) && (!"pid".equals(key))) {
					String value = sauronMap.get(key).toString();
					if (value != null) {
						if (value.length() > 10) {
							value = value.substring(0, 9);
						}
						time.field(key, Double.parseDouble(value));
					}
				}
			}

			batchPoints.point(time.build());

			influxDB.write(batchPoints);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
