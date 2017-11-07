package com.feng.sauron.client.plugin.local.annotation;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feng.sauron.client.annotations.TraceClass;
import com.feng.sauron.client.annotations.TraceMethod;
import com.feng.sauron.client.plugin.AbstractTransformer;

/**
 * 实现通过注解方式对加载前的类动态修改
 */
public class AnnotationTransformer extends AbstractTransformer implements AnnotationTracerName {

	private Logger logger = LoggerFactory.getLogger(AnnotationTransformer.class);
	@Override
	public boolean check(String fixedClassName, CtClass clazz) {

		Object annotation = null;
		try {
			annotation = clazz.getAnnotation(TraceClass.class);
		} catch (Exception e) {
		}
		return annotation != null;
	}
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> clazz, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		String fixedClassName = className.replace("/", ".");
		CtClass classToBeModified = null;
		try {
			classToBeModified = CLASS_POOL.get(fixedClassName);

			if (checkAndCatchException(fixedClassName, classToBeModified)) {

				CtMethod[] methods = classToBeModified.getDeclaredMethods();

				for (CtMethod ctMethod : methods) {

					// 处理Method级别的Trace注解
					if (ctMethod.getAnnotation(TraceMethod.class) != null) {

						TraceMethod annotation = (TraceMethod) ctMethod.getAnnotation(TraceMethod.class);
						// 运行前处理
						ctMethod.insertBefore(sauronCodeBeforeMethodExecute(TRACERNAME_STRING, fixedClassName, ctMethod.getLongName(), sourceAppName, annotation.isTraceParam()));
						// 正常成功后处理
						ctMethod.insertAfter(sauronCodeAfterMethodExecute(fixedClassName, ctMethod.getLongName()), false);
						// 异常捕捉处理
						ctMethod.addCatch(sauronCodeCatchMethodExecute(fixedClassName, ctMethod.getLongName()), CLASS_POOL.getCtClass("java.lang.Exception"));
						// catch后的finally段处理
						ctMethod.insertAfter(sauronCodeAfterMethodExecuteFinally(fixedClassName, ctMethod.getLongName()), true);

					}

				}
				// 返回修改后的class字节码
				return classToBeModified.toBytecode();
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
			// 去掉类找不到时的报错，避免对客户机输出过多错误。
			// 找不到的一般都是lib或虚拟机自身不存在的类，比如自动代理出的类。其实本来也不用处理
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
