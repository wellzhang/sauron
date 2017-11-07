package com.wangwei.cs.sauron.core.tracer.impl;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTracerAdapterFactory;
import com.wangwei.cs.sauron.core.utils.IdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年5月3日 下午4:18:38
 * 
 */
public class TracerLog {

	private final static Logger logger = LoggerFactory.getLogger(AbstractTracerAdapterFactory.class);// 要使用TracerAdapterFactory 的 logger

	private TracerLog() {
	}

	public static void save(String key, String result) {
		save(key, key + "_type", result);
	}

	public static void save(String key, String type, String result) {

		try {
			if (key == null || "".equals(key)) {
				return;
			}
			String tracerid = IdUtils.getInstance().nextId();
			String makeJsonBySb = makeJsonForAlarm(key, type, result, tracerid);
			logger.info(makeJsonBySb);
		} catch (Exception e) {
			System.err.println("TracerLog.save.exception ...");
		}
	}

	private static String makeJsonForAlarm(String key, String type, String result, String tracerid) {

		StringBuilder sb = new StringBuilder();

		sb.append("{\"Sauron\":{\"Type\":\"traceLog\",\"Version\":\"1\"");
		sb.append(",");
		sb.append("\"Key\":\"").append(key).append("\"");
		sb.append(",");
		sb.append("\"EsType\":\"").append(type).append("\"");
		sb.append(",");
		sb.append("\"Traceid\":\"").append(tracerid).append("\"");
		sb.append(",");
		sb.append("\"Result\":").append(result);
		sb.append("}}");

		String json = sb.toString();

		return json;
	}

	public static void main(String[] args) {
		TracerLog.save("crm_log", "{\"editable\":true,\"type\":\"dashlist\"}");
	}

}
