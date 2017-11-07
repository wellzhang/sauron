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
public class RocketMqProducerTransformer extends AbstractTransformer implements RocketMQProducerTracerName {
    private static final Logger logger = LoggerFactory.getLogger(RocketMqProducerTransformer.class);

    private Map<String, HashSet<String>> traceClzMap = new HashMap<>();


    public RocketMqProducerTransformer() {
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
            classToBeModified = CLASS_POOL.get(fixedClassName);

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
                        ctMethod.insertBefore(sauronCodeBeforeMethodExecute(TRACERNAME_STRING, fixedClassName, methodName, sourceAppName, true));
                        // 正常成功后处理
                        ctMethod.insertAfter(sauronCodeAfterMethodExecute(fixedClassName, methodName), false);
                        // 异常捕捉处理
                        ctMethod.addCatch(sauronCodeCatchMethodExecute(fixedClassName, methodName), CLASS_POOL.getCtClass("java.lang.Exception"));
                        // catch后的finally段处理
                        ctMethod.insertAfter(sauronCodeAfterMethodExecuteFinallyWithReturn(fixedClassName, methodName), true);
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
