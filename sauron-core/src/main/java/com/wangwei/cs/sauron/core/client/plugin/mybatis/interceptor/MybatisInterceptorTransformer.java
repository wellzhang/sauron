package com.wangwei.cs.sauron.core.client.plugin.mybatis.interceptor;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.wangwei.cs.sauron.core.client.plugin.AbstractTransformer;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.apache.ibatis.plugin.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年10月26日 下午5:18:44
 * 
 */
public class MybatisInterceptorTransformer extends AbstractTransformer implements MybatisInterceptorTracerName {

	private Logger logger = LoggerFactory.getLogger(MybatisInterceptorTransformer.class);

	private Map<String, HashSet<String>> traceClzMap = new HashMap<String, HashSet<String>>();

	public MybatisInterceptorTransformer() {
		initTtraceClzMap();
		addPackageImport();
	}

	private void addPackageImport() {
		try {
			CLASS_POOL.importPackage(Interceptor.class.getName());
			CLASS_POOL.importPackage(AddInterceptor.class.getName());
			CLASS_POOL.importPackage(MybatisInterceptor.class.getName());
			CLASS_POOL.importPackage("org.mybatis.spring.SqlSessionFactoryBean");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initTtraceClzMap() {

		traceClzMap.put("org.mybatis.spring.SqlSessionFactoryBean", new HashSet<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("setPlugins");
				add("buildSqlSessionFactory");
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

				CtMethod[] methods = classToBeModified.getDeclaredMethods();

				HashSet<String> methodSet = traceClzMap.get(fixedClassName);

				for (CtMethod ctMethod : methods) {
					String methodName = ctMethod.getName();

					if (methodSet.contains(methodName)) {
						if ("setPlugins".equals(methodName)) {
							setPlugins(ctMethod);
						}
						if ("buildSqlSessionFactory".equals(methodName)) {
							buildSqlSessionFactory(ctMethod);
						}
					}
				}
				return classToBeModified.toBytecode();
			}
		} catch (NotFoundException e) {
		} catch (Exception e) {
			logger.error("transform  MybatisInterceptorTransformer Exception ", e);
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
			sb.append("\n this.plugins = AddInterceptor.setNewPlugins($1);\n");

			ctMethod.insertAfter(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void buildSqlSessionFactory(CtMethod ctMethod) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("{\nSystem.err.println(\"-----------begin add sauron mybatis interceptor : MybatisInterceptor\");\n}");
			sb.append("{\nif (plugins == null) {plugins = new Interceptor[1];plugins[0] = new MybatisInterceptor();}\n}");

			ctMethod.insertBefore(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
