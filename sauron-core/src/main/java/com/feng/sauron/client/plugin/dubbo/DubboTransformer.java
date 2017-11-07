package com.feng.sauron.client.plugin.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.feng.sauron.client.context.SauronSessionContext;
import com.feng.sauron.client.plugin.TracerAdapterFactory;
import com.feng.sauron.config.SauronConfig;
import com.feng.sauron.tracer.Tracer;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年3月8日 下午6:42:26
 * 
 */

@Activate(group = { Constants.PROVIDER, Constants.CONSUMER })
public class DubboTransformer implements Filter, DubboTracerName {

	private static final String tracerStackString = "tracerStackString";

	public static final String dubbo_consumer_prifix = "dubbo_consumer";
	public static final String dubbo_provider_prifix = "dubbo_provider";

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

		if (Constants.CONSUMER_SIDE.equals(invoker.getUrl().getParameter(Constants.SIDE_KEY))) {
			return invokeConsumer(invoker, invocation);
		} else {
			return invokeProvider(invoker, invocation);
		}
	}

	private Result invokeConsumer(Invoker<?> invoker, Invocation invocation) {
		try {

			String className = invoker.getInterface().getName();
			String methodName = dubbo_consumer_prifix + "#" + className + "." + invocation.getMethodName();

			Class<?>[] parameterTypes = new Class[] { Invoker.class, Invocation.class };

			Object[] paramVal = new Object[] { invoker, invocation };

			if (SauronSessionContext.isTraceEntry()) {
				SauronSessionContext.initSessionContext();
			}

			SauronSessionContext.allocCurrentTracerAdapter(TRACERNAME_STRING, className, methodName, SauronConfig.getAPP_NAME(), parameterTypes, paramVal);
			SauronSessionContext.getCurrentTracerAdapter().beforeMethodExecute();

			String traceId = SauronSessionContext.getTraceId();

			String spanId = ((TracerAdapterFactory) SauronSessionContext.getCurrentTracerAdapter()).getSpanId();
			String nextSpanCount = ((TracerAdapterFactory) SauronSessionContext.getCurrentTracerAdapter()).getNextSpanCount();

			RpcContext.getContext().setAttachment(tracerStackString, traceId + "_" + spanId + "." + nextSpanCount + "_" + SauronConfig.getAPP_NAME());

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {

			Result result = invoker.invoke(invocation); // 让调用链往下执行
			try {
				SauronSessionContext.getCurrentTracerAdapter().afterMethodExecute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		} catch (Exception e) {
			try {
				SauronSessionContext.getCurrentTracerAdapter().catchMethodException(e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			throw e;
		} finally {
			try {
				SauronSessionContext.getCurrentTracerAdapter().catchMethodExceptionFinally();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Result invokeProvider(Invoker<?> invoker, Invocation invocation) {

		try {
			String beta = invocation.getAttachment(tracerStackString);

			if (beta == null || beta.length() == 0) {// 未获取到 调用栈, 不进行跟踪，或者 走默认跟踪（暂不提供）
				return invoker.invoke(invocation); // 让调用链往下执行
			}

			String[] split = beta.split("_");

			String tracerid = split[0];
			String spanid = split[1];
			
			String sourceAppName = dubbo_provider_prifix;
			
			if (split.length >= 3) {

				sourceAppName = split[2];
			}
			

			SauronSessionContext.initSessionContext(tracerid);

			String className = invoker.getInterface().getName();

			String methodName = dubbo_provider_prifix + "#" + className + "." + invocation.getMethodName();

			Class<?>[] parameterTypes = new Class[] { Invoker.class, Invocation.class };

			Object[] paramVal = new Object[] { invoker, invocation };

			Tracer adapter = TracerAdapterFactory.get(TRACERNAME_STRING, spanid, className, methodName, sourceAppName, parameterTypes, paramVal);

			SauronSessionContext.addTracerAdapter(adapter);
			SauronSessionContext.getCurrentTracerAdapter().beforeMethodExecute();

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			Result invoke = invoker.invoke(invocation); // 让调用链往下执行
			try {
				SauronSessionContext.getCurrentTracerAdapter().afterMethodExecute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return invoke;

		} catch (Exception e) {
			try {
				SauronSessionContext.getCurrentTracerAdapter().catchMethodException(e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			throw e;
		} finally {
			try {
				SauronSessionContext.getCurrentTracerAdapter().catchMethodExceptionFinally();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
