package com.feng.sauron.client.plugin.redis;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

import javassist.CtClass;
import javassist.CtConstructor;
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
public class JedisTransformer extends AbstractTransformer implements JedisTracerName {

	private Logger logger = LoggerFactory.getLogger(JedisTransformer.class);

	private static Map<String, HashMap<String, PrintTraceLog>> traceClzMap = null;

	public JedisTransformer() {
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

			traceClzMap.put("redis.clients.jedis.BinaryClient", new HashMap<String, PrintTraceLog>() {
				private static final long serialVersionUID = 1L;
				{
					put("setPassword", null);// 不包含 这个方法的
					put("isInMulti", null);// 不包含 这个方法的
					put("cluster", null);// 不包含 这个方法的
					put("close", null);// 不包含 这个方法的
					put("getDB", null);// 不包含 这个方法的
					put("ping", null);// 不包含 这个方法的
					put("isInWatch", null);// 不包含 这个方法的
					put("resetState", null);// 不包含 这个方法的
					put("connect", null);// 不包含 这个方法的
					put("disconnect", null);// 不包含 这个方法的
					put("joinParameters", null);// 不包含 这个方法的
					put("quit", null);// 不包含 这个方法的
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

			if (checkAndCatchException(fixedClassName, classToBeModified)) {

				CtConstructor[] constructors = classToBeModified.getConstructors();

				for (CtConstructor ctConstructor : constructors) {

					String longName = ctConstructor.getLongName();

					// 运行前处理
					ctConstructor.insertBefore(sauronCodeBeforeMethodExecute(TRACERNAME_STRING, fixedClassName, longName, sourceAppName, true));
					// 正常成功后处理
					ctConstructor.insertAfter(sauronCodeAfterMethodExecute(fixedClassName, longName), false);
					// 异常捕捉处理
					ctConstructor.addCatch(sauronCodeCatchMethodExecute(fixedClassName, longName), CLASS_POOL.getCtClass("java.lang.Exception"));
					// catch后的finally段处理
					ctConstructor.insertAfter(sauronCodeAfterMethodExecuteFinally(fixedClassName, longName), true);

				}

				HashMap<String, PrintTraceLog> methodNameSet = traceClzMap.get(fixedClassName);

				CtMethod[] methods = classToBeModified.getDeclaredMethods();

				for (CtMethod ctMethod : methods) {

					String shortMethod = ctMethod.getName();

					boolean flag = false;
					if (!methodNameSet.containsKey(shortMethod)) {
						flag = true;
					}

					if (flag) {

						String methodName = ctMethod.getLongName();

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
