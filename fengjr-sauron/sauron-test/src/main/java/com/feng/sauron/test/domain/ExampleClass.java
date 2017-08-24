package com.feng.sauron.test.domain;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.feng.ipcenter.service.IPDataFileService;
import com.feng.sauron.client.annotations.TraceClass;
import com.feng.sauron.client.annotations.TraceMethod;
import com.feng.sauron.tracerImpl.SauronTracer;

@TraceClass
public class ExampleClass {

	
	
//	private static Logger logger = LoggerFactory.getLogger(ExampleClass.class);
	
	@TraceMethod
	public String testDubbo(ClassPathXmlApplicationContext context) throws Exception {

		IPDataFileService bean = (IPDataFileService) context.getBean("ipDataFileService");

		String find = bean.find("127.0.0.1");
//		logger.info(""+System.nanoTime());
		doSomething1();

		return "This is a example";
	}

	@TraceMethod
	public String doSomething1() throws Exception {

		SauronTracer.start();

		InnerClass innerClass = new InnerClass();
		innerClass.printInner();
//		logger.info("abcdef");

		try {
			Thread.sleep(Math.round(Math.random() % 5));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		SauronTracer.end("pay_dosomething_123", "我是2234\r\\n.,dfdf\n]dffd\rfdfd", true);

		try {
			Thread.sleep(Math.round(Math.random() % 5));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ExampleClass2 exampleClass2 = new ExampleClass2();
		exampleClass2.doSomeWork();

		doSomething2(10);

		return "This is a example";
	}

	@TraceMethod(isTraceParam = false)
	private void doSomething2(long i) {

		int ii = new Random().nextInt(10);

		if (ii % 2 == 0) {
			SauronTracer.alarm("pay_xxxxxx_except_path", "pay_xxxxxx_except_path....不能走\r这里，\n但是走这里了\r\n..." + ii);
		}

		if (ii == 0) {
			SauronTracer.alarm("pay_xxxxxx_redis", "redis未命中" + ii);
		} else {
			SauronTracer.alarm("pay_xxxxxx_mysql", "穿透到mysql" + ii);
		}

//		logger.info("ghijk");
		doSomething3(101);
		doSomething4(i, "\r\nererererer\nrfevfd\r\\\\\\\reredfdfrererereretegtetg");
	}

	@TraceMethod(isTraceParam = false)
	private void doSomething3(int i) {
		System.out.println("qwerty");
		doSomething4(44, "");
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@TraceMethod
	private void doSomething4(long i, String ss) {
		System.out.println("oiuioy");
		try {
			Thread.sleep(84);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		doSomething5(i, this.getClass().getName());
		doSomething6(i, this.getClass().getName());
	}

	@TraceMethod
	private void doSomething5(long i, String ss) {
		System.out.println("oiuioy");
		try {
			Thread.sleep(9);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@TraceMethod
	private void doSomething6(long i, String ss) {
		System.out.println("oiuioy");
		try {
			Thread.sleep(4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		doSomething7(i, this.getClass().getName());
		doSomething8(i, this.getClass().getName());
	}

	@TraceMethod
	private void doSomething7(long i, String ss) {
		System.out.println("oiuioy");
		try {
			Thread.sleep(34);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@TraceMethod
	private void doSomething8(long i, String ss) {
		System.out.println("this.getClass().getName()");
		try {
			Thread.sleep(16);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		doSomething9(i, this.getClass().getName());
		doSomething10(i, this.getClass().getName());
	}

	@TraceMethod
	private void doSomething9(long i, String ss) {
		System.out.println("oiuioy");
		try {
			Thread.sleep(23);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@TraceMethod
	private void doSomething10(long i, String ss) {
		System.out.println("oiuioy");
		try {
			Thread.sleep(33);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		doSomething12(i, this.getClass().getName());
	}

	@TraceMethod
	private void doSomething12(long i, String ss) {
		System.out.println("oiuioy");
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		doSomething13(i, this.getClass().getName());
		doSomething14(i, this.getClass().getName());
	}

	@TraceMethod
	private void doSomething13(long i, String ss) {
		System.out.println("oiuioy");
		try {
			Thread.sleep(19);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@TraceMethod
	private void doSomething14(long i, String ss) {
		System.out.println("oiuioy");
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		doSomething15(i, this.getClass().getName());
	}

	@TraceMethod
	private void doSomething15(long i, String ss) {
		System.out.println("oiuioy");
		try {
			Thread.sleep(15);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public class InnerClass {
		public String printInner() {
			return "InnerClassHere!";
		}

	}

}
