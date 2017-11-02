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
 * @author wei.wang@fengjr.com
 * @version 2016年11月23日 下午1:43:40
 * 
 */
public class SystemHandler implements BaseHandler {

	@SuppressWarnings("unchecked")
	@Override
	public void handle(String line, String hostName, String logTime, String version) {

		try {
			Map<String, Object> mapData = JsonUtils.getObject(line, Map.class);
			mapData.put("hostName", hostName);
			mapData.put("logTime", logTime);
			mapData.put("version", version);
			regSys(mapData);
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	@SuppressWarnings("unchecked")
	private void regSys(Map<String, Object> logData) {
		try {

			Map<String, Object> sauronMap = (Map<String, Object>) logData.get("sauron");

			String logtime = (String) logData.get("logTime");

			Date logtimedDate = DateUtils.String2Date(logtime);

			String hostName = (String) logData.get("hostName");

			String userDir = (String) sauronMap.get("userDir");

			String appName = (String) sauronMap.get("appName");

			String pid = (String) sauronMap.get("pid");

			InfluxDB influxDB = InfluxdbUtils.getInfluxDB();

			BatchPoints batchPoints = InfluxdbUtils.getSysBuilder().tag("hostName", hostName).tag("appName", appName.replace("-", "_")).tag("userDir", userDir).tag("pid", pid).build();

			Point.Builder time = Point.measurement(InfluxdbUtils.table_sys).time(logtimedDate.getTime(), TimeUnit.MILLISECONDS);

			for (String key : sauronMap.keySet()) {

				if ((!"userDir".equals(key)) && (!"appName".equals(key)) && (!"pid".equals(key))) {
					String value = sauronMap.get(key).toString();
					if (value != null) {
						time.field(key, value);
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
