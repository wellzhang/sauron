package com.wangwei.cs.sauron.core.client.plugin.httpclient.httpclient4.printlog;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTracerAdapterFactory;
import com.wangwei.cs.sauron.core.client.plugin.PrintTraceLog;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年10月31日 上午11:45:46
 * 
 */
public class HttpRequestExecutorPrintLog implements PrintTraceLog {

	private HttpRequestExecutorPrintLog() {
	}

	private static class InnerClass {
		private static final HttpRequestExecutorPrintLog Inner_Class = new HttpRequestExecutorPrintLog();
	}

	public static HttpRequestExecutorPrintLog getInstances() {
		return InnerClass.Inner_Class;
	}

	@Override
	public String print(AbstractTracerAdapterFactory tracerAdapterFactory) {
		return null;
	}

}
