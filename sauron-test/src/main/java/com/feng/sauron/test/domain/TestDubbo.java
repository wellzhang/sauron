package com.feng.sauron.test.domain;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2016年4月12日 上午10:14:14
 */
public class TestDubbo {

    /**
     * @param args
     */
    public static void mai2n(String[] args) {

        String cmdString = "cmd  telnet 10.255.52.19 50078";

        try {
            Process exec = Runtime.getRuntime().exec(cmdString);
            InputStream inputStream = exec.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "gbk"));
            String line = "";
            while (null != (line = br.readLine())) {
                System.out.println(cmdString + "脚本执行信息:{}" + line);
            }
            exec.waitFor();

            Thread.sleep(10000);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("resource")
    public static void main(String[] args) {

        // ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"consumer.xml"});
        // 　　　　HelloService helloService = (HelloService)context.getBean("helloService"); //

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"consumer.xml"});


        context.start();

    }

    public static void main2(String[] args) {

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("com.feng.sauron.test.domain.ExampleClass.doSomething10(long,java.lang.String)");
        arrayList.add("com.feng.sauron.test.domain.ExampleClass.doSomething3(int)");

        StringBuilder sb = new StringBuilder();

        sb.append("/");

        for (String string : arrayList) {
            String replace = string.replace(".", "\\.").replace("(", "\\(").replace(")", "\\)");
            sb.append(replace).append("|");
        }

        sb.append("/");

        System.out.println(sb.toString());
    }
}
