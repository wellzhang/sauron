package com.feng.sauron.client.plugin.httpclient.httpclient4.printlog;

import java.net.URI;

import org.apache.http.client.methods.HttpUriRequest;

import com.feng.sauron.client.plugin.PrintTraceLog;
import com.feng.sauron.client.plugin.TracerAdapterFactory;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2016年10月31日 下午1:53:42
 * 
 */
public class CloseableHttpClientPrintLog implements PrintTraceLog {

	private CloseableHttpClientPrintLog() {
	}

	private static class InnerClass {
		private static final CloseableHttpClientPrintLog Inner_Class = new CloseableHttpClientPrintLog();
	}

	public static CloseableHttpClientPrintLog getInstances() {
		return InnerClass.Inner_Class;
	}

	@Override
	public String print(TracerAdapterFactory tracerAdapterFactory) {

		try {
			if (tracerAdapterFactory.params[0] instanceof HttpUriRequest) {
				HttpUriRequest httpUriRequest = (HttpUriRequest) tracerAdapterFactory.params[0];
				URI uri = httpUriRequest.getURI();
				tracerAdapterFactory.detail = uri.getHost() + ":" + uri.getPort();

				tracerAdapterFactory.paramClazz = new Class<?>[] { String.class };
				tracerAdapterFactory.params = new Object[] { uri.toString() };

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
