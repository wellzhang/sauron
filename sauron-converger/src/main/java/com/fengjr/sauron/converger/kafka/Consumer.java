package com.fengjr.sauron.converger.kafka;

import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class Consumer {

	private Logger logger = LoggerFactory.getLogger(Consumer.class);

	private List<TopicConfig> topicConfigs;

	@SuppressWarnings("rawtypes")
	public void run() {

		try {
			ConsumerConnector consumerConnector = ConsumerFactory.getConsumer();
			Map<String, Integer> topicCountMap = new HashMap<>();

			for (TopicConfig topicConfig : topicConfigs) {
				topicCountMap.put(topicConfig.getTopic(), topicConfig.getNumThread());
			}

			Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
			logger.info("开始接收日志......");
			for (TopicConfig topicConfig : topicConfigs) {
				List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topicConfig.getTopic());
				ExecutorService executor = Executors.newFixedThreadPool(topicConfig.getNumThread());
				for (final KafkaStream stream : streams) {
					executor.submit(new ConsumerMsgTask(stream, topicConfig.getMsgDecoder(), topicConfig.getTopic()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("发生异常，退出执行Consumer.run...................................................................................................");
			logger.info("...................................................................................................");
			logger.info("...................................................................................................");
			logger.info("...................................................................................................");
		}
	}

	public List<TopicConfig> getTopicConfigs() {
		return topicConfigs;
	}

	public void setTopicConfigs(List<TopicConfig> topicConfigs) {
		this.topicConfigs = topicConfigs;
	}
}
