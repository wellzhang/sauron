package com.feng.sauron.warning.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.feng.sauron.warning.service.base.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.feng.sauron.warning.common.Page;
import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.domain.Strategy;
import com.feng.sauron.warning.domain.StrategyTem;
import com.feng.sauron.warning.service.StrategyTemService;
import com.feng.sauron.warning.service.base.StrategyService;
import com.feng.sauron.warning.web.base.BaseController;
import com.fengjr.upm.filter.annotation.RequiresPermissions;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年12月18日 上午10:30:51
 */

@Controller
@RequestMapping("/strategyTem")
public class StrategyTemController extends BaseController {

	@Autowired
	StrategyTemService service;
	@Autowired
	StrategyService strategyService;

	@RequestMapping(value = "/list/{pageNo}/{pageSize}")
	public String listStrategy(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize, HttpServletRequest request, Model model) {
		String _strategyId = request.getParameter("strategyId");
		if (StringUtils.isEmpty(_strategyId)) {
			_strategyId = "0";
		}
		long strategyId = Long.parseLong(_strategyId);
		String template = request.getParameter("template");
		if (StringUtils.isEmpty(template)) {
			template = "";
		}

		String channelId = request.getParameter("channelId");
		if (StringUtils.isEmpty(channelId)) {
			channelId = "";
		}
		List<Strategy> strategyList = strategyService.selectAll();
		Page<StrategyTem> pageData = new Page<StrategyTem>();
		pageData.setDataList(service.findByPager(pageNo, pageSize, template, strategyId, channelId));
		pageData.setPageSize(pageSize);
		pageData.setPageNO(pageNo);
		pageData.setTotal(service.selectCount(template, strategyId, channelId));
		model.addAttribute("pageData", pageData);
		model.addAttribute("strategyList", strategyList);
		return "strategyTem/strategyTemListNew";
	}

	@ResponseBody
	@RequestMapping(value = "/add")
	public ResponseDTO<StrategyTem> addStrategy(String template, long strategyId, String content, String channelId, int status) {
		ResponseDTO<StrategyTem> responseDTO = new ResponseDTO<>();
		StrategyTem strategyTem = new StrategyTem();
		strategyTem.setTemplate(template);
		strategyTem.setStrategyId(strategyId);
		strategyTem.setContent(content);
		strategyTem.setChannelId(channelId);
		strategyTem.setStatus(status);

		try {
			service.addNewStrategyTem(strategyTem);
			StrategyTemService.removeAllStrategyTemCache();
			StrategyTemService.watch();
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception ex) {
			responseDTO.setReturnCode(ReturnCode.ACTIVE_EXCEPTION);
		}
		return responseDTO;

	}

	@ResponseBody
	@RequiresPermissions("strategyTem:del")
	@RequestMapping(value = "/del")
	public ResponseDTO<StrategyTem> delStrategy(String template) {
		ResponseDTO<StrategyTem> responseDTO = new ResponseDTO<>();
		try {
			service.removeByTemplate(template);
			StrategyTemService.removeAllStrategyTemCache();
			StrategyTemService.watch();
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (IllegalStateException ex) {
			responseDTO.setReturnCode(ReturnCode.ERROR_PERMISSION_DENIED);
			responseDTO.setMsg(ex.getMessage());
		} catch (Exception ex) {
			responseDTO.setReturnCode(ReturnCode.ACTIVE_EXCEPTION);
			responseDTO.setMsg(ex.getMessage());
		}
		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/modify", method = RequestMethod.GET)
	public ResponseDTO<StrategyTem> modifyStrategy(String template) {
		ResponseDTO<StrategyTem> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
		try {
			responseDTO.setAttach(service.findByTemplate(template));
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception ex) {
			responseDTO.setMsg(ex.getMessage());
		}
		return responseDTO;
	}

	@ResponseBody
	@RequiresPermissions("strategyTem:modify")
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public ResponseDTO<StrategyTem> modifyStrategy(String template, long strategyId, String content, String channelId, int status) {
		ResponseDTO<StrategyTem> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
		try {
			StrategyTem strategyTem = new StrategyTem();
			strategyTem.setTemplate(template);
			strategyTem.setStrategyId(strategyId);
			strategyTem.setContent(content);
			strategyTem.setChannelId(channelId);
			strategyTem.setStatus(status);
			service.updateStrategyTem(strategyTem);
			StrategyTemService.removeAllStrategyTemCache();
			StrategyTemService.watch();
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception ex) {
			responseDTO.setMsg(ex.getMessage());
		}
		return responseDTO;
	}

	@Override
	protected UserService getUserService() {
		return null;
	}
}
