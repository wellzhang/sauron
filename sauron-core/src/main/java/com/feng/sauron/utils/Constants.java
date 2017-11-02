package com.feng.sauron.utils;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年11月2日 下午1:22:33
 * 
 */
public final class Constants {
	public static String METHOD_RESOLVE_REGEXP = "([\\.\\w$]+)(:([\\|\\w\\.\\(\\),]+)+)*";
	public static String METHOD_SPLITER = "\\|";
	public static String CLASS_SPLITER = "#";
	public static String TRACER_SPLITER = ",";
	public static String SAURON_METHODNAME = "sauron-methods";
	public static String SAURON_SQL = "sauron-sql";
	public static String SAURON_TRACERS = "sauron-tracers";
	public static String SAURON_SAMPLING_RATE = "sauron-samplingrate";
	public static String SAURON_DEFAULT_SAMPLING_RATE = "100";
	public static String SAURON_DEFAULT_SAURON_TRACERS = "com.feng.sauron.tracerImpl.TimerTracer";
	public static String SAURON_SWITCH = "sauron-switch";
	public static String SAURON_SWITCH_STATUS_ON = "ON";
	public static String SAURON_SWITCH_STATUS_OFF = "OFF";
	public static String SAURON_REQUEST_TRACEID = "SAURON_TRACEID";
	public static String SAURON_REQUEST_SPANID = "SAURON_SPANID";
	public static String SAURON_REQUEST_SOURCE_APPNAME = "SOURCE_APPNAME";

}
