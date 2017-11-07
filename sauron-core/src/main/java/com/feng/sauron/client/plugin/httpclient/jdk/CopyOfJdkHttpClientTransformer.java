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
 * @version 创建时间：2015年10月26日 下午5:18:44
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
		// CLASS_POOL.importPackage(SauronSessionContext.class.getName());
		// CLASS_POOL.importPackage("sun.net.www.protocol.http.HttpURLConnection");
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
						ctConstructor.insertBefore(sauronCodeBeforeMethodExecute(TRACERNAME_STRING, fixedClassName, methodName, sourceAppName, true));
						// 正常成功后处理
						ctConstructor.insertAfter(sauronCodeAfterMethodExecute(fixedClassName, methodName), false);
						// 异常捕捉处理
						ctConstructor.addCatch(sauronCodeCatchMethodExecute(fixedClassName, methodName), CLASS_POOL.getCtClass("java.lang.Exception"));
						// catch后的finally段处理
						ctConstructor.insertAfter(sauronCodeAfterMethodExecuteFinally(fixedClassName, methodName), true);
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
