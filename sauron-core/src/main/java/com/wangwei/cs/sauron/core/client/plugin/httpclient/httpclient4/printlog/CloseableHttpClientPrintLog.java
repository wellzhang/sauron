package com.wangwei.cs.sauron.core.client.plugin.httpclient.httpclient4.printlog;

import java.net.URI;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTracerAdapterFactory;
import org.apache.http.client.methods.HttpUriRequest;

import com.wangwei.cs.sauron.core.client.plugin.PrintTraceLog;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年10月31日 下午1:53:42
 * 
 */
public class CloseableHttpClientPrintLog implements PrintTraceLog {

	private CloseableHttpClientPrintLog() {
	}

	private static class InnerClass {
		private static final CloseableHttpClientPrintLog INSTANCE = new CloseableHttpClientPrintLog();
	}

	public static CloseableHttpClientPrintLog getInstances() {
		return InnerClass.INSTANCE;
	}

	@Override
	public String print(AbstractTracerAdapterFactory tracerAdapterFactory) {

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
