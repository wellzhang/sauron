package com.feng.sauron.client.plugin;

import java.lang.instrument.ClassFileTransformer;
import java.util.concurrent.atomic.AtomicBoolean;

import com.feng.sauron.config.SauronConfig;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年10月27日 下午2:39:08
 * 
 */
public abstract class AbstractTransformer implements ClassFileTransformer {

	public static final ClassPool classPool = ClassPool.getDefault();

	protected String sourceAppName = SauronConfig.getAPP_NAME();

	private static AtomicBoolean flag = new AtomicBoolean(true);

	static {
		if (flag.getAndSet(false)) {
			insertClassPath();
		}
	}

	private static void insertClassPath() {

		try {
			classPool.insertClassPath(Thread.currentThread().getContextClassLoader().getResource("").getPath());
			classPool.insertClassPath(new ClassClassPath(AbstractTransformer.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean checkAndCatchException(String fixedClassName, CtClass clazz) {

		boolean check = false;
		try {
			check = check(fixedClassName, clazz);
		} catch (Exception e) {
			if (!(e.getCause() instanceof NotFoundException)) {
				e.printStackTrace();
			}
		} catch (Throwable e) {
			if (!(e.getCause() instanceof NotFoundException)) {
				e.printStackTrace();
			}
		}
		return check;
	}

	public abstract boolean check(String fixedClassName, CtClass clazz) throws Exception, Throwable;

	// 运行前处理
	public String sauron_code_before_method_execute(String tracerName, String className, String methodName, String sourceAppName, boolean isTraceParam) {

		if (isTraceParam) {
			return "if(SauronSessionContext.isMethodShouldBeTrace(\"" + className + "\",\"" + methodName + "\"))"//
					+ "{" // ------
					+ "   SauronSessionContext.allocCurrentTracerAdapter(\"" + tracerName + "\",\"" + className + "\",\"" + methodName + "\",\"" + sourceAppName + "\",$sig ,$args);"//
					+ "   SauronSessionContext.getCurrentTracerAdapter().beforeMethodExecute();" //
					+ "}";// ------
		} else {
			return "if(SauronSessionContext.isMethodShouldBeTrace(\"" + className + "\",\"" + methodName + "\"))"//
					+ "{" // ------
					+ "   SauronSessionContext.allocCurrentTracerAdapter(\"" + tracerName + "\",\"" + className + "\",\"" + methodName + "\",\"" + sourceAppName + "\");" //
					+ "   SauronSessionContext.getCurrentTracerAdapter().beforeMethodExecute();" //
					+ "}";
		}
	}

	// 正常成功后处理
	public String sauron_code_after_method_execute(String className, String methodName) {
		return "if(SauronSessionContext.isMethodShouldBeTrace(\"" + className + "\",\"" + methodName + "\"))"//
				+ "{" //
				+ "   SauronSessionContext.getCurrentTracerAdapter().afterMethodExecute();"//
				+ "}";//
	}

	// 异常捕捉处理
	public String sauron_code_catch_method_execute(String className, String methodName) {
		return "if(SauronSessionContext.isMethodShouldBeTrace(\"" + className + "\",\"" + methodName + "\"))"//
				+ "{" //
				+ "   SauronSessionContext.getCurrentTracerAdapter().catchMethodException($e);" //
				+ "}" //
				+ "throw $e;";//
	}

	// catch后的finally段处理
	public String sauron_code_after_method_execute_finally(String className, String methodName) {
		return "if(SauronSessionContext.isMethodShouldBeTrace(\"" + className + "\",\"" + methodName + "\"))" //
				+ "{" //
				+ "   SauronSessionContext.getCurrentTracerAdapter().catchMethodExceptionFinally();" //
				+ "}";//
	}

	public String sauron_code_after_method_execute_finally_with_return(String className, String methodName) {
		return "if(SauronSessionContext.isMethodShouldBeTrace(\"" + className + "\",\"" + methodName + "\"))" //
				+ "{" //
				+ "   SauronSessionContext.getCurrentTracerAdapter().catchMethodExceptionFinally($type,$_);" //
				+ "}";//
	}

}
