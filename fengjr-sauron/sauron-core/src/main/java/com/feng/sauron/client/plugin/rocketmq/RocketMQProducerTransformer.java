package com.feng.sauron.client.plugin.rocketmq;

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
 * Created by lianbin.wang on 11/2/16.
 */
public class RocketMQProducerTransformer extends AbstractTransformer implements RocketMQProducerTracerName {
    private static final Logger logger = LoggerFactory.getLogger(RocketMQProducerTransformer.class);

    private Map<String, HashSet<String>> traceClzMap = new HashMap<>();


    public RocketMQProducerTransformer() {
        initTraceClzMap();
    }

    private void initTraceClzMap() {
        traceClzMap.put("com.alibaba.rocketmq.client.impl.producer.DefaultMQProducerImpl", new HashSet<String>() {
            private static final long serialVersionUID = 1L;

            {
                add("sendKernelImpl");
            }
        });
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
                        ctMethod.insertBefore(sauron_code_before_method_execute(TRACERNAME_STRING, fixedClassName, methodName, sourceAppName, true));
                        // 正常成功后处理
                        ctMethod.insertAfter(sauron_code_after_method_execute(fixedClassName, methodName), false);
                        // 异常捕捉处理
                        ctMethod.addCatch(sauron_code_catch_method_execute(fixedClassName, methodName), classPool.getCtClass("java.lang.Exception"));
                        // catch后的finally段处理
                        ctMethod.insertAfter(sauron_code_after_method_execute_finally_with_return(fixedClassName, methodName), true);
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
