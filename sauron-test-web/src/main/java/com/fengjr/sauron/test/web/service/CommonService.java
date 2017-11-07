package com.fengjr.sauron.test.web.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.wangwei.cs.sauron.annotations.TraceClass;
import com.wangwei.cs.sauron.annotations.TraceMethod;

@TraceClass
@Service
public class CommonService {

	Random random = new Random();

	@TraceMethod
	public void print(String msg) {
		System.out.println("msg:" + msg);
	}

	@TraceMethod
	public void printError(String msg) {

		System.err.println("msg.err:" + msg);

		test(msg);
	}

	public void test(String msg) {

		if (random.nextInt() % 11 == 0) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					System.out.println("ererer");
				}
			}).start();

			Integer.parseInt(msg);
		}

	}

}
