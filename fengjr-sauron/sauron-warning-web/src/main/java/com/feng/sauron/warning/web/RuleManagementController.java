package com.feng.sauron.warning.web;

import com.feng.sauron.warning.common.Page;
import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.domain.*;
import com.feng.sauron.warning.service.InfluxdbService;
import com.feng.sauron.warning.service.MetricOptInfoService;
import com.feng.sauron.warning.service.RuleManagementService;
import com.feng.sauron.warning.service.base.*;
import com.feng.sauron.warning.util.InfluxdbUtils;
import com.feng.sauron.warning.web.base.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Liuyb on 2015/12/14.
 */
@Controller
@RequestMapping("/rule")
public class RuleManagementController extends BaseController {
	@Autowired
	RuleManagementService ruleManagementService;
	@Autowired
	InfluxdbService influxdbService;
	@Autowired
	ContactsService contactsService;
	@Autowired
	MetricOptInfoService metricOptInfoService;
	@Autowired
	RuleReceiverService receiverService;
	@Autowired
	ClauseService clauseService;
	@Autowired
	RulesService rulesService;

	@Autowired
	private AppService appService;

	@Autowired
	StrategyService strategyService;

	@Autowired
	private UserService userService;

	// @RequestMapping(value = "")
	// public String listRule(Model model) {
	// Page<Rules> pageData = new Page<Rules>();
	// pageData.setDataList(ruleManagementService.listByPager(0, 20, "", "", ""));
	// pageData.setPageSize(20);
	// pageData.setPageNO(1);
	// model.addAttribute("pageData", pageData);
	// return "rule/ruleList";
	// }

	@RequestMapping(value = "/matchMethodName")
	@ResponseBody
	public ResponseDTO matchMethodName(Model model,String methodName){
		ResponseDTO responseDTO = new ResponseDTO<Object>();
		responseDTO.setCode(ReturnCode.ACTIVE_EXCEPTION.code());
		Map<String,String> map = new HashMap<>();
		map.put("methodName","11111");
		Map<String,String> map1 = new HashMap<>();
		map1.put("methodName","2222");
		List<Map<String,String>> list = new ArrayList<>();
		list.add(map);
		list.add(map1);
		responseDTO.setAttach(list);
		responseDTO.setCode(ReturnCode.ACTIVE_SUCCESS.code());
		return responseDTO;
	}



	@RequestMapping(value = "/query")
	public String queryRule(HttpServletRequest request, Model model) {
		String hostName = request.getParameter("hostName");
		String appName = request.getParameter("appName");
		String methodName = request.getParameter("methodName");
		String pageNo = request.getParameter("pageNo");
		String type = request.getParameter("type");

		Page<Rules> pageData = new Page<>();
		pageData.setDataList(rulesService.findByPager(1, 10, hostName, appName, methodName,getCreatorId(),Byte.valueOf(type)));
		pageData.setPageSize(10);
		pageData.setPageNO(1);
		pageData.setTotal(ruleManagementService.getTotalCount(hostName, appName, methodName,getCreatorId(),Byte.valueOf(type)));
		model.addAttribute("pageData", pageData);
		return "rule/methodRuleList";

	}

	@RequestMapping(value = "/list/{pageNo}/{pageSize}/{type}")
	public String listRule(@PathVariable(value = "pageNo") int pageNo, @PathVariable(value = "pageSize") int pageSize,
						   @PathVariable(value = "type")  byte type , String hostName, String appName,
						   String methodName, Model model) {
		Page<Rules> pageData = new Page<Rules>();
		pageData.setPageSize(pageSize);
		pageData.setPageNO(pageNo);
		if(type == Rules.TypeEnum.Function.val()){
			pageData.setDataList(ruleManagementService.listByPager(pageNo, pageSize, hostName, appName, methodName,getCreatorId(),type));
			pageData.setTotal(ruleManagementService.getTotalCount(hostName, appName, methodName,getCreatorId(),type));
		}else{
			pageData.setDataList(ruleManagementService.listByPager(pageNo, pageSize, hostName, appName, methodName,getCreatorId()));
			pageData.setTotal(ruleManagementService.getTotalCount(hostName, appName, methodName,getCreatorId()));
		}
		model.addAttribute("pageData", pageData);
		model.addAttribute("hostName", hostName);
		model.addAttribute("appName", appName);
		model.addAttribute("methodName", methodName);
		return (type == Rules.TypeEnum.Function.val())?"rule/methodRuleList":"rule/customRuleList";
	}

//	@RequiresPermissions("rule:modify")
	@RequestMapping(value = "/modify/{ruleId}", method = RequestMethod.GET)
	public String modifyRule(@PathVariable("ruleId") Long ruleId, Model model) {
//		List<String> appNameList = influxdbService.getTagValues("sauron_metrics", "sauron", "appName");
		List<App> appNameList = appService.findByPage(1,1000,null,getCreatorId());
		List<Contact> contactsList = contactsService.getAllContactsInfo();
		List<Strategy> strategyList = strategyService.selectAll();
		List<MetricOpt> metricOptList = metricOptInfoService.selectAllMetricsOpt(0).getAttach();
		model.addAttribute("appName", appNameList);

		model.addAttribute("contactsList", contactsList);
		model.addAttribute("metricOptList", metricOptList);
		model.addAttribute("strategyList", strategyList);

		Rules rule = rulesService.getRuleById(ruleId);
		List<String> hostNameList = influxdbService.getTagValuesByApp("sauron_metrics", "sauron", rule.getAppName(), "hostName");
		List<String> methodNameList = influxdbService.getTagValuesByApp("sauron_metrics", "sauron", rule.getAppName(), "method");

		List<String> hHostNameList = influxdbService.getTagValuesByApp("sauron_metrics_h5","sauron_h5",rule.getAppName(),"hostName");
		List<String> hMethodNameList = influxdbService.getTagValuesByApp("sauron_metrics_h5","sauron_h5",rule.getAppName(),"method");

		hHostNameList.addAll(hHostNameList);
		methodNameList.addAll(hMethodNameList);

//		BatchPoints batchPoints = InfluxdbUtils.getH5Builder().tag("appName", "tt").tag("hostName", "t_t").tag("method", "com.com").build();

//		Point point = Point.measurement(InfluxdbUtils.table_h5).time(logtimedDate.getTime(), TimeUnit.MILLISECONDS).field("tp0", Integer.valueOf(tp0)).field("tp90", Integer.valueOf(tp90)).field("tp99", Integer.valueOf(tp99)).field("tp999", Integer.valueOf(tp999))
//				.field("tp100", Integer.valueOf(tp100)).field("count", totalCount).field("avg", sampling_avgvalue).build();
//
//		batchPoints.point(point);

//		InfluxDB influxDB = InfluxdbUtils.getInfluxDB();
//
//		influxDB.write(batchPoints);

		model.addAttribute("hostName", hostNameList);
		model.addAttribute("methodName", methodNameList);
		List<RuleReceiversKey> receivers = receiverService.findReceiversByRule(ruleId, RuleReceiversKey.Type.Fuction.val());
		List<Long> receiversIdList = new LinkedList<>();
		String notifyMode = null;
		for (RuleReceiversKey receiver : receivers) {
			receiversIdList.add(receiver.getContactId());
			notifyMode = receiver.getNotifyMode();
		}
		List<Clauses> clauses = clauseService.findClausesByRuleId(ruleId);

		model.addAttribute("notifyMode",notifyMode);
		model.addAttribute("rule", rule);
		model.addAttribute("receivers", receiversIdList);
		model.addAttribute("clauses", clauses);

		model.addAttribute("operType", "modify");
		model.addAttribute("ruleId",ruleId);

		return (rule.getType() == Rules.TypeEnum.Function.val())?"rule/addMethodRule":"rule/addCustomRule";
	}

	@ResponseBody
//	@RequiresPermissions("rule:del")
	@RequestMapping(value = "/del/{ruleId}", method = RequestMethod.GET)
	public ResponseDTO delRule(@PathVariable(value = "ruleId") int ruleId) {
		ResponseDTO responseDTO = new ResponseDTO<Object>();
		try {
			ruleManagementService.delRule(ruleId);

			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception ex) {
			responseDTO.setMsg(ex.getMessage());
			responseDTO.setReturnCode(ReturnCode.ACTIVE_EXCEPTION);
		}
		ruleManagementService.reflushRule();
		return responseDTO;
	}

	/**
	 * 添加页面前
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addRule(Model model) {
//		List<String> appNameList = influxdbService.getTagValues("sauron_metrics", "sauron", "appName");
		List<App> appNameList = appService.findByPage(1,1000,null,getCreatorId());

		List<String> hHostNameList = influxdbService.getTagValues("sauron_metrics_h5","sauron_h5","appName");


		//h5 特殊逻辑
		List<String> tableAppNames = new ArrayList<>();
		for(App app : appNameList){
			tableAppNames.add(app.getName());
		}

		App app = null;
		if(hHostNameList !=null && hHostNameList.size()>0){
			for(String appName : hHostNameList){
				if(tableAppNames.contains(appName)){
					continue;
				}else{
					if(appName.equals("appName")){
						continue;
					}
					app = new App();
					app.setName(appName);
					app.setDescribes("html5");
					app.setUserId("please enter uuid");
					appService.addApp(app);
				}
			}
		}

		appNameList = appService.findByPage(1,1000,null,getCreatorId());




//		List<String> appNameListStr = new ArrayList<>();
//		for(App app:appNameList){
//			appNameListStr.add(app.getName());
//		}
		List<String> hostNameList = influxdbService.getTagValuesByApp("sauron_metrics", "sauron", "sauron","hostName");
//		List<String> methodNameList = influxdbService.getTagValues("sauron_metrics", "sauron", "method");
		List<Contact> contactsList = contactsService.getAllContactsInfo();
		List<MetricOpt> metricOptList = metricOptInfoService.selectAllMetricsOpt(0).getAttach();
		List<Strategy> strategyList = strategyService.selectAll();
		model.addAttribute("hostName", hostNameList);
		model.addAttribute("appName", appNameList);
//		model.addAttribute("methodName", methodNameList);
		model.addAttribute("contactsList", contactsList);
		model.addAttribute("metricOptList", metricOptList);
		model.addAttribute("operType", "add");
		model.addAttribute("strategyList", strategyList);

		return "rule/addMethodRule";
	}



	@RequestMapping(value = "/addCustom", method = RequestMethod.GET)
	public String addRuleCustom(Model model) {

		List<App> appNameList = appService.findByPage(1,1000,null,getCreatorId());
		List<Contact> contactsList = contactsService.getAllContactsInfo();
		List<MetricOpt> metricOptList = metricOptInfoService.selectAllMetricsOpt(0).getAttach();
		List<Strategy> strategyList = strategyService.selectAll();
		model.addAttribute("appName", appNameList);
		model.addAttribute("contactsList", contactsList);
		model.addAttribute("metricOptList", metricOptList);
		model.addAttribute("operType", "add");
		model.addAttribute("strategyList", strategyList);

		return "rule/addCustomRule";
	}

	@ResponseBody
	@RequestMapping(value = "/detail/{ruleId}", method = RequestMethod.GET)
	public ResponseDTO<Map<String, Object>> getDetailInfo(@PathVariable("ruleId") long ruleId) {
		ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
		try {
			Map<String, Object> resultMap = new HashMap<>();
			Rules rule = rulesService.getRuleById(ruleId);
			List<RuleReceiversKey> receivers = receiverService.findReceiversByRule(ruleId, RuleReceiversKey.Type.Fuction.val());
			List<Contact> receiverContacts = new LinkedList<>();
			List<MetricOpt> metricOpts = metricOptInfoService.selectAllMetricOpt(0);
			for (RuleReceiversKey receiver : receivers) {
				receiverContacts.add(contactsService.findContactById(receiver.getContactId()));
			}
			List<Clauses> clauses = clauseService.findClausesByRuleId(ruleId);
			resultMap.put("rule", rule);
			resultMap.put("receivers", receiverContacts);
			resultMap.put("clauses", clauses);
			resultMap.put("metricOpts", metricOpts);
			responseDTO.setAttach(resultMap);
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception e) {
			responseDTO.setMsg(e.getMessage());
		}
		return responseDTO;
	}

	/**
	 * 点击后的处理
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/CreateRule", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseDTO<String> addRule(HttpServletRequest request, String hostName, String appName, String methodName, Long appId, String metricsOpt, String contact, Long strategyId, String contactType, byte type, String monitorKey) {

		ResponseDTO<String> responseDTO = new ResponseDTO<>();
		responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
//		if (StringUtils.isBlank(hostName) || StringUtils.isBlank(appName) || StringUtils.isBlank(methodName) || StringUtils.isBlank(metricsOpt) || StringUtils.isBlank(contact)) {

		if(type == Rules.TypeEnum.Function.val()){
			if (StringUtils.isBlank(hostName) || StringUtils.isBlank(appName) || StringUtils.isBlank(methodName)) {
				responseDTO.setMsg("参数不能为空");
				return responseDTO;
			}
		}else{
			if (StringUtils.isBlank(monitorKey)) {
				responseDTO.setMsg("参数不能为空");
				return responseDTO;
			}
		}

		Rules rules = new Rules();
		rules.setType(type);
		if(type == Rules.TypeEnum.Function.val()){
			rules.setMethodName(methodName);
			rules.setHostName(hostName);
		}else{
			rules.setMonitorKey(monitorKey);
		}

		rules.setCreatedTime(new Date());
		rules.setCreatorId(1l);
		rules.setAppId(appId);
		rules.setTemplate(strategyId);
		App app = appService.findAppById(appId);
		rules.setAppName(app.getName());
		List<RuleReceiversKey> ruleReceiversList = null;
		if(StringUtils.isNotBlank(contact)){
			ruleReceiversList = new ArrayList<>();
			String[] split3 = contact.split(",");
			for (int i = 0; i < split3.length; i++) {
				RuleReceiversKey ruleReceiversKey = new RuleReceiversKey();
				Long contactId = Long.parseLong(split3[i]);
				ruleReceiversKey.setContactId(contactId);
				ruleReceiversKey.setType(RuleReceiversKey.Type.Fuction.val());
				if(StringUtils.isNotBlank(contactType)){
					ruleReceiversKey.setNotifyMode(contactType);
				}
				ruleReceiversList.add(ruleReceiversKey);
			}
		}
		List<Clauses> clausesList = null;

		if(StringUtils.isNotBlank(metricsOpt)){
			clausesList = new ArrayList<Clauses>();
			String[] split = metricsOpt.split(",_,");
			for (int i = 0; i < split.length; i++) {
				String[] split2 = split[i].split(",");
				Long metricOptId = Long.parseLong(split2[1]);
				String operoter = split2[3];
				Long varible = Long.parseLong(split2[4]);
				Clauses clauses = new Clauses();
				clauses.setOperator(operoter);
				clauses.setMetricOptId(metricOptId);
				clauses.setVarible(varible);
				clausesList.add(clauses);
			}
		}
		ruleManagementService.addNewRule(rules, ruleReceiversList, clausesList);
		ruleManagementService.reflushRule();
		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/modifyRule", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseDTO<String> modifyRule(HttpServletRequest request, Long ruleId, String hostName, String appName, Long appId, String methodName, String metricsOpt, String contact, Long strategyId, String contactType, byte type, String monitorKey) {

		ResponseDTO<String> responseDTO = new ResponseDTO<>();
		responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
//		if (StringUtils.isBlank(hostName) || StringUtils.isBlank(appName) || StringUtils.isBlank(methodName) || StringUtils.isBlank(metricsOpt) || StringUtils.isBlank(contact)) {
		if(type == Rules.TypeEnum.Function.val()){
			if (StringUtils.isBlank(hostName) || StringUtils.isBlank(appName) || StringUtils.isBlank(methodName)) {
				responseDTO.setMsg("参数不能为空");
				return responseDTO;
			}
		}

		String[] split = metricsOpt.split(",_,");

		Rules rules = new Rules();
		rules.setId(ruleId);
		rules.setType(type);
		if(type == Rules.TypeEnum.Function.val()){
			rules.setMethodName(methodName);
			rules.setHostName(hostName);
		}else{
			rules.setMonitorKey(monitorKey);
		}
		rules.setAppId(appId);
		rules.setCreatedTime(new Date());
		rules.setCreatorId(1l);
		rules.setTemplate(strategyId);
		App app = appService.findAppById(appId);
		rules.setAppName(app.getName());

		List<RuleReceiversKey> ruleReceiversList = null;
		if(StringUtils.isNotBlank(contact)){
			ruleReceiversList = new ArrayList<>();
			String[] split3 = contact.split(",");
			for (int i = 0; i < split3.length; i++) {
				RuleReceiversKey ruleReceiversKey = new RuleReceiversKey();
				Long contactId = Long.parseLong(split3[i]);
				ruleReceiversKey.setContactId(contactId);
				ruleReceiversList.add(ruleReceiversKey);
				ruleReceiversKey.setType(RuleReceiversKey.Type.Fuction.val());
				if(StringUtils.isNotBlank(contactType)){
					ruleReceiversKey.setNotifyMode(contactType);
				}
			}
		}

		List<Clauses> clausesList = null;

		if(StringUtils.isNotBlank(metricsOpt)){
			clausesList = new ArrayList<Clauses>();
			for (int i = 0; i < split.length; i++) {
				String[] split2 = split[i].split(",");
				Long metricOptId = Long.parseLong(split2[1]);
				String operoter = split2[3];
				Long varible = Long.parseLong(split2[4]);
				Clauses clauses = new Clauses();
				clauses.setOperator(operoter);
				clauses.setMetricOptId(metricOptId);
				clauses.setVarible(varible);
				clausesList.add(clauses);
			}
		}

		ruleManagementService.modifyRule(rules, ruleReceiversList, clausesList);
		ruleManagementService.reflushRule();
		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/updateSelect")
	public ResponseDTO<Map<String, List>> queryTagOnAppChange(String appName) {
		ResponseDTO<Map<String, List>> responseDTO = new ResponseDTO<>();
		try {
			List<String> hostNameList = influxdbService.getTagValuesByApp("sauron_metrics", "sauron", appName, "hostName");
			List<String> methodNameList = influxdbService.getTagValuesByApp("sauron_metrics", "sauron", appName, "method");
			Map<String, List> map = new HashMap<>();
			map.put("hostName", hostNameList);
			map.put("methodName", methodNameList);
			responseDTO.setAttach(map);
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception ex) {
			responseDTO.setReturnCode(ReturnCode.ACTIVE_EXCEPTION);
		}
		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/getMethodNames")
	public ResponseDTO queryMethodNames(String appName){
		ResponseDTO<List<String>> responseDTO = new ResponseDTO<>();
		try{
			//todo  h5  inflexDB hbase?
			List<String> methodNameList = influxdbService.getTagValuesByApp(InfluxdbUtils.dbName, "sauron", appName, "method");
			if(methodNameList.size()>0){
				responseDTO.setAttach(methodNameList);
			}else{
				List<String> hMethodNameList = influxdbService.getTagValuesByApp("sauron_metrics_h5","sauron_h5",appName,"method");
				responseDTO.setAttach(hMethodNameList);
			}

			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		}catch(Exception e){
			responseDTO.setReturnCode(ReturnCode.ACTIVE_FAILURE);
		}
		return responseDTO;
	}


	@Override
	protected UserService getUserService() {
		return userService;
	}
}
