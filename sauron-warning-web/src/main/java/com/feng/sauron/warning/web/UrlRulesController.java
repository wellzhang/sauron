package com.feng.sauron.warning.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.feng.sauron.warning.service.base.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.feng.sauron.warning.common.Page;
import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.domain.App;
import com.feng.sauron.warning.domain.Contact;
import com.feng.sauron.warning.domain.RuleReceiversKey;
import com.feng.sauron.warning.domain.Strategy;
import com.feng.sauron.warning.domain.UrlRules;
import com.feng.sauron.warning.service.HttpMonitorService;
import com.feng.sauron.warning.web.base.BaseController;
import com.fengjr.upm.filter.annotation.RequiresPermissions;

/**
 * UrlRulesController Created by jianzhang
 */
@Controller
@RequestMapping("/urlRule")
public class UrlRulesController extends BaseController {

	@Autowired
	private AppService appService;

	@Autowired
	private UrlRulesService urlRulesService;

	@Autowired
	ContactsService contactsService;

	@Autowired
	StrategyService strategyService;

	@Autowired
	private HttpMonitorService httpMonitorService;

	@Autowired
	private RuleReceiverService ruleReceiverService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/list/{pageNo}/{pageSize}")
	public String list(@PathVariable(value = "pageNo") int pageNo, @PathVariable(value = "pageSize") int pageSize, String appName, String monitorKey, Model model) {
		Page<UrlRules> pageData = new Page<UrlRules>();
		String creatorId = getCreatorId();
		pageData.setDataList(urlRulesService.findByPage(pageNo, pageSize, appName, monitorKey, creatorId));
		pageData.setPageSize(pageSize);
		pageData.setPageNO(pageNo);
		pageData.setTotal(urlRulesService.findCount(appName, monitorKey, creatorId));
		model.addAttribute("pageData", pageData);
		model.addAttribute("appName", appName);
		model.addAttribute("monitorKey", monitorKey);
		return "rule/urlRuleList";
	}

	@RequestMapping(value = "/query")
	public String queryRule(HttpServletRequest request, Model model) {
		String appName = request.getParameter("appName");
		String monitorKey = request.getParameter("monitorKey");
		String creatorId = getCreatorId();
		Page<UrlRules> pageData = new Page<UrlRules>();
		pageData.setDataList(urlRulesService.findByPage(1, 10, appName, monitorKey, creatorId));
		pageData.setPageSize(10);
		pageData.setPageNO(1);
		pageData.setTotal(urlRulesService.findCount(appName, monitorKey, creatorId));
		model.addAttribute("appName", appName);
		model.addAttribute("monitorKey", monitorKey);
		model.addAttribute("pageData", pageData);
		return "rule/urlRuleList";
	}

	@ResponseBody
	@RequestMapping(value = "/enableOrDisable")
	public ResponseDTO<UrlRules> enableOrDisableUrlRule(String ids, int status) {
		ResponseDTO<UrlRules> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
		if (StringUtils.isBlank(ids)) {
			responseDTO.setMsg("参数不能为空");
			return responseDTO;
		}
		httpMonitorService.enableOrDisableUrlRule(ids, status);
		responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		return responseDTO;
	}

	@RequiresPermissions("rule:modify")
	@RequestMapping(value = "/modify/{id}", method = RequestMethod.GET)
	public String modifyRule(@PathVariable("id") Long id, Model model) {
		List<App> appNameList = appService.findByPage(1, 1000, null, getCreatorId());
		List<Contact> contactsList = contactsService.getAllContactsInfo();
		List<Strategy> strategyList = strategyService.selectAll();
		UrlRules urlRules = urlRulesService.findUrlRulesById(id);

		List<RuleReceiversKey> ruleReceiversKeys = ruleReceiverService.findReceiversByRule(id, RuleReceiversKey.Type.Url.val());
		String contactNames = "";
		String contractIds = "";
		String notifyMode = "";
		for (RuleReceiversKey ruleReceiversKey : ruleReceiversKeys) {
			for (Contact contact : contactsList) {
				if (ruleReceiversKey.getContactId() == contact.getId() && ruleReceiversKey.getType() == RuleReceiversKey.Type.Url.val()) {
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
		model.addAttribute("notifyMode", notifyMode);
		model.addAttribute("contactNames", contactNames);
		model.addAttribute("contractIds", contractIds);
		model.addAttribute("appName", appNameList);
		model.addAttribute("contactsList", contactsList);
		model.addAttribute("strategyList", strategyList);
		model.addAttribute("urlRuleId", id);
		model.addAttribute("urlRules", urlRules);

		return "rule/addUrlRule";
	}

	@ResponseBody
	@RequestMapping(value = "/queryModify")
	public ResponseDTO<UrlRules> queryUrlRuleById(HttpServletRequest request) {
		ResponseDTO<UrlRules> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
		try {
			long id = Long.valueOf(request.getParameter("id"));
			UrlRules urlRules = urlRulesService.findUrlRulesById(id);
			responseDTO.setAttach(urlRules);
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
		model.addAttribute("appName", appNameList);
		model.addAttribute("contactsList", contactsList);
		model.addAttribute("strategyList", strategyList);
		return "rule/addUrlRule";
	}

	@ResponseBody
	@RequestMapping(value = "/addData")
	public ResponseDTO<UrlRules> addData(@ModelAttribute UrlRules urlRules, String contact, String contactType) {
		ResponseDTO<UrlRules> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
		responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		// if (StringUtils.isBlank(contact) || StringUtils.isBlank(contactType) || StringUtils.isBlank(contact)) {
		// responseDTO.setMsg("参数不能为空");
		// return responseDTO;
		// }
		try {
			App app = appService.findAppById(urlRules.getAppId());
			urlRules.setAppName(app.getName());
			urlRules.setIsEnabled((byte) UrlRules.IsEnabled.NoneEnabled.val());
			httpMonitorService.addOrUpdateUrlRules(urlRules, contact, contactType, false);
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/update/{urlRuleId}")
	public ResponseDTO<UrlRules> modifyZookeeperIps(@PathVariable("urlRuleId") long urlRuleId, UrlRules urlRules, String contact, String contactType) {
		ResponseDTO<UrlRules> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
		try {
			App app = appService.findAppById(urlRules.getAppId());
			byte isConfigHost = urlRules.getIsConfigHost();
			if (isConfigHost == UrlRules.IsConfigHost.NoneConfigHost.val()) {
				urlRules.setHostIp("");
			}
			byte isContain = urlRules.getIsContain();
			if (isContain == UrlRules.IsContain.NoneContain.val()) {
				urlRules.setMatchContent("");
			}
			byte isDefaultCode = urlRules.getIsDefaultCode();
			if (isDefaultCode == UrlRules.IsDefaultCode.DefaultCode.val()) {
				urlRules.setCustomCode(null);
			}
			urlRules.setAppName(app.getName());
			urlRules.setId(urlRuleId);
			httpMonitorService.addOrUpdateUrlRules(urlRules, contact, contactType, true);
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception ex) {
			ex.printStackTrace();
			responseDTO.setMsg(ex.getMessage());
		}
		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/del")
	public ResponseDTO<UrlRules> delZookeeperIps(String ids) {
		ResponseDTO<UrlRules> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
		if (StringUtils.isBlank(ids)) {
			responseDTO.setMsg("参数不能为空");
			return responseDTO;
		}
		try {
			httpMonitorService.delUrlRules(ids);
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception ex) {
			responseDTO.setMsg(ex.getMessage());
		}
		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/check")
	public String check(String ids) {
		return "通过";
	}

	@Override
	protected UserService getUserService() {
		return userService;
	}
}
