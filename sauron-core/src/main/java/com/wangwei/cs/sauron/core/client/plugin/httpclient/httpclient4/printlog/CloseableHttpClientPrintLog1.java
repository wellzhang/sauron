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
public class CloseableHttpClientPrintLog1 implements PrintTraceLog {

	private CloseableHttpClientPrintLog1() {
	}

	private static class InnerClass {
		private static final CloseableHttpClientPrintLog1 INSTANCE = new CloseableHttpClientPrintLog1();
	}

	public static CloseableHttpClientPrintLog1 getInstances() {
		return InnerClass.INSTANCE;
	}

	@Override
	public String print(AbstractTracerAdapterFactory tracerAdapterFactory) {

		try {
			if (tracerAdapterFactory.params != null && tracerAdapterFactory.params.length >= 2 && tracerAdapterFactory.params[1] instanceof HttpUriRequest) {
				HttpUriRequest httpUriRequest = (HttpUriRequest) tracerAdapterFactory.params[1];
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
