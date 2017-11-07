package com.wangwei.cs.sauron.core.client.plugin;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年10月31日 上午11:39:17
 * 
 */
public interface PrintTraceLog {

	/**
	 *
	 * @param tracerAdapterFactory
	 * @return
	 */
	public String print(AbstractTracerAdapterFactory tracerAdapterFactory);

}
