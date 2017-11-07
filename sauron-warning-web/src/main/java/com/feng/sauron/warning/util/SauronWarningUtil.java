package com.feng.sauron.warning.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年1月12日 下午7:49:50
 * 
 */
public class SauronWarningUtil {

	private static final Logger logger = LoggerFactory.getLogger(SauronWarningUtil.class);

	private static final String POST = "post";
	private static final String UTF8 = "utf-8";

	private static final String patternByMaps = "&";
	private static final String patternByKeyValues = "=";

	/** DES_KEY */
	public static final String DES_KEY = "wang!@#$%^&*()wei_test";

	public static String getLocation(HttpServletRequest request) {
		String param = getQueryParam(request);
		if (StringUtils.isBlank(param)) {
			return request.getRequestURI();
		}
		return request.getRequestURI() + "?" + param;
	}

	public static String getQueryParam(HttpServletRequest request) {
		if (request.getMethod().equalsIgnoreCase(POST)) {
			return map2String(getQueryParams(request));
		} else {
			String s = request.getQueryString();
			if (StringUtils.isBlank(s)) {
				return null;
			}
			try {
				s = URLDecoder.decode(s, UTF8);
			} catch (UnsupportedEncodingException e) {
				logger.error("encoding " + UTF8 + " not support?", e);
			}
			return s;
		}
	}

	/**
	 * 将map中key,value值转换为字符串，Map.Entry之间默认分隔符<b> ${patternByMaps} </b>联接,key,value之间默认分隔符<b> ${patternByKeyValues} </b>联接.
	 * 
	 * @param params
	 * @param patternByMaps
	 * @param patternByKeyValues
	 */
	public static String map2String(Map<String, ? extends Object> params, String patternByMaps, String patternByKeyValues, boolean valueHasQuotationMarks) {
		int count = 0;
		StringBuilder builder = new StringBuilder();
		for (Entry<String, ? extends Object> entry : params.entrySet()) {
			if (count > 0) {
				builder.append(patternByMaps);
			}
			builder.append(entry.getKey());
			builder.append(patternByKeyValues);
			if (valueHasQuotationMarks) {
				builder.append("\"" + entry.getValue() + "\"");
			} else {
				builder.append(entry.getValue());
			}
			count++;
		}
		return builder.toString();
	}

	/**
	 * 将map中key,value值转换为字符串，Map.Entry之间默认分隔符<b> & </b>联接,key,value之间默认分隔符 <b> = </b>联接.
	 * 
	 * @param params
	 * @return 字符串
	 */
	public static String map2String(Map<String, ? extends Object> params) {
		return map2String(params, patternByMaps, patternByKeyValues, false);
	}

	public static Map<String, Object> getQueryParams(HttpServletRequest request) {
		Map<String, String[]> map;
		if (request.getMethod().equalsIgnoreCase(POST)) {
			map = request.getParameterMap();
		} else {
			String s = request.getQueryString();
			if (StringUtils.isBlank(s)) {
				return new HashMap<String, Object>();
			}
			try {
				s = URLDecoder.decode(s, UTF8);
			} catch (UnsupportedEncodingException e) {
				logger.error("encoding " + UTF8 + " not support?", e);
			}
			map = parseQueryString(s);
		}

		Map<String, Object> params = new HashMap<String, Object>(map.size());
		int len;
		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			len = entry.getValue().length;
			if (len == 1) {
				params.put(entry.getKey(), entry.getValue()[0]);
			} else if (len > 1) {
				params.put(entry.getKey(), entry.getValue());
			}
		}
		return params;
	}

	public static Map<String, String[]> parseQueryString(String s) {
		String valArray[] = null;
		if (s == null) {
			throw new IllegalArgumentException();
		}
		Map<String, String[]> ht = new HashMap<String, String[]>();
		StringTokenizer st = new StringTokenizer(s, "&");
		while (st.hasMoreTokens()) {
			String pair = st.nextToken();
			int pos = pair.indexOf('=');
			if (pos == -1) {
				continue;
			}
			String key = pair.substring(0, pos);
			String val = pair.substring(pos + 1, pair.length());
			if (ht.containsKey(key)) {
				String oldVals[] = ht.get(key);
				valArray = new String[oldVals.length + 1];
				for (int i = 0; i < oldVals.length; i++) {
					valArray[i] = oldVals[i];
				}
				valArray[oldVals.length] = val;
			} else {
				valArray = new String[1];
				valArray[0] = val;
			}
			ht.put(key, valArray);
		}
		return ht;
	}

}
