package com.feng.sauron.warning.web;

import com.feng.sauron.warning.common.Page;
import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.domain.*;
import com.feng.sauron.warning.service.base.*;
import com.feng.sauron.warning.web.base.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * DubboRulesController Created by jianzhang
 */
@Controller
@RequestMapping("/dubboRule")
public class DubboRulesController extends BaseController {

	@Autowired
	private AppService appService;

	@Autowired
	private DubboRulesService dubboRulesService;

	@Autowired
	ContactsService contactsService;

	@Autowired
	StrategyService strategyService;

	@Autowired
	private RuleReceiverService ruleReceiverService;

	@Autowired
	private ZookeeperIpsService zookeeperIpsService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/list/{pageNo}/{pageSize}")
	public String list(@PathVariable(value = "pageNo") int pageNo, @PathVariable(value = "pageSize") int pageSize, String appName, String applicationName, Model model) {
		Page<Map<String, Object>> pageData = new Page<Map<String, Object>>();
		pageData.setDataList(dubboRulesService.findByPage(pageNo, pageSize, appName, applicationName, getCreatorId()));
		pageData.setPageSize(pageSize);
		pageData.setPageNO(pageNo);
		pageData.setTotal(dubboRulesService.findCount(appName, applicationName, getCreatorId()));
		model.addAttribute("pageData", pageData);
		return "rule/dubboRuleList";
	}

	@RequestMapping(value = "/query")
	public String queryRule(HttpServletRequest request, Model model) {
		String appName = request.getParameter("appName");
		String applicationName = request.getParameter("applicationName");

		Page<Map<String, Object>> pageData = new Page<Map<String, Object>>();
		pageData.setDataList(dubboRulesService.findByPage(1, 10, appName, applicationName, getCreatorId()));
		pageData.setPageSize(10);
		pageData.setPageNO(1);
		pageData.setTotal(dubboRulesService.findCount(appName, applicationName, getCreatorId()));
		model.addAttribute("appName", appName);
		model.addAttribute("applicationName", applicationName);
		model.addAttribute("pageData", pageData);
		return "rule/dubboRuleList";
	}

	@ResponseBody
	@RequestMapping(value = "/enableOrDisable")
	public ResponseDTO enableOrDisableDubboRule(String ids, int status) {
		ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<Map<String, Object>>(ReturnCode.ACTIVE_EXCEPTION);
		if (StringUtils.isBlank(ids)) {
			responseDTO.setMsg("参数不能为空");
			return responseDTO;
		}
		try {
			dubboRulesService.enableOrDisableUrlRule(ids, status);
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDTO;
	}

	@RequestMapping(value = "/modify/{id}", method = RequestMethod.GET)
	public String modifyRule(@PathVariable("id") Long id, Model model) {
		List<App> appNameList = appService.findByPage(1, 1000, null, getCreatorId());
		List<Contact> contactsList = contactsService.getAllContactsInfo();
		List<Strategy> strategyList = strategyService.selectAll();

		List<RuleReceiversKey> ruleReceiversKeys = ruleReceiverService.findReceiversByRule(id, RuleReceiversKey.Type.Dubbo.val());
		List<ZookeeperIps> zookeeperIpses = zookeeperIpsService.findByPage(1, 100);
		DubboRules dubboRules = dubboRulesService.findDubboRulesById(id);
		String contactNames = "";
		String contractIds = "";
		String notifyMode = "";
		for (RuleReceiversKey ruleReceiversKey : ruleReceiversKeys) {
			for (Contact contact : contactsList) {
				if (ruleReceiversKey.getContactId() == contact.getId() && ruleReceiversKey.getType() == RuleReceiversKey.Type.Dubbo.val()) {
					if (contactNames.length() > 0)
						contactNames += ";";
					if (contractIds.length() > 0)
						contractIds += ";";
					contactNames += contact.getName();
					contractIds += ruleReceiversKey.getContactId();
					break;
				}
			}
			notifyMode = ruleReceiversKey.getNotifyMode();
		}
		model.addAttribute("dubboRules", dubboRules);
		model.addAttribute("zookeeperIpses", zookeeperIpses);
		model.addAttribute("notifyMode", notifyMode);
		model.addAttribute("contactNames", contactNames);
		model.addAttribute("contractIds", contractIds);
		model.addAttribute("appName", appNameList);
		model.addAttribute("contactsList", contactsList);
		model.addAttribute("strategyList", strategyList);
		model.addAttribute("dubboRuleId", id);

		return "rule/addDubboRule";
	}

	@ResponseBody
	@RequestMapping(value = "/queryModify")
	public ResponseDTO<DubboRules> queryDubboRuleById(HttpServletRequest request) {
		ResponseDTO<DubboRules> responseDTO = new ResponseDTO<DubboRules>(ReturnCode.ACTIVE_EXCEPTION);
		try {
			long id = Long.valueOf(request.getParameter("id"));
			DubboRules dubboRules = dubboRulesService.findDubboRulesById(id);
			responseDTO.setAttach(dubboRules);
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseDTO;
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addRule(Model model) {
		List<App> appNameList = appService.findByPage(1, 1000, null, getCreatorId());
		List<Contact> contactsList = contactsService.getAllContactsInfo();
		List<Strategy> strategyList = strategyService.selectAll();
		List<ZookeeperIps> zookeeperIpses = zookeeperIpsService.findByPage(1, 100);
		model.addAttribute("zookeeperIpses", zookeeperIpses);
		model.addAttribute("appName", appNameList);
		model.addAttribute("contactsList", contactsList);
		model.addAttribute("strategyList", strategyList);
		return "rule/addDubboRule";
	}

	@ResponseBody
	@RequestMapping(value = "/addData")
	public ResponseDTO<DubboRules> addData(@ModelAttribute DubboRules dubboRules, String contact, String contactType) {
		ResponseDTO<DubboRules> responseDTO = new ResponseDTO<DubboRules>(ReturnCode.ACTIVE_EXCEPTION);
		responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		// if (StringUtils.isBlank(contact) || StringUtils.isBlank(contactType) || StringUtils.isBlank(contact)) {
		// responseDTO.setMsg("参数不能为空");
		// return responseDTO;
		// }
		try {
			App app = appService.findAppById(dubboRules.getAppId());
			dubboRules.setIsEnabled((byte) UrlRules.IsEnabled.NoneEnabled.val());
			dubboRulesService.addOrUpdateDubboRules(dubboRules, contact, contactType, false);
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/update/{dubboRuleId}")
	public ResponseDTO<DubboRules> modify(@PathVariable("dubboRuleId") long dubboRuleId, DubboRules dubboRules, String contact, String contactType) {
		ResponseDTO<DubboRules> responseDTO = new ResponseDTO<DubboRules>(ReturnCode.ACTIVE_EXCEPTION);
		try {
			dubboRules.setId(dubboRuleId);
			dubboRulesService.addOrUpdateDubboRules(dubboRules, contact, contactType, true);
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception ex) {
			ex.printStackTrace();
			responseDTO.setMsg(ex.getMessage());
		}
		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/del")
	public ResponseDTO<DubboRules> delZookeeperIps(String ids) {
		ResponseDTO<DubboRules> responseDTO = new ResponseDTO<DubboRules>(ReturnCode.ACTIVE_EXCEPTION);
		if (StringUtils.isBlank(ids)) {
			responseDTO.setMsg("参数不能为空");
			return responseDTO;
		}
		try {
			dubboRulesService.delDubboRules(ids);
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception ex) {
			responseDTO.setMsg(ex.getMessage());
		}
		return responseDTO;
	}

	@Override
	protected UserService getUserService() {
		return userService;
	}
}
