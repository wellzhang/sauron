package com.feng.sauron.warning.task;

import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.dao.NotifyMapper;
import com.feng.sauron.warning.domain.*;
import com.feng.sauron.warning.service.*;
import com.feng.sauron.warning.service.base.ClauseService;
import com.feng.sauron.warning.service.base.ContactsService;
import com.feng.sauron.warning.service.base.RuleReceiverService;
import com.feng.sauron.warning.util.DateUtils;
import com.feng.sauron.warning.util.WatchableConfigClient;

import com.fengjr.cachecloud.client.IRedis;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class H5WarningTask {


	@Resource
	InfluxdbService influxDB;

	@Resource
	RuleManagementService ruleManagementService;

	@Resource
	ClauseService clauseService;

	@Resource
	WarningEventService warningEventService;

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

	private String dbName = "sauron_metrics_h5";
	String table = "sauron_h5";
	private byte ruleType = RuleReceiversKey.Type.Fuction.val();// default

	//private static RedisClusterClient redisClient = RedisClusterClientFactory.getRedisClusterClient("redisCache");
	@Autowired
	private IRedis redisClient;

	private static final Logger logger = LoggerFactory.getLogger(H5WarningTask.class);

	public void readDataFromInfluxdb() {

		String switch_string = WatchableConfigClient.getInstance().get("sauron", "sauron-warning-task-switch", "OFF");

		if (!"ON".equals(switch_string)) {
			return;
		}
		if (!leaderSelectionClient.hasLeaderShip()) {
			logger.info("当前主机为非主节点 定时任务不开启");
			return;
		} else {
			logger.info("定时任务开启...");
		}

		logger.info("h5 task start");

		List<Rules> allRules = ruleManagementService.getAllRules();
		if (allRules == null) {
			return;
		}

		List<String> hAppNameList = null;

		for (Rules rule : allRules) {
			if (!(rule.getType() == Rules.TypeEnum.DefinedKeyAndBlock.val() || rule.getType() == Rules.TypeEnum.Function.val()))
				continue;
			String appName = rule.getAppName();
			String methodName = rule.getMethodName();
			List<Clauses> findClausesByRuleId = clauseService.findClausesByRuleId(rule.getId());
			if (findClausesByRuleId == null || findClausesByRuleId.size() == 0) {
				continue;
			}

			hAppNameList = influxDB.getTagValuesByApp("sauron_metrics_h5","sauron_h5",rule.getAppName(),"appName");
			if(hAppNameList != null && hAppNameList.size() > 0){
				if(!(hAppNameList.contains(rule.getAppName()))){
					continue;
				}
			}else{
				continue;
			}

			String hostName = rule.getHostName();

			String queryString = "SELECT * FROM " + table + "  WHERE  appName = '" + appName + "' AND method = '" + methodName + "' AND hostName = '" + hostName + "' AND time < now() - 1m  AND time > now() - 3m  AND ( ";
			if ("all".equalsIgnoreCase(hostName)) {
				queryString = "SELECT * FROM " + table + "  WHERE  appName = '" + appName + "' AND method = '" + methodName + "' AND time < now() - 1m  AND time > now() - 3m  AND ( ";
			}

			StringBuffer sb = new StringBuffer(queryString);

			StringBuffer metricOpt = new StringBuffer();

			for (int i = 0; i < findClausesByRuleId.size(); i++) {
				Clauses clauses = findClausesByRuleId.get(i);
				ResponseDTO<MetricOpt> selectMetricsOptById = metricOptInfoService.selectMetricsOptById(clauses.getMetricOptId());
				if (selectMetricsOptById.getCode() == ReturnCode.ACTIVE_SUCCESS.code()) {
					MetricOpt selectByPrimaryKey = selectMetricsOptById.getAttach();
					metricOpt.append(selectByPrimaryKey.getMetricName()).append(" ").append(clauses.getOperator()).append(" ").append(clauses.getVarible()).append(" AND ");
				}
			}

			String rulesString = metricOpt.toString().trim();
			if (rulesString.endsWith("AND")) {
				rulesString = rulesString.substring(0, rulesString.lastIndexOf("AND"));
			}

			sb.append(rulesString).append(" ) ");

			sb.append(" order by time desc ");
			QueryResult query = influxDB.query(new Query(sb.toString(), dbName));

			List<Result> results = query.getResults();
			Result result = results.get(0);

			List<Series> series = result.getSeries();
			if (series == null || series.size() == 0 || series.get(0) == null) {
				continue;
			}
			Series series2 = series.get(0);
			List<List<Object>> points = series2.getValues();
			if (points == null || points.size() == 0) {
				continue;
			}

			List<String> arrayList = new ArrayList<>();

			List<RuleReceiversKey> selectByRuleId = ruleReceiverService.findReceiversByRule(rule.getId(), ruleType);
			for (RuleReceiversKey ruleReceiversKey : selectByRuleId) {

				Contact selectByPrimaryKey = contactsService.findContactById(ruleReceiversKey.getContactId());
				arrayList.add(selectByPrimaryKey.getMobile());
			}

			try {
				logger.info("rulesString:"+rulesString);
				alarm(arrayList, points, rulesString, rule);
				logger.info("rulesString:"+rulesString);
			} catch (Exception e) {
				logger.info("warning task ", e);
			}
		}
		logger.info("h5 task end");
	}

	/*
	 * mobileNum 手机号 列表 points 数据点 即 tp count avg method 等数据指标 ruleString 被触发的规则
	 */
	@Transactional
	public void alarm(List<String> mobileNum, List<List<Object>> points, String ruleString, Rules rule) {

		HashMap<String, Object> hashMap = new HashMap<>();
		ConcurrentHashSet<Object> concurrentHashSet = new ConcurrentHashSet<>();

		for (int i = 0; i < points.size(); i++) {

			List<Object> list = points.get(i);

			Object method = list.get(5);
			if (concurrentHashSet.contains(method)) {
				continue;
			}

			Object time = list.get(0);
			Object appName = list.get(1);
			Object hostName = list.get(4);

			hashMap.put("time", time);
			hashMap.put("appName", appName);
			hashMap.put("avg", list.get(2));
			hashMap.put("count", list.get(3));
			hashMap.put("hostName", hostName);
			hashMap.put("method", list.get(5));
			hashMap.put("tp0", list.get(6));
			hashMap.put("tp100", list.get(7));
			hashMap.put("tp90", list.get(8));
			hashMap.put("tp99", list.get(9));
			hashMap.put("tp999", list.get(10));

			if (list.size() == 12) {
				hashMap.put("traceId", list.get(11));
			}

			String buildSmsText_TP = AlarmService.buildSmsText_TP(hashMap, ruleString);

			if (redisClient.exists(buildSmsText_TP)) {
				logger.debug("重复消息：" + buildSmsText_TP);
				continue;// 避免同一条报警信息 重复报
			} else {
				redisClient.set(buildSmsText_TP, "", 300);// 不存在 保留5分钟
			}

			WarningEvent warningEvent = new WarningEvent();

			warningEvent.setAppName(appName.toString());
			warningEvent.setHostName(hostName.toString());
			warningEvent.setInstantName("default_server_1");
			warningEvent.setMethodName(method.toString());
			warningEvent.setOccurTime(new Date(DateUtils.parseEightZone(time.toString())));

			warningEventService.insertSelective(warningEvent);

			// 插入成功会回写 id 值 到bean 此处要判断插入是否成功
			Long event_id = warningEvent.getId();

			ResponseDTO<List<MetricOpt>> selectAllMetricsOpt = metricOptInfoService.selectAllMetricsOpt(0);

			if (selectAllMetricsOpt.getCode() == ReturnCode.ACTIVE_SUCCESS.code()) {
				List<MetricOpt> selectAll = selectAllMetricsOpt.getAttach();
				ArrayList<Metrics> metricsList = new ArrayList<Metrics>();
				for (MetricOpt metricOpt : selectAll) {

					if ("exception".equalsIgnoreCase(metricOpt.getMetricName())) {
						continue;
					}

					Metrics metrics = new Metrics();
					metrics.setMetricOptId(metricOpt.getId());
					metrics.setRelEventId(event_id);
					metrics.setValue((Double) hashMap.get(metricOpt.getMetricName()));
					metricsList.add(metrics);
					metricsService.insertSelective(metrics);
				}
				// metricsMapper.batchInsert(metricsList);
			} else {
				throw new RuntimeException("metricOptInfoService.selectAllMetricsOpt fail throw exception...rollback");
			}

			Notify notify = new Notify();

			notify.setContent(ruleString);
			notify.setRelRuleId(rule.getId());
			notify.setStatus(0);
			notify.setTitle(appName.toString());

			notifyMapper.insertSelective(notify);

			alarmService.sendSms(mobileNum, buildSmsText_TP, rule.getTemplate());

			hashMap.clear();
		}
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void setRuleType(byte ruleType) {
		this.ruleType = ruleType;
	}
}
