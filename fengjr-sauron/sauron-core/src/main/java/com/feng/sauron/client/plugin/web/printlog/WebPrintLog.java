package com.feng.sauron.client.plugin.web.printlog;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import com.feng.sauron.client.plugin.PrintTraceLog;
import com.feng.sauron.client.plugin.TracerAdapterFactory;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2016年10月31日 下午1:53:42
 * 
 */
public class WebPrintLog implements PrintTraceLog {

	private WebPrintLog() {
	}

	private static class InnerClass {
		private static final WebPrintLog Inner_Class = new WebPrintLog();
	}

	public static WebPrintLog getInstances() {
		return InnerClass.Inner_Class;
	}

	@Override
	public String print(TracerAdapterFactory tracerAdapterFactory) {

		try {

			if (tracerAdapterFactory.params != null && tracerAdapterFactory.params[0] instanceof HttpServletRequest) {

				HttpServletRequest request = (HttpServletRequest) tracerAdapterFactory.params[0];

				StringBuffer requestURL = request.getRequestURL();

				String domain = requestURL.toString().replace(request.getRequestURI(), "");

				tracerAdapterFactory.detail = domain;

				if (!tracerAdapterFactory.sourceAppName.equals(tracerAdapterFactory.appName)) {
					tracerAdapterFactory.type += "_in";
				}

				tracerAdapterFactory.methodName = domain;

				tracerAdapterFactory.paramClazz = new Class[] { URI.class, String.class };

				tracerAdapterFactory.params = new Object[] { requestURL.toString(), request.getParameterMap() };

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
