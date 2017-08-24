package com.fengjr.sauron.commons.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by bingquan.an@fengjr.com on 2015/7/29.
 */
public class DateUtils {

	public final static String DEFAULT_MSSTRING_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
	public final static String DEFAULT_MSSTRING_PATTERN_TIMEZONE = "yyyy-MM-dd HH:mm:ss.SSS";
	// public final static String DEFAULT_MSSTRING_PATTERN_TIMEZONE = "yyyy-MM-dd HH:mm:ss";
	public final static String DEFAULT_DATESTRING_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public final static String DAY_DATESTRING_PATTERN = "yyyy-MM-dd";
	public final static String HOUR_DATESTRING_PATTERN = "yyyy-MM-dd_HH";
	public final static String MINUTE_DATESTRING_PATTERN = "yyyy-MM-dd_HH:mm";
	public final static String MINUTE_ABBR_PATTERN = "HH:mm";
	public final static String HOURS_ABBR_PATTERN = "HH";

	public static String Date2String(Date date) {
		return Date2String(date, DEFAULT_DATESTRING_PATTERN);
	}

	public static Date String2Date(String dateString) throws Exception {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATESTRING_PATTERN);
		return simpleDateFormat.parse(dateString);
	}

	public static Date String2Date(String dateString, String pattern) throws Exception {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.parse(dateString);
	}

	public static String Date2String(Date date, String pattern) {
		DateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	public static String getCurrentDateString_hours(Date date) {
		return Date2String(date, HOUR_DATESTRING_PATTERN);
	}

	public static String getCurrentDateString_minute(Date date) {
		return Date2String(date, MINUTE_DATESTRING_PATTERN);
	}

	public static String getCurrentDateStringMinuteAbbr(Date date) {
		return Date2String(date, MINUTE_ABBR_PATTERN);
	}

	public static String getCurrentDateStringHoursAbbr(Date date) {
		return Date2String(date, HOURS_ABBR_PATTERN);
	}

	public static String getCurrentDateString_minute(String dateStr) {
		return StringUtils.replace(StringUtils.substring(dateStr.trim(), 0, 16), " ", "_");
	}

	public static long nextHourDelayMs() {
		Date day = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);
		// cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
		return cal.getTime().getTime() - day.getTime() + 8;
	}

	public static long nextMinuteDelayMs() {
		Date day = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);
		// cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
		// cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
		return cal.getTime().getTime() - day.getTime() + 8;
	}

	public static void main(String args[]) {
		System.out.print(nextMinuteDelayMs());
	}
}
