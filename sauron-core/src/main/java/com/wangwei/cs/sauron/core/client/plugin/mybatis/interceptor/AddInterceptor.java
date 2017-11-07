package com.wangwei.cs.sauron.core.client.plugin.mybatis.interceptor;

import org.apache.ibatis.plugin.Interceptor;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年9月1日 下午2:09:51
 * 
 */
public class AddInterceptor {

	public static Object[] setNewPlugins(Object[] paramVal) {

		try {
			if (paramVal == null) {
				return paramVal;
			}

			for (Object object : paramVal) {
				if (object instanceof MybatisInterceptor) {
					return paramVal;
				}
			}

			Object[] newInterceptors = new Interceptor[paramVal.length + 1];
			for (int i = 0; i < paramVal.length; i++) {
				newInterceptors[i] = paramVal[i];
			}
			newInterceptors[newInterceptors.length - 1] = new MybatisInterceptor();

			paramVal = newInterceptors;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return paramVal;
	}
}
