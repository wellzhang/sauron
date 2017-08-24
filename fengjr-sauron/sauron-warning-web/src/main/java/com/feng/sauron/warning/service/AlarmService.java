package com.feng.sauron.warning.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.fengjr.sauron.dao.model.MetricsCodeBulkAlarmData;
import com.fengjr.sauron.dao.model.MetricsOriData;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.feng.sauron.warning.common.TPEnum;
import com.feng.sauron.warning.monitor.dubbo.DubboAliveMonitor;
import com.feng.sauron.warning.util.DateUtils;
import com.feng.tsc.xhttpclient.async.nettyImpl.DefaultNeXHttpClient;
import com.feng.tsc.xhttpclient.async.nettyImpl.NeXHttpClientFactory;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年5月13日 上午11:17:39
 * 
 */

@Service
public class AlarmService {

	public final static String SMS_TYPE = "sauron.warning.template_no";
	final static DefaultNeXHttpClient client = NeXHttpClientFactory.Custom().setConnectTimeout(500).setReadTimeout(500).setMaxConnections(200).setMaxConnectionsPerHost(50).make();
	private final static Logger logger = LoggerFactory.getLogger(AlarmService.class);
	// private final static String SMS_URL_PARTTEN = "http://10.255.52.19:8097/sms/send?mobileNum=%s&smsType=%s&content=%s";//test
	private final static String SMS_URL_PARTTEN = "http://10.10.52.180:16680/sms/send?mobileNum=%s&smsType=%s&content=%s";// online

	@Resource
	StrategyCheckService strategyCheckService;

	public void sendSms(List<String> mobileNum, String content, Long strategyId) {

		for (String string : mobileNum) {

			boolean check = strategyCheckService.check(SMS_TYPE, string, strategyId);
			if (check) {
				sendSms(string, SMS_TYPE, content);
			}
		}
	}
	
	public static void main(String[] args) {
		AlarmService alarmService = new AlarmService();
		alarmService.sendSms("18671024882", SMS_TYPE, "test——e——_SSdr");

	}

	public void sendSms(String mobileNum, String smsType, String content) {

		String url = String.format(SMS_URL_PARTTEN, mobileNum, smsType, content);

		final Request request = new RequestBuilder().setMethod("get").setUrl(url).build();

		try {
			Response response = client.execute(request, 500, TimeUnit.MILLISECONDS);
			logger.info("AlarmService sendSms response url:{}, status:{}, text:{}", url, response.getStatusCode(), response.getResponseBody());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static String buildSmsText_TP(Map<String, Object> map, String ruleString) {

		StringBuilder smsText = new StringBuilder("Trigger alarm rule:" + ruleString + ", out of limits：");
		smsText.append("[time=").append(DateUtils.parseEightZoneTimeToStande(String.valueOf(map.get("time")))).append("]");
		smsText.append("[hostName=").append(String.valueOf(map.get("hostName"))).append("]");
		smsText.append("[appName=").append(String.valueOf(map.get("appName"))).append("]");
		smsText.append("[method=").append(String.valueOf(map.get("method"))).append("]");

		if (map.containsKey("traceId")) {
			smsText.append("[traceId=").append(String.valueOf(map.get("traceId"))).append("]");
		}

		Double tp_90 = (Double) map.get(TPEnum.TP_90.getName());
		Double tp_99 = (Double) map.get(TPEnum.TP_99.getName());
		Double tp_999 = (Double) map.get(TPEnum.TP_999.getName());
		Double min = (Double) map.get(TPEnum.TP_0.getName());
		Double max = (Double) map.get(TPEnum.TP_100.getName());
		Double avg = (Double) map.get("avg");
		Double count = (Double) map.get("count");

		smsText.append("[tp_90=").append(tp_90.intValue()).append("]");
		smsText.append("[tp_99=").append(tp_99.intValue()).append("]");
		smsText.append("[tp_999=").append(tp_999.intValue()).append("]");

		smsText.append("[min=").append(min.intValue()).append("]");
		smsText.append("[max=").append(max.intValue()).append("]");
		smsText.append("[avg=").append(avg.intValue()).append("]");
		smsText.append("[count=").append(count.intValue()).append("]");

		return smsText.toString();
	}

	public static String buildSmsText_exception(Document map) {

		StringBuilder smsText = new StringBuilder("Trigger alarm rule , exception：");
		smsText.append("[time=").append(String.valueOf(map.get("logtime"))).append("]");
		smsText.append("[hostName=").append(String.valueOf(map.get("hostName"))).append("]");
		smsText.append("[appName=").append(String.valueOf(map.get("AppName"))).append("]");
		smsText.append("[method=").append(String.valueOf(map.get("methodName"))).append("]");
		smsText.append("[exception=").append(String.valueOf(map.get("Exception"))).append("]");

		smsText.append("[traceId=").append(String.valueOf(map.get("Traceid"))).append("]");
		smsText.append("[duration=").append(String.valueOf(map.get("duration"))).append("ms]");

		return smsText.toString();
	}

	public static String buildSmsText_exception(MetricsCodeBulkAlarmData data) {

		StringBuilder smsText = new StringBuilder("Trigger alarm rule , exception：");
		smsText.append("[time=").append(new Date(data.getLogTime())).append("]");
		smsText.append("[hostName=").append(data.getHostName()).append("]");
		smsText.append("[appName=").append(data.getAppName()).append("]");
		smsText.append("[method=").append(data.getMethodName()).append("]");
		smsText.append("[exception=").append(data.getResult()).append("]");

		smsText.append("[traceId=").append(data.getTraceId()).append("]");
		//smsText.append("[duration=").append(data.get).append("ms]");

		return smsText.toString();
	}
	public static String buildSmsText_exception(MetricsOriData data) {

		StringBuilder smsText = new StringBuilder("Trigger alarm rule , exception：");
		smsText.append("[time=").append(new Date(data.getLogTime())).append("]");
		smsText.append("[hostName=").append(data.getHostName()).append("]");
		smsText.append("[appName=").append(data.getAppName()).append("]");
		smsText.append("[method=").append(data.getMethodName()).append("]");
		smsText.append("[exception=").append(data.getResult()).append("]");
		smsText.append("[traceId=").append(data.getTraceId()).append("]");
		smsText.append("[duration=").append(data.getDuration()).append("ms]");

		return smsText.toString();
	}


	public static String buildSmsText_dubboAliveMonitor(String[] strings) {

		StringBuilder smsText = new StringBuilder("[Dubbo Alive Monitor] ");

		if (DubboAliveMonitor.LOSE_ALL.equals(strings[0])) {
			smsText.append("服务:").append(strings[3]).append(",");
		} else {
			smsText.append("应用:").append(strings[1]).append(",");
		}

		if (DubboAliveMonitor.ADD.equals(strings[0])) {
			smsText.append("新增节点:").append(strings[2]).append(",");
		} else if (DubboAliveMonitor.LOSE.equals(strings[0])) {
			smsText.append("丢失节点:").append(strings[2]).append(",");
		} else if (DubboAliveMonitor.LOSE_ALL.equals(strings[0])) {
			smsText.append("丢失全部提供者,请关注").append(",");
		}

		if (DubboAliveMonitor.LOSE_ALL.equals(strings[0])) {
			smsText.append("原地址为:").append(strings[2]);
		} else {
			smsText.append("目前存活节点:").append(strings[2]);
		}

		return smsText.toString();
	}

}
