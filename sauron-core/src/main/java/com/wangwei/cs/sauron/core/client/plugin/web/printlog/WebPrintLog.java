package com.wangwei.cs.sauron.core.client.plugin.web.printlog;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import com.wangwei.cs.sauron.core.client.plugin.PrintTraceLog;
import com.wangwei.cs.sauron.core.client.plugin.AbstractTracerAdapterFactory;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年10月31日 下午1:53:42
 * 
 */
public class WebPrintLog implements PrintTraceLog {

	private WebPrintLog() {
	}

	private static class InnerClass {
		private static final WebPrintLog INSTANCE = new WebPrintLog();
	}

	public static WebPrintLog getInstances() {
		return InnerClass.INSTANCE;
	}

	@Override
	public String print(AbstractTracerAdapterFactory tracerAdapterFactory) {

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
