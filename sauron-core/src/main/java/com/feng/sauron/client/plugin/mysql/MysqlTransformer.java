package com.feng.sauron.client.plugin.mysql;

import com.feng.sauron.client.plugin.AbstractTransformer;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by lianbin.wang on 11/4/16.
 */
public class MysqlTransformer extends AbstractTransformer implements MysqlTracerName {

    private final Map<String, HashSet<String>> traceClzMap = new HashMap<>();

    public MysqlTransformer() {
        initTraceClzMap();
    }

    private void initTraceClzMap() {
        traceClzMap.put("com.mysql.jdbc.NonRegisteringDriver", new HashSet<>(Arrays.asList("connect"))); //public java.sql.Connection connect(String url, Properties info) throws SQLException
    }

    @Override
    public boolean check(String fixedClassName, CtClass clazz) {
        return traceClzMap.containsKey(fixedClassName);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        CtClass classToBeModified = null;
        try {
            String fixedClassName = className.replace("/", ".");
            classToBeModified = classPool.get(fixedClassName);

            if (checkAndCatchException(fixedClassName, classToBeModified)) {
                CtMethod[] methods = classToBeModified.getDeclaredMethods();

                for (CtMethod ctMethod : methods) {
                    String methodName = ctMethod.getLongName();
                    if (shouldWeaveMethod(fixedClassName, methodName)) {
                        ctMethod.insertBefore(sauron_code_before_method_execute(TRACERNAME_STRING, fixedClassName, methodName, sourceAppName, true));
                        ctMethod.insertAfter(sauron_code_after_method_execute(fixedClassName, methodName), false);
                        ctMethod.addCatch(sauron_code_catch_method_execute(fixedClassName, methodName), classPool.getCtClass("java.lang.Exception"));
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

    private boolean shouldWeaveMethod(String fixedClassName, String methodName) {
        HashSet<String> methodNameSet = traceClzMap.get(fixedClassName);
        if (methodNameSet == null || methodNameSet.isEmpty()) {
            return true;
        }
        for (String name : methodNameSet) {
            if (methodName.contains(name)) {
                return true;
            }
        }
        return false;
    }

}
