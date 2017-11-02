package com.fengjr.sauron.converger.kafka;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

public class ProducerFactory extends Thread {

	private String topic;

	public ProducerFactory(String topic) {
		super();
		this.topic = topic;
	}

	@Override
	public void run() {
		Producer<Integer, String> producer = createProducer();
		while (true) {
			try {

				// String writerAsString = ObjectMapperHelper.writerAsString("username", i);
				String writerAsString = "[mw01-error] [183.50.109.203] [-] [19/Apr/2016:14:51:21 +0800] [GET /h5/js/main-1604191350.js HTTP/1.1] [200] [332735] [https://m.fengjr.com/h5/chaihongbao?theme=1&channel=ifengxxl05wzl-45] [Mozilla/5.0 (Linux; Android 5.1; HUAWEI RIO-AL00 Build/HuaweiRIO-AL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/39.0.0.0 Mobile Safari/537.36] [-] [-] [0.405] [m.fengjr.com]";

				producer.send(new KeyedMessage<Integer, String>(topic, writerAsString));
				System.out.println(topic + "  " + writerAsString);

				// TimeUnit.SECONDS.sleep(1);
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Producer<Integer, String> createProducer() {
		Properties properties = new Properties();
		properties.put("zookeeper.connect", "tzk1.fengjr.inc:2181,tzk1.fengjr.inc:2181,tzk1.fengjr.inc:2181");// 声明zk
		properties.put("serializer.class", StringEncoder.class.getName());
		// properties.put("serializer.class", DefaultEncoder.class.getName());

		properties.put("metadata.broker.list", "10.255.52.3:9092,10.255.52.3:9093,10.255.52.3:9094");// 声明kafka broker
		return new Producer<Integer, String>(new ProducerConfig(properties));
	}

	public static void main(String[] args) {
		new ProducerFactory("test").start();// 使用kafka集群中创建好的主题 test

	}

}
