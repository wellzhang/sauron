package com.feng.sauron.warning.service;

import com.feng.sauron.warning.domain.Clauses;
import com.feng.sauron.warning.domain.RuleReceiversKey;
import com.feng.sauron.warning.domain.Rules;
import com.feng.sauron.warning.service.base.ClauseService;
import com.feng.sauron.warning.service.base.RuleReceiverService;
import com.feng.sauron.warning.service.base.RulesService;
import com.feng.sauron.warning.util.ConfigChangeWatcher;
import com.feng.sauron.warning.util.DateUtils;
import com.feng.sauron.warning.util.WatchableConfigClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Liuyb on 2015/12/15.
 */
@Service
public class RuleManagementService {
	@Autowired
	private ClauseService clauseService;
	@Autowired
	private RuleReceiverService ruleReceiverService;
	@Autowired
	private RulesService rulesService;

	private static CopyOnWriteArrayList<Rules> ruleslist = null;

	private static final Logger logger = LoggerFactory.getLogger(RuleManagementService.class);

	private static String format = DateUtils.format(new Date()) + "." + new Random().nextInt(1000);

	private final static String nodeName = "sauron_warning_sync_1";
	
	static {
		try {
			defaultWatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void defaultWatch() {

		WatchableConfigClient.getInstance().create(nodeName, "sauron.warning.rule", "default");

		WatchableConfigClient.getInstance().getAndWatch(nodeName, "sauron.warning.rule", "default", new ConfigChangeWatcher() {

			@Override
			public void onValueChanged(String newVal) {
				if (!format.equalsIgnoreCase(newVal)) {
					logger.info("检测到其他服务节点更新了rule , 开始刷新缓存rule缓存");
					ruleslist = null;
				} else {
					logger.info("此次更新rule 来自 服务本身 ,已经刷新过,忽略...");
				}
			}
		});
	}

	public static void watch() {

		try {
			String format_now = DateUtils.format(new Date()) + "." + new Random().nextInt(1000);
			WatchableConfigClient.getInstance().set(nodeName, "sauron.warning.rule", format_now);
			format = format_now;
		} catch (Exception e) {
		}
	}

	@Transactional
	public void addNewRule(Rules rule, List<RuleReceiversKey> ruleReceiversList, List<Clauses> clausesList) {
		rulesService.addNewRule(rule);
		if(ruleReceiversList!=null){
			Long id = rule.getId();
			for (RuleReceiversKey ruleReceivers : ruleReceiversList) {
				ruleReceivers.setRuleId(id);
			}
			ruleReceiverService.addReceiversToRule(ruleReceiversList);
		}
		if(clausesList!=null){
			for (Clauses clause : clausesList) {
				clause.setRelRuleId(rule.getId());
			}
			clauseService.addClausesToRule(clausesList);
		}

	}

	@Transactional
	public void modifyRule(Rules rule, List<RuleReceiversKey> ruleReceiversList, List<Clauses> clausesList) {
		rulesService.updateRule(rule);
		// 先删除规则下所有接收人，再重新添加
		if(ruleReceiversList != null){
			ruleReceiverService.delReceiversByRuleId(rule.getId(),RuleReceiversKey.Type.Fuction.val());
			for (RuleReceiversKey ruleReceivers : ruleReceiversList) {
				ruleReceivers.setRuleId(rule.getId());
			}
			ruleReceiverService.addReceiversToRule(ruleReceiversList);
		}
		// 先删除规则下所有条款，再重新添加
		if(clausesList != null){
			clauseService.delClausesByRelRuleId(rule.getId());
			for (Clauses clause : clausesList) {
				clause.setRelRuleId(rule.getId());
			}
			clauseService.addClausesToRule(clausesList);
		}
	}

	public List<Rules> listAllRule() {
		return rulesService.getAllRules();
	}

	public List<Rules> listByPager(int pageNo, int pageSize, String hostName, String appName, String methodName,String creatorId,byte type) {
		return rulesService.findByPager(pageNo, pageSize, hostName, appName, methodName,creatorId,type);
	}

	public int getTotalCount(String hostName, String appName, String methodName,String creatorId, byte type) {
		return rulesService.findCount(hostName, appName, methodName,creatorId,type);
	}

	public List<Rules> listByPager(int pageNo, int pageSize, String hostName, String appName, String methodName,String creatorId) {
		return rulesService.findConstomByPager(pageNo, pageSize, hostName, appName, methodName,creatorId);
	}

	public int getTotalCount(String hostName, String appName, String methodName,String creatorId) {
		return rulesService.findConstomCount(hostName, appName, methodName,creatorId);
	}

	@Transactional
	public void delRule(long ruleId) {
		rulesService.delRule(ruleId);
		ruleReceiverService.delReceiversByRuleId(ruleId,RuleReceiversKey.Type.Fuction.val());
		clauseService.delClausesByRelRuleId(ruleId);
	}

	public List<Rules> getAllRules() {

		if (ruleslist == null) {
			init();
		}
		return ruleslist;
	}

	public synchronized void reflushRule() {
		init();//如果此处注掉    那么需要watch 里面  不限制 format 是否匹配   
		watch();
	}

	public void init() {
		ruleslist = new CopyOnWriteArrayList<Rules>();
		ruleslist.addAll(listAllRule());
	}
}
