package com.fengjr.sauron.converger.kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by songlin on 2015/8/27.
 */
public class ConsumerFactory {

	private static final String CONSUMER_PROPERTIES = "consumer.properties";
	private static Logger logger = LoggerFactory.getLogger(ConsumerFactory.class);
	private static ConsumerConnector consumer = null;
	private static volatile boolean isInit = false;
	private static Lock lock = new ReentrantLock();

	private static void init() {
		try {
			lock.lock();
			if (consumer == null) {
				logger.info("Kafka consumer begin to init");
				Properties properties = new Properties();
				try {
					properties.load(ConsumerFactory.class.getClassLoader().getResourceAsStream(CONSUMER_PROPERTIES));
				} catch (IOException e) {
					logger.error("load kafka consumer properties error", e);
				}
				ConsumerConfig cc = new ConsumerConfig(properties);
				consumer = Consumer.createJavaConsumerConnector(cc);
				isInit = true;
				logger.info("Kafka inited over");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public static ConsumerConnector getConsumer() {
		if (!isInit) {
			init();
		}
		return consumer;
	}

}
