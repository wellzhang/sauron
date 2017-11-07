package com.feng.sauron.client.plugin.rocketmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.feng.sauron.client.context.SauronSessionContext;
import com.feng.sauron.client.plugin.AbstractTracerAdapterFactory;
import com.feng.sauron.config.SauronConfig;
import com.feng.sauron.tracer.Tracer;
import com.feng.sauron.tracer.impl.TimerTracer;
import com.feng.sauron.utils.Constants;

/**
 * Created by lianbin.wang on 11/2/16.
 */
public class RocketMQProducerTracerAdapter extends AbstractTracerAdapterFactory implements RocketMQProducerTracerName {
	private static final Logger logger = LoggerFactory.getLogger(RocketMQProducerTracerAdapter.class);// 此logger 只能打印 本类普通log ，不能用于打印sauron log， 需使用父类的logger 打印

	public RocketMQProducerTracerAdapter() {
	}

	private RocketMQProducerTracerAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
		this.spanId = spanId;
		this.spanCount = 0;
		this.status = STATUS.SUCCESS;
		this.methodName = methodName;
		this.className = className;
		this.sourceAppName = sourceAppName;
		this.detail = TRACERNAME_STRING;
		this.type = TRACERNAME_STRING;
		this.paramClazz = paramClazz;
		this.params = params;
		tracerPool.put(TimerTracer.class.getName(), new TimerTracer());
	}

	@Override
	public Tracer getAdapter(String spanId, String className, String methodName, String sourceAppName, Class<?>[] paramClazz, Object[] params) {
		return new RocketMQProducerTracerAdapter(spanId, className, methodName, sourceAppName, paramClazz, params);
	}

	@Override
	public void beforeMethodExecute() {
		super.beforeMethodExecute();

		try {
			if (this.params.length > 0 && this.params[0] instanceof Message) {

				Message message = (Message) this.params[0];

				message.putUserProperty(Constants.SAURON_REQUEST_TRACEID, SauronSessionContext.getTraceId());
				message.putUserProperty(Constants.SAURON_REQUEST_SPANID, getNextSpanId());
				message.putUserProperty(Constants.SAURON_REQUEST_SOURCE_APPNAME, SauronConfig.getAppName());
			}
		} catch (Exception e) {
			logger.debug("error setting property into mq message, ", e.getMessage(), e);
		}
	}

	@Override
	public String printTraceLog() {
		try {

			if (this.params.length > 0 && this.params[0] instanceof Message) {
				Message message = (Message) this.params[0];
				String keys = message.getKeys();
				String topic = message.getTopic();

				this.detail = topic;

				this.methodName = "sendMQ_:" + topic;

				String msgId = null;
				if (this.returnValue != null) {
					SendResult result = (SendResult) this.returnValue;
					msgId = result.getMsgId();
				}

				this.paramClazz = new Class<?>[] { String.class, String.class, String.class };
				this.params = new Object[] { "topic:" + topic, "keys:" + keys, "msgId:" + msgId };
			}
		} catch (Exception e) {
			logger.debug("error setting property into mq message, ", e.getMessage(), e);

			this.paramClazz = null;
			this.params = null;
		}

		return super.printTraceLog();
	}

}
