package com.feng.sauron.warning.task;

import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.dao.NotifyMapper;
import com.feng.sauron.warning.domain.*;
import com.feng.sauron.warning.service.*;
import com.feng.sauron.warning.service.base.ClauseService;
import com.feng.sauron.warning.service.base.ContactsService;
import com.feng.sauron.warning.service.base.RuleReceiverService;
import com.feng.sauron.warning.service.hbase.HbaseMetricsCodeAlarmService;
import com.feng.sauron.warning.util.DateUtils;
import com.feng.sauron.warning.util.WatchableConfigClient;
import com.fengjr.cachecloud.client.IRedis;
import com.fengjr.sauron.dao.hbase.vo.Range;
import com.fengjr.sauron.dao.model.MetricsCodeBulkAlarmData;
import com.mongodb.BasicDBObject;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年12月18日 上午11:00:08
 * 
 */
public class CodeBulkAlarmWarningTask {

	@Resource
	RuleManagementService ruleManagementService;

	@Resource
	ClauseService clauseService;

	@Resource
	ExceptionEventService exceptionEventService;

	@Resource
	ContactsService contactsService;

	@Resource
	RuleReceiverService ruleReceiverService;

	@Resource
	MetricOptInfoService metricOptInfoService;

	@Resource
	MetricsService metricsService;

	@Resource
	NotifyMapper notifyMapper;

	@Resource
	StrategyCheckService strategyCheckService;

	@Resource
	AlarmService alarmService;

	@Resource
	LeaderSelectionClient leaderSelectionClient;

	//@Resource
	//MongoService mongoService;

	@Resource
	HbaseMetricsCodeAlarmService hbaseMetricsCodeAlarmService;

	public static final String COLLECTION_NAME = "metrics_ori_data_codebulk_alarm";

	//private RedisClusterClient redisClient = RedisClusterClientFactory.getRedisClusterClient("redisCache");
	@Autowired
	private IRedis redisClient;

	private static final Logger logger = LoggerFactory.getLogger(CodeBulkAlarmWarningTask.class);

	public void run() {

		String switch_string = WatchableConfigClient.getInstance().get("sauron", "sauron-exception-warning-task-switch", "OFF");

		if (!"ON".equals(switch_string)) {
			return;
		}

		if (!leaderSelectionClient.hasLeaderShip()) {
			logger.info("当前主机为非主节点 定时任务ExceptiongWarningTask不开启");
			return;
		} else {
			logger.info("定时任务ExceptiongWarningTask开启...");
		}

		List<Rules> allRules = ruleManagementService.getAllRules();
		if (allRules == null) {
			return;
		}

		for (Rules rule : allRules) {
			if (rule.getType() != Rules.TypeEnum.DefinedKeyAlarm.val())
				continue;
			List<Clauses> findClausesByRuleId = clauseService.findClausesByRuleId(rule.getId());
			if (findClausesByRuleId == null || findClausesByRuleId.size() == 0) {
				continue;
			}

			boolean flag = false;

			Long metricId = null;

			for (int i = 0; i < findClausesByRuleId.size(); i++) {
				Clauses clauses = findClausesByRuleId.get(i);
				ResponseDTO<MetricOpt> selectMetricsOptById = metricOptInfoService.selectMetricsOptById(clauses.getMetricOptId());
				if (selectMetricsOptById.getCode() == ReturnCode.ACTIVE_SUCCESS.code()) {
					MetricOpt selectByPrimaryKey = selectMetricsOptById.getAttach();
					if (selectByPrimaryKey == null)
						continue;
					if ("exception".equalsIgnoreCase(selectByPrimaryKey.getMetricName())) {
						flag = true; // 包含exception 的 才 去 mongo查
						metricId = selectByPrimaryKey.getId();
						break;
					}
				}
			}

			if (flag) {

				String appName = rule.getAppName();
				String hostName = rule.getHostName();
				long start = System.currentTimeMillis() - 3 * 60 * 1000;
				long end = System.currentTimeMillis();
				//String start = DateUtils.dateFormat(System.currentTimeMillis() - 3 * 60 * 1000);// 最近三分钟
				//String end = DateUtils.format(new Date());


//				BasicDBObject basicDBObject = new BasicDBObject();
//				basicDBObject.put("AppName", appName);
//
//				if (!"all".equalsIgnoreCase(hostName)) {
//					basicDBObject.put("hostName", hostName);
//				}
//
//				basicDBObject.put("Type", "alarm");
//				basicDBObject.put("logtime", new BasicBSONObject("$gt", start).append("$lt", end));// 大于 start 小于 end
//
//				List<Document> findMsg = mongoService.findMsg(COLLECTION_NAME, basicDBObject, 50);
				Range range = new Range(start,end);
				List<MetricsCodeBulkAlarmData> metricsCodeBulkAlarmDatas = hbaseMetricsCodeAlarmService.getMetricsCodeBulkAlarmRange(appName,range);
				if (!"all".equalsIgnoreCase(hostName)) {
					List<MetricsCodeBulkAlarmData> newDatas = new ArrayList<>();
					for( MetricsCodeBulkAlarmData data: metricsCodeBulkAlarmDatas){
						if(data.getHostName().trim().equals(hostName.trim()))
							newDatas.add(data);
					}
					metricsCodeBulkAlarmDatas = newDatas;
				}
				if (metricsCodeBulkAlarmDatas == null || metricsCodeBulkAlarmDatas.size() == 0) {
					continue;
				}
				alarm(metricsCodeBulkAlarmDatas, rule, metricId);
			} else {
				continue;
			}
		}
	}

	@Transactional
	public void alarm(List<MetricsCodeBulkAlarmData> metricsCodeBulkAlarmDatas, Rules rule, Long metricId) {

		List<String> phoneNum = new ArrayList<>();
		List<RuleReceiversKey> selectByRuleId = ruleReceiverService.findReceiversByRule(rule.getId(), RuleReceiversKey.Type.custom.val());
		for (RuleReceiversKey ruleReceiversKey : selectByRuleId) {
			Contact selectByPrimaryKey = contactsService.findContactById(ruleReceiversKey.getContactId());
			phoneNum.add(selectByPrimaryKey.getMobile());
		}
//		if (redisClient == null)
//			redisClient = RedisClusterClientFactory.getRedisClusterClient("redisCache");

		for (MetricsCodeBulkAlarmData  data : metricsCodeBulkAlarmDatas) {
			if (redisClient.exists(data.getTraceId())) {
				logger.debug("重复Traceid：");
				continue;// 避免同一条报警信息 重复报
			} else {
				redisClient.set(String.valueOf(data.getTraceId()), "", 300);// 不存在 保留5分钟
			}
			ExceptionEvent ex = new ExceptionEvent();
			ex.setAppName(rule.getAppName());
			ex.setHostName(rule.getHostName());
			ex.setMethodName(data.getMethodName());
			ex.setExceptionInfo(data.getResult());
			ex.setOccurTime(new Date(data.getLogTime()));
			ex.setParams("type:codebluk_alarm");
			exceptionEventService.insertSelective(ex);

			Notify notify = new Notify();
			notify.setContent(data.getType());
			notify.setRelRuleId(rule.getId());
			notify.setStatus(0);
			notify.setTitle(rule.getAppName());
			notifyMapper.insertSelective(notify);
			String buildSmsText_exception = AlarmService.buildSmsText_exception(data);

			alarmService.sendSms(phoneNum, buildSmsText_exception, rule.getTemplate());

		}
	}
}
