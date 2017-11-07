package com.feng.sauron.client.listener;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.Map;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import com.feng.sauron.client.plugin.AbstractTransformer;
import com.feng.sauron.client.plugin.TransformerFactory;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年11月15日 上午10:20:38
 */
public class CallbackRedefineClasse {

    private static Thread thread = null;

    private CallbackRedefineClasse() {
        try {
            printJvmInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class CallbackInnerClass {

        private static final ClassPool classPool = ClassPool.getDefault();
        private static final CallbackRedefineClasse JVM_MONITOR = new CallbackRedefineClasse();
    }

    public static CallbackRedefineClasse run() {
        return CallbackInnerClass.JVM_MONITOR;
    }

    private void printJvmInfo() {

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread = new Thread(runnable);
        thread.setName("CallbackRedefineClasse");
        thread.setDaemon(true);
        thread.start();

    }

    public void start() {

        try {
            Thread.sleep(10000 * 2);
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        SauronInstrumentation javaAgentWeaver = null;

        try {
            javaAgentWeaver = SauronInstrumentation.getInstance();
        } catch (Exception e1) {
            e1.printStackTrace();
        } catch (Throwable e1) {
            e1.printStackTrace();
        }

        if (javaAgentWeaver == null) {
            System.err.println("javaAgentInstrumentationWeaver  is null ...");
            return;
        }

        Instrumentation instrumentation = javaAgentWeaver.initInstrumentation();

        Class<?>[] allLoadedClasses = instrumentation.getAllLoadedClasses();

        for (Class<?> class1 : allLoadedClasses) {

            if (class1.getName().startsWith("com.feng")) {
                transform(instrumentation, class1);
            }
        }
    }

    public static void transform(Instrumentation instrumentation, Class<?> class1) {

        try {

            CallbackInnerClass.classPool.insertClassPath(new ClassClassPath(class1.getClass()));

            CtClass ctClass = CallbackInnerClass.classPool.get(class1.getName());

            if (class1.getName().contains("javassist")) {
                return;
            }

            Map<String, AbstractTransformer> classFileTransFormerMap = TransformerFactory.getAllTransformer();

            for (String tracerName : classFileTransFormerMap.keySet()) {

                AbstractTransformer classFileTransformer = classFileTransFormerMap.get(tracerName);

                if (classFileTransformer.checkAndCatchException(class1.getName(), ctClass)) {

                    byte[] bytecode = ctClass.toBytecode();// toBytecode 会造成类被冻结

                    // FileOutputStream fileWriter = new FileOutputStream("/export/log/" + class1.getSimpleName() + ".class");//测试用
                    //
                    // DataOutputStream dataOutputStream = new DataOutputStream(fileWriter);
                    //
                    // ctClass.toBytecode(dataOutputStream);

                    if (ctClass.isFrozen()) {// toBytecode 会造成类被冻结
                        ctClass.defrost();
                    }

                    ClassDefinition classDefinition = new ClassDefinition(class1, bytecode);

                    instrumentation.redefineClasses(classDefinition); // classFileTransformer（AnnotationTransformer） 已经注册到 instrumentation 里面了 ，所以此处不需要用到
                }
            }

        } catch (NotFoundException e) {
            // 不需要关心，一般都是代理类
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                CallbackInnerClass.classPool.removeClassPath(new ClassClassPath(class1.getClass()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
