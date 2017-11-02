package com.feng.sauron.warning.web.base;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 返回对象
 * 
 * @author bingquan.an
 * 
 */
public class ResponseObject {

	public static final String SUCCESS = "200"; // 成功

	public static final String FAIL = "499"; // 失败
	public final static String STATUS = "status";
	public final static String STATUS_TEXT = "statusText";
	public String isSuccess = SUCCESS;
	private String error;

	private String response;

	private Map<String, Object> data = new HashMap<String, Object>();

	public ResponseObject() {

	}

	@SuppressWarnings("unchecked")
	public ResponseObject(Object value) {
		this();
		this.putAll((Map<String, Object>) value);
	}

	public ResponseObject(Map<String, Object> map) {
		this();
		this.putAll(map);
	}

	public void putAll(Map<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			this.putStatusAndData(entry.getKey(), entry.getValue());
		}
		for (Entry<String, Object> entry : map.entrySet()) {
			this.putStatusText(entry.getKey(), entry.getValue());
		}
	}

	public void putStatusText(String key, Object value) {
		if (key.equals(STATUS_TEXT)) {
			String statusText = value.toString();
			if (StringUtils.isNotBlank(statusText)) {
				if (this.getIsSuccess().equals(SUCCESS))
					this.setResponse(value.toString());
				if (this.getIsSuccess().equals(FAIL))
					this.setError(value.toString());
			}
		}
	}

	public void putStatusAndData(String key, Object value) {
		if (key.equals(STATUS)) {
			this.setIsSuccess(value.toString());
		} else {
			this.data.put(key, value);
		}
	}

	public String getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

}
