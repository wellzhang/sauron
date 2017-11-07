package com.wangwei.cs.sauron.core.client.plugin.httpclient.httpclient4;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTransformer;
import com.wangwei.cs.sauron.core.client.plugin.httpclient.httpclient4.printlog.CloseableHttpClientPrintLog;
import com.wangwei.cs.sauron.core.client.plugin.httpclient.httpclient4.printlog.CloseableHttpClientPrintLog2;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wangwei.cs.sauron.core.client.plugin.PrintTraceLog;
import com.wangwei.cs.sauron.core.client.plugin.httpclient.httpclient4.printlog.CloseableHttpClientPrintLog1;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年10月26日 下午5:18:44
 * 
 */
public class HttpClient4Transformer extends AbstractTransformer implements HttpClient4TracerName {

	private Logger logger = LoggerFactory.getLogger(HttpClient4Transformer.class);

	private static Map<String, HashMap<String, PrintTraceLog>> traceClzMap = null;

	public HttpClient4Transformer() {
		initTtraceClzMap();
		addPackageImport();
	}

	private void addPackageImport() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initTtraceClzMap() {

		if (traceClzMap == null) {
			traceClzMap = new HashMap<>();
			// traceClzMap.put("org.apache.http.protocol.HttpRequestExecutor", new HashMap<String, PrintTraceLog>() {
			// private static final long serialVersionUID = 1L;
			// {
			// put("execute", HttpRequestExecutorPrintLog.getInstances());
			// // put("doSendRequest", HttpRequestExecutorPrintLog.getInstances());//与上面重复
			// // put("doReceiveResponse", HttpRequestExecutorPrintLog.getInstances());//与上面重复
			// }
			// });

			traceClzMap.put("org.apache.http.impl.client.AbstractHttpClient", new HashMap<String, PrintTraceLog>() {
				private static final long serialVersionUID = 1L;
				{
					put("execute(org.apache.http.HttpRequest,org.apache.http.protocol.HttpContext)", CloseableHttpClientPrintLog.getInstances());
					put("execute(org.apache.http.HttpHost,org.apache.http.HttpRequest,org.apache.http.protocol.HttpContext)", CloseableHttpClientPrintLog1.getInstances());
					put("execute(org.apache.http.HttpHost,org.apache.http.HttpRequest)", CloseableHttpClientPrintLog2.getInstances());
				}
			});

			traceClzMap.put("org.apache.http.impl.client.CloseableHttpClient", new HashMap<String, PrintTraceLog>() {
				private static final long serialVersionUID = 1L;
				{
					put("execute(org.apache.http.client.methods.HttpUriRequest,org.apache.http.protocol.HttpContext)", CloseableHttpClientPrintLog.getInstances());
					put("execute(org.apache.http.HttpHost,org.apache.http.HttpRequest,org.apache.http.protocol.HttpContext)", CloseableHttpClientPrintLog1.getInstances());
					put("execute(org.apache.http.HttpHost,org.apache.http.HttpRequest)", CloseableHttpClientPrintLog2.getInstances());
				}
			});

			// traceClzMap.put("org.apache.http.impl.client.DefaultHttpRequestRetryHandler", new HashMap<String, PrintTraceLog>() {
			// private static final long serialVersionUID = 1L;
			// {
			// put("retryRequest(org.apache.http.HttpRequest,org.apache.http.protocol.HttpContext)", WebPrintLog.getInstances());
			// }
			// });
		}

	}

	public static Map<String, HashMap<String, PrintTraceLog>> getTraceClzMap() {
		initTtraceClzMap();
		return traceClzMap;
	}
	@Override
	public boolean check(String fixedClassName, CtClass clazz) {
		return traceClzMap.containsKey(fixedClassName);
	}
	@Override
	public byte[] transform(ClassLoader classLoader, String className, Class<?> clazz, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		CtClass classToBeModified = null;
		try {
			String fixedClassName = className.replace("/", ".");

			classToBeModified = CLASS_POOL.get(fixedClassName);

			if (checkAndCatchException(fixedClassName, classToBeModified)) {

				HashMap<String, PrintTraceLog> methodNameSet = traceClzMap.get(fixedClassName);

				CtMethod[] methods = classToBeModified.getDeclaredMethods();

				for (CtMethod ctMethod : methods) {

					String methodName = ctMethod.getLongName();

					boolean flag = false;

					if (methodNameSet == null) {
						flag = true;
					} else {
						for (String name : methodNameSet.keySet()) {
							if (methodName.contains(name)) {
								flag = true;
								break;
							}
						}
					}

					if (flag) {
						// 运行前处理
						ctMethod.insertBefore(sauronCodeBeforeMethodExecute(TRACERNAME_STRING, fixedClassName, methodName, sourceAppName, true));
						// 正常成功后处理
						ctMethod.insertAfter(sauronCodeAfterMethodExecute(fixedClassName, methodName), false);
						// 异常捕捉处理
						ctMethod.addCatch(sauronCodeCatchMethodExecute(fixedClassName, methodName), CLASS_POOL.getCtClass("java.lang.Exception"));
						// catch后的finally段处理
						ctMethod.insertAfter(sauronCodeAfterMethodExecuteFinally(fixedClassName, methodName), true);
					}
				}
				return classToBeModified.toBytecode();
			}
		} catch (NotFoundException e) {
		} catch (Exception e) {
			logger.error("transform Exception ", e);
		} finally {
			if (classToBeModified != null) {
				try {
					classToBeModified.detach();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

}
