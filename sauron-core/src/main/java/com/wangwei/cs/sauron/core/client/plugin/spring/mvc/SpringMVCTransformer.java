package com.wangwei.cs.sauron.core.client.plugin.spring.mvc;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTransformer;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年10月26日 下午5:18:44
 */
public class SpringMVCTransformer extends AbstractTransformer implements SpringMVCTracerName {

	private Logger logger = LoggerFactory.getLogger(SpringMVCTransformer.class);

	public SpringMVCTransformer() {
		initTtraceClzMap();
		addPackageImport();
	}

	private void addPackageImport() {
		try {
			// CLASS_POOL.importPackage("org.springframework.web.bind.annotation.RestController");
			// CLASS_POOL.importPackage("org.springframework.stereotype.Controller");
			// CLASS_POOL.importPackage("org.springframework.web.bind.annotation.RequestMapping");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initTtraceClzMap() {

	}
	@Override
	public boolean check(String fixedClassName, CtClass clazz) throws Exception, Throwable {

		if (fixedClassName.startsWith("com.feng")) {

			Object[] annotations = clazz.getAnnotations();

			for (int i = 0; i < annotations.length; i++) {

				if ("@org.springframework.web.bind.annotation.RestController".equals(annotations[i].toString())) {
					// try {
					// Class<?> forName = Class.forName("org.springframework.web.bind.annotation.RestController");
					// boolean hasAnnotation = clazz.hasAnnotation(forName);
					// } catch (Exception e) {
					// }
					return true;
				} else if ("@org.springframework.stereotype.Controller".equals(annotations[i].toString())) {
					return true;
				}
			}
		}

		return false;
	}
	@Override
	public byte[] transform(ClassLoader classLoader, String className, Class<?> clazz, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		CtClass classToBeModified = null;
		try {
			String fixedClassName = className.replace("/", ".");

			classToBeModified = CLASS_POOL.get(fixedClassName);

			if (checkAndCatchException(fixedClassName, classToBeModified)) {

				CtMethod[] methods = classToBeModified.getDeclaredMethods();

				for (CtMethod ctMethod : methods) {

					Object[] annotations = ctMethod.getAnnotations();

					for (int i = 0; i < ctMethod.getAnnotations().length; i++) {

						if (annotations[i].toString().startsWith("@org.springframework.web.bind.annotation.RequestMapping(")) {

							// 运行前处理
							ctMethod.insertBefore(sauronCodeBeforeMethodExecute(TRACERNAME_STRING, fixedClassName, ctMethod.getLongName(), sourceAppName, true));
							// 正常成功后处理
							ctMethod.insertAfter(sauronCodeAfterMethodExecute(fixedClassName, ctMethod.getLongName()), false);
							// 异常捕捉处理
							ctMethod.addCatch(sauronCodeCatchMethodExecute(fixedClassName, ctMethod.getLongName()), CLASS_POOL.getCtClass("java.lang.Exception"));
							// catch后的finally段处理
							ctMethod.insertAfter(sauronCodeAfterMethodExecuteFinally(fixedClassName, ctMethod.getLongName()), true);

							break;
						}
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