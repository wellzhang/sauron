package com.fengjr.sauron.converger.kafka;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fengjr.sauron.converger.kafka.decoder.MsgDecoder;

/**
 *
 */
public class ConsumerMsgTask implements Runnable {

	private Logger logger = LoggerFactory.getLogger(ConsumerMsgTask.class);

	@SuppressWarnings("rawtypes")
	private KafkaStream m_stream;
	private MsgDecoder m_msgDecoder;
	private String topic;

	private int threadNum = Runtime.getRuntime().availableProcessors() * 2;
	//private int threadNum = 1;
	private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threadNum, threadNum, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

	@SuppressWarnings("rawtypes")
	public ConsumerMsgTask(KafkaStream stream, MsgDecoder decoder, String topic) {
		m_stream = stream;
		m_msgDecoder = decoder;
		this.topic = topic;

		threadPoolExecutor.setRejectedExecutionHandler(new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

				if (!executor.isShutdown()) {
					try {
						executor.getQueue().put(r);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void run() {
		try {
			ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
			while (it.hasNext()) {
				try {
					MessageAndMetadata<byte[], byte[]> mam = it.next();
					final String msg = new String(mam.message());
//					logger.info("-------------topic is:{}, partition is {}", topic, mam.partition());

					threadPoolExecutor.submit(new Runnable() {
						@Override
						public void run() {
							try {
								m_msgDecoder.decodeMsg(msg);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				} catch (Exception e) {
					logger.error("ConsumerMsgTask run exception:", e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("发生异常，退出执行ConsumerMsgTask.run...............................................................");
			logger.info("...................................................................................................");
			logger.info("...................................................................................................");
			logger.info("...................................................................................................");
		}
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

}
