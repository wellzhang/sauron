package com.feng.sauron.client.plugin.local.methodname;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feng.sauron.client.annotations.TraceClass;
import com.feng.sauron.client.annotations.TraceMethod;
import com.feng.sauron.client.context.SauronSessionContext;
import com.feng.sauron.client.plugin.AbstractTransformer;
import com.feng.sauron.config.SauronConfig;
import com.feng.sauron.config.WatchableConfigClient;
import com.feng.sauron.tracer.Tracer;
import com.feng.sauron.utils.Constants;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年10月26日 下午5:18:44
 */
public class MethodNameTransformer extends AbstractTransformer implements MethodNameTracerName {

    private Logger logger = LoggerFactory.getLogger(MethodNameTransformer.class);

    private Map<String, HashSet<String>> traceClzMap = new HashMap<String, HashSet<String>>();

    public MethodNameTransformer() {
        try {
            initTtraceClzMap();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e2) {
            e2.printStackTrace();
        }
        addPackageImport();
    }

    private void addPackageImport() {
        try {
            classPool.importPackage(Tracer.class.getName());
            classPool.importPackage(SauronSessionContext.class.getName());
            classPool.importPackage(TraceClass.class.getName());
            classPool.importPackage(TraceMethod.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTtraceClzMap() {

        String methodNameString = WatchableConfigClient.getInstance().get(SauronConfig.getAPP_NAME(), Constants.SAURON_METHODNAME, "");
        // methodNameString = "com.feng.sauron.test.domain.ExampleClass:doSomething1(),doSomething2(long)#com.feng.sauron.test.domain.ExampleClass2:doSomeWork()#";
        String[] methodArray = methodNameString.split(Constants.CLASS_SPLITER);

        // 表达式对象
        Pattern p = Pattern.compile(Constants.METHOD_RESOLVE_REGEXP);
        for (String method : methodArray) {
            // 创建 Matcher 对象
            Matcher m = p.matcher(method);
            // 是否找到匹配
            boolean found = m.find();
            if (found) {
                String clazzName = m.group(1);
                if (clazzName.isEmpty()) {
                    logger.debug("处理描述符时出错，跳过该条转换");
                    continue;
                }
                String methodName = m.group(3);
                if (methodName != null) {
                    String[] methods = methodName.split(Constants.METHOD_SPLITER);
                    HashSet<String> methodNameSet = new HashSet<String>();
                    for (String oneMethod : methods) {
                        methodNameSet.add(clazzName + "." + oneMethod);
                    }
                    traceClzMap.put(clazzName, methodNameSet);
                } else {
                    traceClzMap.put(clazzName, null);
                }
            }
        }
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

                HashSet<?> methodNameSet = traceClzMap.get(fixedClassName);

                CtMethod[] methods = classToBeModified.getDeclaredMethods();

                for (CtMethod ctMethod : methods) {

                    String methodName = ctMethod.getLongName();

                    if (methodNameSet == null || methodNameSet.contains(methodName)) {
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
