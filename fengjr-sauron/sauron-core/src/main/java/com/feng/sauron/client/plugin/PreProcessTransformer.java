package com.feng.sauron.client.plugin;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Map;

import javassist.ByteArrayClassPath;
import javassist.CtClass;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年10月28日 下午2:12:41
 * 
 */
public class PreProcessTransformer implements ClassFileTransformer {

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

		ByteArrayClassPath byteArrayClassPath = null;

		try {

			if (className == null || className.contains("javassist")) {
				return null;
			}

			String fixedClassName = className.replace("/", ".");

			byteArrayClassPath = new ByteArrayClassPath(fixedClassName, classfileBuffer);

			AbstractTransformer.classPool.insertClassPath(byteArrayClassPath);

			CtClass classToBeModified = AbstractTransformer.classPool.get(fixedClassName);

			if (classToBeModified == null) {
				return null;
			}

			Map<String, AbstractTransformer> classFileTransFormerMap = TransformerFactory.getAllTransformer();

			byte[] transformed = null;

			for (String tracerName : classFileTransFormerMap.keySet()) {

				AbstractTransformer classFileTransformer = classFileTransFormerMap.get(tracerName);

				if (classFileTransformer.checkAndCatchException(fixedClassName, classToBeModified)) {

					transformed = classFileTransformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);

					if (transformed != null) {

						System.out.println("Transformer [" + classFileTransformer.getClass().getName() + "] transformed class [" + className + "]; in=" + classfileBuffer.length + "; out="
								+ transformed.length);

						break;// 有一个满足的 就行
					}
				}
			}
			return transformed;
		} catch (Exception e) {
			System.err.println("Error weaving class [" + className + "] ..........");
			e.printStackTrace();
		} finally {
			if (byteArrayClassPath != null) {
				try {
					AbstractTransformer.classPool.removeClassPath(byteArrayClassPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
