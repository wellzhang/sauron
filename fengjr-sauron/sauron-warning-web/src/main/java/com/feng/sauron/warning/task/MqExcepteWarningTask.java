package com.feng.sauron.warning.task;

import com.feng.sauron.warning.domain.ProducerFailedFlow;
import com.feng.sauron.warning.domain.ScheduleFailedFlow;
import com.feng.sauron.warning.service.AlarmService;
import com.feng.sauron.warning.service.ProducerFailedFlowService;
import com.feng.sauron.warning.service.ScheduleFailedFlowService;
import com.feng.sauron.warning.util.WatchableConfigClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年12月18日 上午11:00:08
 */
public class MqExcepteWarningTask {

	@Resource
	AlarmService alarmService;

	@Resource
	LeaderSelectionClient leaderSelectionClient;

	@Resource
	ProducerFailedFlowService producerFailedFlowService;

	@Resource
	ScheduleFailedFlowService scheduleFailedFlowService;

	private String mq_tem = "conf.alarm.mqconsumer";

	private static final Logger logger = LoggerFactory.getLogger(MqExcepteWarningTask.class);

	private static HashMap<String, String> map = new HashMap<>();

	static {
		loadProperties();
	}

	public void run() {

		String switch_string = WatchableConfigClient.getInstance().get("sauron", "sauron-mq-warning-task-switch", "OFF");

		if (!"ON".equals(switch_string)) {
			return;
		}

		if (!leaderSelectionClient.hasLeaderShip()) {
			logger.info("当前主机为非主节点 定时任务MqExcepteWarningTask不开启");
			return;
		} else {
			logger.info("定时任务MqExcepteWarningTask开启...");
		}

		scheduleFailed();
		producerFailed();
	}

	private static void loadProperties() {
		Properties p = new Properties();
		try {
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("mq-person.properties");
			p.load(inputStream);

			for (Object key : p.keySet()) {
				map.put(key.toString(), p.get(key).toString());
				logger.info(key + "=" + p.get(key));
			}

		} catch (Exception e) {
			logger.error("加载配置mq-person.properties配置文件错误", e);
		}
	}

	public void producerFailed() {
		try {
			List<ProducerFailedFlow> selectWarning = producerFailedFlowService.selectWarning();

			if (selectWarning == null || selectWarning.size() == 0) {
				return;
			}

			HashMap<String, Integer> hashMap = new HashMap<>();

			for (ProducerFailedFlow producerFailedFlow : selectWarning) {
				String topic = producerFailedFlow.getTopic();
				if (hashMap.containsKey(topic)) {
					Integer integer = hashMap.get(topic);
					hashMap.put(topic, integer + 1);
				} else {
					hashMap.put(topic, 1);
				}
			}

			for (String topic : hashMap.keySet()) {
				String string = map.get(topic);
				if (StringUtils.isNotBlank(string)) {
					String[] split = string.split("\\|");
					if (split.length > 1) {
						String phones = split[1];
						String cont = "您在生产消息topic=[" + topic + "]时有[" + hashMap.get(topic) + "]条消息发送失败,请注意，尽快处理 http://10.10.52.27:8095/login.jsp";
						alarmService.sendSms(phones, mq_tem, cont);
					}
				}
			}
		} catch (Exception e) {
			logger.info("producerFailed" + e);
		}
	}

	public void scheduleFailed() {

		try {
			List<ScheduleFailedFlow> selectWarning = scheduleFailedFlowService.selectWarning();

			if (selectWarning == null || selectWarning.size() == 0) {
				return;
			}

			HashMap<String, HashMap<String, Integer>> topic_url = new HashMap<>();

			for (ScheduleFailedFlow scheduleFailedFlow : selectWarning) {
				String topic = scheduleFailedFlow.getMqtopic();
				String url = scheduleFailedFlow.getExecuteurl();

				if (topic_url.containsKey(topic)) {

					HashMap<String, Integer> url_map = topic_url.get(topic);
					if (url_map.containsKey(url)) {
						Integer integer = url_map.get(url);
						url_map.put(url, integer + 1);
					} else {
						url_map.put(url, 1);
					}
				} else {
					HashMap<String, Integer> objectObjectHashMap = new HashMap<>();
					objectObjectHashMap.put(url, 1);
					topic_url.put(topic, objectObjectHashMap);
				}
			}

			for (String topic : topic_url.keySet()) {

				String string = map.get(topic);
				if (StringUtils.isNotBlank(string)) {
					String[] split = string.split("\\|");
					if (split.length > 0) {
						String phones = split[0];
						HashMap<String, Integer> stringIntegerHashMap = topic_url.get(topic);
						for (String url : stringIntegerHashMap.keySet()) {
							String cont = "您在消费消息topic=[" + topic + "],url=[" + url + "],时有[" + stringIntegerHashMap.get(url) + "]条消息接收失败,请尽快处理";
							alarmService.sendSms(phones, mq_tem, cont);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.info("scheduleFailed" + e);
		}
	}
}
