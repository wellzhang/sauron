package com.fengjr.sauron.converger.kafka.decoder;

import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.fengjr.sauron.commons.utils.DateUtils;
import com.fengjr.sauron.converger.kafka.storage.AppAllLogElasticSearchBulkCommit;

@Component("appLogMsgDecoder")
public class AppLogMsgDecoder implements MsgDecoder {

	private static final String REGEX = " ";
	public Logger logger = LoggerFactory.getLogger(AppLogMsgDecoder.class);

	@Override
	public Map<String, Object> decodeMsg(String line) throws ParseException {
		try {
			if (!filterLog(line)) {
				return null;
			}
			instore(line);
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean filterLog(String log) {

		if (StringUtils.startsWith(log, "10.")) {
			return true;
		}
		return false;
	}

	private void instore(String line) {

		String[] split = line.split(REGEX);

		try {

			if (split.length < 5) {
				return;
			}

			String date = split[3];
			if ((!date.startsWith("20")) || (date.length() != 10) || (date.indexOf("-") != 4) || (date.lastIndexOf("-") != 7)) {
				return;
			}
			String time = split[4];
			if ((!StringUtils.isNumeric(time.substring(0, 1)) || (time.length() != 8 && time.length() != 12) || (time.indexOf(":") != 2) || (time.lastIndexOf(":") != 5))) {
				return;
			}

			DateUtils.String2Date(date + REGEX + time, DateUtils.DEFAULT_MSSTRING_PATTERN_TIMEZONE);// 耗时
		} catch (Exception e) {
			return;
		}

		String timestamp = split[3] + "T" + split[4];

		String app_ip = split[0];
		String index = split[1];
		String type = app_ip + "-" + split[3];
		String categroy = split[2];

		StringBuilder sb = new StringBuilder();

		sb.append("{\"message\":").append(JSON.toJSONString(line)).append(",");
		sb.append("\"categroy\":\"").append(categroy).append("\",");
		sb.append("\"timestamp\":\"").append(timestamp).append("\",");
		sb.append("\"app_ip\":\"").append(app_ip).append("\"}");

		// for (int i = 0; i < 10000; i++) {
		AppAllLogElasticSearchBulkCommit.getInstance().createIndexBulk(index, type, sb.toString());
		// }
	}

	private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

	public static void main(String[] args) throws Exception {

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

		final AppLogMsgDecoder logMsgDecoder = new AppLogMsgDecoder();

		long currentTimeMillis = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {

			threadPoolExecutor.submit(new Runnable() {

				@Override
				public void run() {

					try {
						logMsgDecoder.decodeMsg("10.255.52.19 fengjr-waf-test fengjr-waf-web.log 2016-07-13 10:25:03.832 DEBUG [main-SendThread(10.255.53.151:2181)] - Got ping response for");
					} catch (ParseException e) {
						e.printStackTrace();
					}

				}
			});

		}
		System.out.println("--------------------------------------------------------------------------------------------------------");
		System.out.println(System.currentTimeMillis() - currentTimeMillis);

	}
}
