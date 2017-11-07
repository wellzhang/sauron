package com.feng.sauron.client.plugin.httpclient.jdk;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

import javassist.CtClass;
import javassist.CtMethod;
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
public class JdkHttpClientTransformer extends AbstractTransformer implements JdkHttpClientTracerName {

	private Logger logger = LoggerFactory.getLogger(JdkHttpClientTransformer.class);

	private static Map<String, HashMap<String, PrintTraceLog>> traceClzMap = null;

	public JdkHttpClientTransformer() {
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

			traceClzMap.put("java.net.URLConnection", new HashMap<String, PrintTraceLog>() {
				private static final long serialVersionUID = 1L;
				{
					put("getInputStream()", null);
				}
			});
			// traceClzMap.put("sun.net.www.protocol.http.HttpURLConnection", new HashMap<String, PrintTraceLog>() {
			// private static final long serialVersionUID = 1L;
			// {
			// put("sun.net.www.protocol.http.HttpURLConnection(java.net.URL,java.net.Proxy,sun.net.www.protocol.http.Handler)", null);
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

			if (check(fixedClassName, classToBeModified)) {

				CtMethod[] declaredMethods = classToBeModified.getDeclaredMethods();

				HashMap<String, PrintTraceLog> methodNameSet = traceClzMap.get(fixedClassName);

				for (CtMethod ctMethod : declaredMethods) {

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
						setPlugins(ctMethod);
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

	public void setPlugins(CtMethod ctMethod) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("{\nSystem.err.println(\"-----------begin add sauron jdkhttptracer\");\n}");
			sb.append("\n WriteUrl.write(this.url);\n");

			ctMethod.insertAfter(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
