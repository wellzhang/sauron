package com.feng.sauron.client.plugin.httpclient.jdk;

import java.net.URL;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年11月9日 上午10:08:21
 * 
 */
public class WriteUrl {

	public static void write(URL url) {

		String host = url.getHost();

		System.out.println(host);
	}

}
