package com.feng.sauron.warning.filter;

import com.fengjr.upm.filter.util.UserUtils;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.MultiMap;

import javax.servlet.*;

public class JettyRequestFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException {

		try {

//			try {
//				Request baseRequest = (Request) req;
//				if (!baseRequest.getRequestURI().equals(baseRequest.getUri().toString()) && baseRequest.getParameterMap().isEmpty()) {
//					MultiMap _baseParameters = new MultiMap();
//					baseRequest.getUri().decodeQueryTo(_baseParameters);
//					baseRequest.setParameters(_baseParameters);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//
//			}

			chain.doFilter(req, res);
		} catch (Exception e) {
			if (!(e instanceof org.eclipse.jetty.io.EofException)) {
				e.printStackTrace();
			}
		}
		// logger.info("timeFilter,time:{},thread:{},url:{}",(System.currentTimeMillis()-before),Thread.currentThread().getId(),url);

	}

	@Override
	public void destroy() {

	}
}
