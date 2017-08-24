package com.feng.sauron.client.plugin.httpclient.jdk;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feng.sauron.client.plugin.AbstractTransformer;
import com.feng.sauron.client.plugin.PrintTraceLog;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2016年10月26日 下午5:18:44
 * 
 */
public class CopyOfJdkHttpClientTransformer extends AbstractTransformer implements JdkHttpClientTracerName {

	private Logger logger = LoggerFactory.getLogger(CopyOfJdkHttpClientTransformer.class);

	private static Map<String, HashMap<String, PrintTraceLog>> traceClzMap = null;

	public CopyOfJdkHttpClientTransformer() {
		initTtraceClzMap();
		addPackageImport();
	}

	private void addPackageImport() {
		// try {
		// classPool.importPackage(SauronSessionContext.class.getName());
		// classPool.importPackage("sun.net.www.protocol.http.HttpURLConnection");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	private static void initTtraceClzMap() {

		if (traceClzMap == null) {
			traceClzMap = new HashMap<>();

			// traceClzMap.put("java.net.URLConnection", new HashMap<String, PrintTraceLog>() {
			// private static final long serialVersionUID = 1L;
			// {
			// put("getInputStream()", null);
			// }
			// });
			traceClzMap.put("sun.net.www.protocol.http.HttpURLConnection", new HashMap<String, PrintTraceLog>() {
				private static final long serialVersionUID = 1L;
				{
					put("sun.net.www.protocol.http.HttpURLConnection(java.net.URL,java.net.Proxy,sun.net.www.protocol.http.Handler)", null);
				}
			});
		}
	}

	public static Map<String, HashMap<String, PrintTraceLog>> getTraceClzMap() {
		initTtraceClzMap();
		return traceClzMap;
	}

	public boolean check(String fixedClassName, CtClass clazz) {
		return traceClzMap.containsKey(fixedClassName);
	}

	public byte[] transform(ClassLoader classLoader, String className, Class<?> clazz, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		CtClass classToBeModified = null;
		try {
			String fixedClassName = className.replace("/", ".");

			classToBeModified = classPool.get(fixedClassName);

			if (check(fixedClassName, classToBeModified)) {

				CtConstructor[] constructors = classToBeModified.getConstructors();

				HashMap<String, PrintTraceLog> methodNameSet = traceClzMap.get(fixedClassName);

				for (CtConstructor ctConstructor : constructors) {

					String methodName = ctConstructor.getLongName();

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
						ctConstructor.insertBefore(sauron_code_before_method_execute(TRACERNAME_STRING, fixedClassName, methodName, sourceAppName, true));
						// 正常成功后处理
						ctConstructor.insertAfter(sauron_code_after_method_execute(fixedClassName, methodName), false);
						// 异常捕捉处理
						ctConstructor.addCatch(sauron_code_catch_method_execute(fixedClassName, methodName), classPool.getCtClass("java.lang.Exception"));
						// catch后的finally段处理
						ctConstructor.insertAfter(sauron_code_after_method_execute_finally(fixedClassName, methodName), true);
					}
				}
				return classToBeModified.toBytecode();
			}
		} catch (NotFoundException e) {
			// e.printStackTrace();
			// 去掉类找不到时的报错，避免对输出过多错误。
			// 找不到的一般都是lib或虚拟机自身不存在的类，比如自动代理出的类 不用处理
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
