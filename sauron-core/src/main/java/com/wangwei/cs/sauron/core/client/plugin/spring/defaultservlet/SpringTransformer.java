package com.wangwei.cs.sauron.core.client.plugin.spring.defaultservlet;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTransformer;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年10月26日 下午5:18:44
 * 
 */
public class SpringTransformer extends AbstractTransformer implements SpringTracerName {

	private Logger logger = LoggerFactory.getLogger(SpringTransformer.class);

	private Map<String, HashSet<String>> traceClzMap = new HashMap<String, HashSet<String>>();

	public SpringTransformer() {
		initTtraceClzMap();
		addPackageImport();
	}

	private void addPackageImport() {
		try {
			CLASS_POOL.importPackage("org.springframework.web.servlet.FrameworkServlet");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initTtraceClzMap() {

		traceClzMap.put("org.springframework.web.servlet.FrameworkServlet", new HashSet<String>() {
			private static final long serialVersionUID = 1L;
			{
				// add("doPost");
				// add("doPut");
				// add("doDelete");
				// add("doOptions");
				// add("doTrace");
				add("processRequest(");// 包含上面
			}
		});

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

				HashSet<String> methodNameSet = traceClzMap.get(fixedClassName);

				CtMethod[] methods = classToBeModified.getDeclaredMethods();

				for (CtMethod ctMethod : methods) {

					String methodName = ctMethod.getLongName();

					boolean flag = false;
					if (methodNameSet == null) {
						flag = true;
					} else {
						for (String name : methodNameSet) {
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
