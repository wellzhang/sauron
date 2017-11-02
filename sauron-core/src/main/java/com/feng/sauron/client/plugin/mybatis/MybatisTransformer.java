package com.feng.sauron.client.plugin.mybatis;

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

import com.feng.sauron.client.plugin.AbstractTransformer;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2016年10月26日 下午5:18:44
 * 
 */
public class MybatisTransformer extends AbstractTransformer implements MybatisTracerName {

	private Logger logger = LoggerFactory.getLogger(MybatisTransformer.class);

	private Map<String, HashSet<String>> traceClzMap = new HashMap<String, HashSet<String>>();

	public MybatisTransformer() {
		initTtraceClzMap();
		addPackageImport();
	}

	private void addPackageImport() {
	}

	private void initTtraceClzMap() {

		// traceClzMap.put("org.mybatis.spring.SqlSessionTemplate", new HashSet<String>() {//使用spring 适配
		// private static final long serialVersionUID = 1L;
		// {
		// // add("selectList(java.lang.String,java.lang.Object)");
		// add("select");
		// add("insert");
		// add("update");
		// add("delete");
		// }
		// });

		traceClzMap.put("org.apache.ibatis.session.defaults.DefaultSqlSession", new HashSet<String>() {// 使用原生mybatis
					private static final long serialVersionUID = 1L;
					{
						// add("selectOne(java.lang.String,java.lang.Object)");//和selectList 重复 ，关闭
						add("selectList(java.lang.String,java.lang.Object,org.apache.ibatis.session.RowBounds)");
						add("selectMap(java.lang.String,java.lang.Object,java.lang.String,org.apache.ibatis.session.RowBounds)");
						add("select(java.lang.String,java.lang.Object,org.apache.ibatis.session.RowBounds,org.apache.ibatis.session.ResultHandler)");
						add("insert");
						add("update");
						add("delete");
					}
				});
	}

	public boolean check(String fixedClassName, CtClass clazz) {
		return traceClzMap.containsKey(fixedClassName);
	}

	public byte[] transform(ClassLoader classLoader, String className, Class<?> clazz, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		CtClass classToBeModified = null;
		try {
			String fixedClassName = className.replace("/", ".");

			classToBeModified = classPool.get(fixedClassName);

			if (checkAndCatchException(fixedClassName, classToBeModified)) {

				HashSet<String> methodNameSet = traceClzMap.get(fixedClassName);

				CtMethod[] methods = classToBeModified.getDeclaredMethods();

				for (CtMethod ctMethod : methods) {

					String methodName = ctMethod.getLongName();

					boolean flag = false;
					for (String name : methodNameSet) {
						if (methodName.contains(name)) {
							flag = true;
							break;
						}
					}

					if (methodNameSet == null || flag) {
						// 运行前处理
						ctMethod.insertBefore(sauron_code_before_method_execute(TRACERNAME_STRING, fixedClassName, methodName, sourceAppName, true));
						// 正常成功后处理
						ctMethod.insertAfter(sauron_code_after_method_execute(fixedClassName, methodName), false);
						// 异常捕捉处理
						ctMethod.addCatch(sauron_code_catch_method_execute(fixedClassName, methodName), classPool.getCtClass("java.lang.Exception"));
						// catch后的finally段处理
						ctMethod.insertAfter(sauron_code_after_method_execute_finally(fixedClassName, methodName), true);
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
