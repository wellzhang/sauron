package com.wangwei.cs.sauron.core.utils;

import com.alibaba.fastjson.JSON;

public final class JsonUtils {

	public static String toJSon(Object object) {

		try {
			String json = JSON.toJSONString(object);
			if (json.length() > 500) {
				return "\"tooLong\"";
			} else {
				return json;
			}
		} catch (Exception e) {
			return "\"Could not resolve!\"";
		} catch (Throwable e) {
			return "\"Could not resolve!\"";
		}
	}

	public static String toLongJSon(Object object) {

		try {
			String json = JSON.toJSONString(object);

			return json;

		} catch (Exception e) {
			return "\"Could not resolve!\"";
		} catch (Throwable e) {
			return "\"Could not resolve!\"";
		}
	}

}