package com.feng.sauron.client.plugin.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import com.feng.sauron.client.context.SauronSessionContext;
import com.feng.sauron.client.plugin.TracerAdapterFactory;
import com.feng.sauron.tracer.Tracer;
import com.feng.sauron.utils.Constants;
import com.feng.sauron.utils.IdUtils;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年9月10日 上午11:08:25
 * 
 */

@WebFilter(urlPatterns = { "/*" })
public class WebTransformer implements Filter, WebTracerName {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		boolean isSuccess = false;
		try {
			HttpServletRequest httpReq = (HttpServletRequest) request;

			String traceid = httpReq.getHeader(Constants.SAURON_REQUEST_TRACEID);
			String spanid = httpReq.getHeader(Constants.SAURON_REQUEST_SPANID);
			String sourceAppName = httpReq.getHeader(Constants.SAURON_REQUEST_SOURCE_APPNAME);

			if (traceid == null | "".equals(traceid)) {
				traceid = IdUtils.getInstance().nextId();
			}

			if (spanid == null | "".equals(spanid)) {
				spanid = "0";
			} else {
				spanid += ".1";
			}

			if (sourceAppName == null || "".equals(sourceAppName)) {
				sourceAppName = TRACERNAME_STRING;
			}

			SauronSessionContext.initSessionContext(traceid);

			String className = "";

			String methodName = TRACERNAME_STRING;

			Class<?>[] parameterTypes = new Class[] { HttpServletRequest.class };

			Object[] paramVal = new Object[] { httpReq };

			Tracer tracer = TracerAdapterFactory.get(TRACERNAME_STRING, spanid, className, methodName, sourceAppName, parameterTypes, paramVal);

			SauronSessionContext.addTracerAdapter(tracer);

			SauronSessionContext.getCurrentTracerAdapter().beforeMethodExecute();
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();

		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			chain.doFilter(request, response);
			try {
				if (isSuccess) {
					SauronSessionContext.getCurrentTracerAdapter().afterMethodExecute();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			try {
				if (isSuccess) {
					SauronSessionContext.getCurrentTracerAdapter().catchMethodException(e);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			throw e;
		} finally {
			try {
				if (isSuccess) {
					SauronSessionContext.getCurrentTracerAdapter().catchMethodExceptionFinally();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void destroy() {
	}
}
