package com.fengjr.sauron.converger.kafka;

import com.fengjr.sauron.converger.kafka.decoder.MsgDecoder;

/**
 *
 */
public class TopicConfig {

	private String topic;
	private int numThread;
	private MsgDecoder msgDecoder;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getNumThread() {
		return numThread;
	}

	public void setNumThread(int numThread) {
		this.numThread = numThread;
	}

	public MsgDecoder getMsgDecoder() {
		return msgDecoder;
	}

	public void setMsgDecoder(MsgDecoder msgDecoder) {
		this.msgDecoder = msgDecoder;
	}
}
