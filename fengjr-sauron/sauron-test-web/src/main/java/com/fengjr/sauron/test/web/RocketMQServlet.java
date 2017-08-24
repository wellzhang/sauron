package com.fengjr.sauron.test.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.feng.ipcenter.service.IPDataFileService;
import com.feng.sauron.client.annotations.TraceClass;
import com.feng.sauron.client.annotations.TraceMethod;

/**
 * Created by lianbin.wang on 11/2/16.
 */

@TraceClass
public class RocketMQServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	DefaultMQProducer producer = new DefaultMQProducer("fengmq-test-group");

	IPDataFileService bean = null;

	@TraceMethod
	@Override
	public void init() throws ServletException {

		producer.setNamesrvAddr("10.255.73.156:9876");
//		producer.setNamesrvAddr("10.255.52.16:9876");
		try {
			producer.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "consumer.xml" });
		bean = (IPDataFileService) context.getBean("ipDataFileService");
		super.init();
		context.start();

	}

	@Override
	@TraceMethod
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Message message = new Message("fengmq-test", "my test content".getBytes());

		try {
			SendResult result = producer.send(message);
			resp.getWriter().write("message send success, msgId:" + result.getMsgId());
		} catch (Exception e) {
			e.printStackTrace();
			resp.getWriter().write("message send fail, " + e.getMessage());
		}

		String find = bean.find("220.181.111.188");
		System.out.println(find);

	}

	@Override
	@TraceMethod
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
