package com.feng.sauron.warning.web;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.feng.sauron.warning.service.base.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.feng.sauron.warning.common.Page;
import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.domain.Strategy;
import com.feng.sauron.warning.domain.StrategyTem;
import com.feng.sauron.warning.service.AlarmService;
import com.feng.sauron.warning.service.StrategyCheckService;
import com.feng.sauron.warning.service.StrategyTemService;
import com.feng.sauron.warning.service.base.RulesService;
import com.feng.sauron.warning.service.base.StrategyService;
import com.feng.sauron.warning.util.DateUtils;
import com.feng.sauron.warning.web.base.BaseController;
import com.fengjr.upm.filter.annotation.RequiresPermissions;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年12月18日 上午10:30:51
 * 
 */

@Controller
@RequestMapping("/strategy")
public class StrategyController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(StrategyController.class);
	@Autowired
	RulesService rulesService;
	@Autowired
	StrategyService service;
	@Resource
	StrategyCheckService strategyCheckService;

	@Resource
	StrategyTemService strategyTemService;

	@ResponseBody
	@RequestMapping(value = "/checkMobile", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseDTO<String> checkMobile(String template, String strategyId, @RequestParam String phone) throws NoSuchRequestHandlingMethodException {

		ResponseDTO<String> responseDTO = new ResponseDTO<>();
		responseDTO.setCode(0);
		responseDTO.setMsg("strategy limited");

		if (StringUtils.isBlank(phone)) {
			return responseDTO;
		}

		if (StringUtils.isBlank(template)) {
			responseDTO.setCode(-1);
			responseDTO.setMsg("template no exist");
			return responseDTO;
		}

		StrategyTem strategyTem = strategyTemService.selectBytemplate(template);

		if (strategyTem == null) {
			responseDTO.setCode(-1);
			responseDTO.setMsg("template no exist");
			return responseDTO;
		}

		Long strategy_Id = null;
		try {
			if (StringUtils.isNumeric(strategyId)) {
				strategy_Id = Long.parseLong(strategyId);
			}
		} catch (Exception e) {
		}

		boolean test = false;
		if (strategy_Id == null || strategyCheckService.getStrategyById(strategy_Id) == null) {
			logger.info(" strategyId:{} is not exist ", strategy_Id);
			strategy_Id = strategyTem.getStrategyId();
			logger.info(" template:{} , use template of strategyId :{}: ", template, strategy_Id);
		}

		test = strategyCheckService.check(template, phone, strategy_Id);
		String format = DateUtils.format(new Date());
		if (test) {
			logger.debug(format + "     -      " + test);
			if (strategyTem != null) {
				responseDTO.setAttach(strategyTem.getChannelId());
			}

			responseDTO.setCode(1);
			responseDTO.setMsg("success");
		} else {
			if (strategyTem != null) {
				responseDTO.setAttach(strategyTem.getChannelId());
			}
			logger.debug(format + " - " + test);
		}
		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/checkMobile_old", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseDTO<String> checkMobile_old(String template, String strategyId, @RequestParam String phone) throws NoSuchRequestHandlingMethodException {

		ResponseDTO<String> responseDTO = new ResponseDTO<>();
		responseDTO.setCode(0);
		responseDTO.setMsg("strategy limited");

		if (StringUtils.isBlank(phone)) {
			return responseDTO;
		}
		Long strategy_Id = null;
		try {
			if (StringUtils.isNumeric(strategyId)) {
				strategy_Id = Long.parseLong(strategyId);
			}
		} catch (Exception e) {
		}

		boolean test = false;
		StrategyTem strategyTem = null;
		if (strategy_Id != null) {
			if (StringUtils.isBlank(template)) {

				template = AlarmService.SMS_TYPE;
				logger.info("template isBlank , use default template: " + AlarmService.SMS_TYPE);
			} else {
				strategyTem = strategyTemService.selectBytemplate(template);
				if (strategyTem == null) {
					logger.info("template:{} and strategyId:{} is not matching , use default template :template_no: ", template, strategy_Id);
					template = AlarmService.SMS_TYPE;
					strategyTem = strategyTemService.selectBytemplate(template);// 如果template 和 strategyId 都传了 但是不匹配 则依strategyId为准 ，且使用默认模板
				}
			}
			test = strategyCheckService.check(template, phone, strategy_Id);
		} else if (StringUtils.isNotBlank(template)) {

			strategyTem = strategyTemService.selectBytemplate(template);

			if (strategyTem != null) {
				strategy_Id = strategyTem.getStrategyId();
			} else {
				logger.info("template:{} is no exist  , use default template = {}", template, AlarmService.SMS_TYPE);
				template = AlarmService.SMS_TYPE;
				strategyTem = strategyTemService.selectBytemplate(template);// 如果template 和 strategyId 都传了 但是不匹配 就使用默认模板
				strategy_Id = strategyTem.getStrategyId();
			}
			test = strategyCheckService.check(template, phone, strategy_Id);
		} else {
			return responseDTO;
		}

		String format = DateUtils.format(new Date());
		if (test) {
			logger.debug(format + "     -      " + test);
			if (strategyTem != null) {
				responseDTO.setAttach(strategyTem.getChannelId());
			}

			responseDTO.setCode(1);
			responseDTO.setMsg("success");
		} else {
			logger.debug(format + " - " + test);
		}
		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/checkMobile1_old", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseDTO<String> checkMobile1(String template, String strategyId, @RequestParam String phone) throws NoSuchRequestHandlingMethodException {

		ResponseDTO<String> responseDTO = new ResponseDTO<>();
		responseDTO.setCode(0);
		responseDTO.setMsg("strategy limited");

		if (StringUtils.isBlank(phone) || !StringUtils.isNumeric(strategyId)) {
			return responseDTO;
		}
		Long strategy_Id = null;
		try {
			if (StringUtils.isNumeric(strategyId)) {
				strategy_Id = Long.parseLong(strategyId);
			}
		} catch (Exception e) {
		}

		ResponseDTO<Object> check2 = new ResponseDTO<>();
		StrategyTem strategyTem = null;
		if (strategy_Id != null) {
			if (StringUtils.isBlank(template)) {

				Strategy strategyById = strategyCheckService.getStrategyById(strategy_Id);
				if (strategyById == null) {
					return responseDTO;
				}

				template = AlarmService.SMS_TYPE;
				logger.info("template isBlank , use default template: " + AlarmService.SMS_TYPE);
			} else {
				strategyTem = strategyTemService.selectBytemplate(template);
				if (strategyTem == null) {

					Strategy strategyById = strategyCheckService.getStrategyById(strategy_Id);
					if (strategyById == null) {
						logger.info("template: is no exist  and strategyId:is no exist , return ");
						return responseDTO; // 如果 template 传个错误的 ，且 strategyId 也是错误的 则返回失败
					}

					logger.info("template:{} and strategyId:{} is not matching , use default template :template_no: ", template, strategy_Id);
					template = AlarmService.SMS_TYPE;
					strategyTem = strategyTemService.selectBytemplate(template);// 如果template 和 strategyId 都传了 但是不匹配 就使用默认模板
				} else {
					Strategy strategyById = strategyCheckService.getStrategyById(strategy_Id);
					if (strategyById == null) {
						logger.info(" strategyId:{} is not exist ", strategy_Id);
						strategy_Id = strategyTem.getStrategyId();
						logger.info(" template:{} , use template of strategyId :{}: ", template, strategy_Id);
					}
				}
			}
			check2 = strategyCheckService.check2(template, phone, strategy_Id);
		} else if (StringUtils.isNotBlank(template)) {

			strategyTem = strategyTemService.selectBytemplate(template);

			if (strategyTem != null) {
				strategy_Id = strategyTem.getStrategyId();
			} else {
				logger.info("template:{} is no exist and strategyid is null , return ", template);
				return responseDTO;
			}
			check2 = strategyCheckService.check2(template, phone, strategy_Id);
		} else {
			return responseDTO;
		}

		if (check2.getCode() == ReturnCode.ACTIVE_SUCCESS.code()) {
			if (strategyTem != null) {
				responseDTO.setAttach(strategyTem.getChannelId());
			}
			responseDTO.setCode(1);
		}
		responseDTO.setMsg(check2.getMsg());

		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/checkMobile_old1", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseDTO<String> checkMobile_old1(String template, String strategyId, @RequestParam String phone) throws NoSuchRequestHandlingMethodException {

		ResponseDTO<String> responseDTO = new ResponseDTO<>();
		responseDTO.setCode(0);
		responseDTO.setMsg("strategy limited");

		if (StringUtils.isBlank(phone) || !StringUtils.isNumeric(strategyId)) {
			return responseDTO;
		}
		Long strategy_Id = null;
		try {
			if (StringUtils.isNumeric(strategyId)) {
				strategy_Id = Long.parseLong(strategyId);
			}
		} catch (Exception e) {
		}

		ResponseDTO<Object> check2 = new ResponseDTO<>();
		StrategyTem strategyTem = null;
		if (strategy_Id != null) {
			if (StringUtils.isBlank(template)) {
				template = AlarmService.SMS_TYPE;
				logger.info("template isBlank , use default template: " + AlarmService.SMS_TYPE);
			} else {
				strategyTem = strategyTemService.selectBytemplate(template);
				if (strategyTem == null) {
					logger.info("template:{} and strategyId:{} is not matching , use default template :template_no: ", template, strategy_Id);
					template = AlarmService.SMS_TYPE;
					strategyTem = strategyTemService.selectBytemplate(template);// 如果template 和 strategyId 都传了 但是不匹配 就使用默认模板
				}
			}
			check2 = strategyCheckService.check2(template, phone, strategy_Id);
		} else if (StringUtils.isNotBlank(template)) {

			strategyTem = strategyTemService.selectBytemplate(template);

			if (strategyTem != null) {
				strategy_Id = strategyTem.getStrategyId();
			} else {
				logger.info("template:{} is no exist  , use default template = {}", template, AlarmService.SMS_TYPE);
				template = AlarmService.SMS_TYPE;
				strategyTem = strategyTemService.selectBytemplate(template);// 如果template 和 strategyId 都传了 但是不匹配 就使用默认模板
				strategy_Id = strategyTem.getStrategyId();
			}
			check2 = strategyCheckService.check2(template, phone, strategy_Id);
		} else {
			return responseDTO;
		}

		if (check2.getCode() == ReturnCode.ACTIVE_SUCCESS.code()) {
			if (strategyTem != null) {
				responseDTO.setAttach(strategyTem.getChannelId());
			}
			responseDTO.setCode(1);
		}
		responseDTO.setMsg(check2.getMsg());

		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/removeAllStrategyCache", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseDTO<Boolean> removeAllStrategyCache() throws NoSuchRequestHandlingMethodException {

		ResponseDTO<Boolean> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_SUCCESS);
		StrategyCheckService.removeAllStrategyCache();
		StrategyCheckService.watch();
		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/removeAllStrategyTemCache", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseDTO<Boolean> removeAllStrategyTemCache() throws NoSuchRequestHandlingMethodException {

		ResponseDTO<Boolean> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_SUCCESS);
		StrategyTemService.removeAllStrategyTemCache();
		StrategyTemService.watch();
		return responseDTO;
	}

	@RequestMapping(value = "/list/{pageNo}/{pageSize}")
	public String listStrategy(HttpServletRequest request,@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize, Model model) {

		Page<Strategy> pageData = new Page<Strategy>();
		String _id = request.getParameter("id");
		if (StringUtils.isEmpty(_id)) {
			_id = "0";
		}
		long id = Long.parseLong(_id);
		String stgyName = request.getParameter("stgyName");
		pageData.setDataList(service.findByPager(pageNo, pageSize,id, stgyName));
		pageData.setPageSize(pageSize);
		pageData.setPageNO(pageNo);
		pageData.setTotal(service.selectCount(id, stgyName));
		model.addAttribute("pageData", pageData);
		return "strategy/strategyListNew";
	}

	@RequestMapping(value = "/query")
	public String queryStrategy(HttpServletRequest request, Model model) {

		String _id = request.getParameter("id");
		if (StringUtils.isEmpty(_id)) {
			_id = "0";
		}
		long id = Long.parseLong(_id);
		String stgyName = request.getParameter("stgyName");
		Page<Strategy> pageData = new Page<Strategy>();
		pageData.setDataList(service.findByPager(1, 10, id, stgyName));
		pageData.setPageSize(10);
		pageData.setPageNO(1);
		pageData.setTotal(service.selectCount(id, stgyName));
		model.addAttribute("pageData", pageData);
		return "strategy/strategyListNew";
	}

	@ResponseBody
	@RequestMapping(value = "/add")
	public ResponseDTO<Strategy> addStrategy(String stgyName, int minInterval, int warnCountWithTime, int warnMaxCount, long bakNum, String bakChar, String warnDaysInWeek, String startTimeInDay, String endTimeInDay) {
		ResponseDTO<Strategy> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
		Strategy strategy = new Strategy();
		strategy.setStgyName(stgyName);
		strategy.setMinInterval(minInterval);
		strategy.setWarnCountWithTime(warnCountWithTime);
		strategy.setWarnMaxCount(warnMaxCount);
		strategy.setWarnDaysInWeek(warnDaysInWeek);
		strategy.setStartTimeInDay(startTimeInDay);
		strategy.setEndTimeInDay(endTimeInDay);
		strategy.setStatus(0);
		strategy.setCreateTime(new Date());
		strategy.setCreatorId(1L);
		strategy.setBakNum(bakNum);
		strategy.setBakChar(bakChar);

		try {
			service.addNewStrategy(strategy);
			StrategyCheckService.removeAllStrategyCache();
			StrategyCheckService.watch();
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception ex) {
		}
		return responseDTO;

	}

	@ResponseBody
	@RequiresPermissions("strategy:del")
	@RequestMapping(value = "/del/{id}")
	public ResponseDTO<Strategy> delStrategy(@PathVariable("id") long id, byte type) {
		ResponseDTO<Strategy> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
		try {
			if (rulesService.findByStrategyId(id,type).size() != 0) {
				throw new IllegalStateException("当前策略被至少一条规则关联，无法删除");
			}
			service.removceStrategyById(id);
			StrategyCheckService.removeAllStrategyCache();
			StrategyCheckService.watch();
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (IllegalStateException ex) {
			responseDTO.setReturnCode(ReturnCode.ERROR_PERMISSION_DENIED);
			responseDTO.setMsg(ex.getMessage());
		} catch (Exception ex) {
			responseDTO.setMsg(ex.getMessage());
		}
		return responseDTO;
	}

	@ResponseBody
	@RequestMapping(value = "/modify/{id}", method = RequestMethod.GET)
	public ResponseDTO<Strategy> modifyStrategy(@PathVariable("id") long id) {
		ResponseDTO<Strategy> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
		try {
			responseDTO.setAttach(service.findById(id));
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
		} catch (Exception ex) {
			responseDTO.setMsg(ex.getMessage());
		}
		return responseDTO;
	}

	@ResponseBody
	@RequiresPermissions("strategy:modify")
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public ResponseDTO<Strategy> modifyStrategy(long id, String stgyName, int minInterval, int warnCountWithTime, int warnMaxCount, long bakNum, String bakChar, String warnDaysInWeek, String startTimeInDay, String endTimeInDay, int status) {
		ResponseDTO<Strategy> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
		try {
			Strategy strategy = new Strategy();
			strategy.setId(id);
			strategy.setStgyName(stgyName);
			strategy.setMinInterval(minInterval);
			strategy.setWarnCountWithTime(warnCountWithTime);
			strategy.setWarnMaxCount(warnMaxCount);
			strategy.setWarnDaysInWeek(warnDaysInWeek);
			strategy.setStartTimeInDay(startTimeInDay);
			strategy.setEndTimeInDay(endTimeInDay);
			strategy.setStatus(0);
			strategy.setCreateTime(new Date());
			strategy.setCreatorId(1L);
			strategy.setBakNum(bakNum);
			strategy.setBakChar(bakChar);
			service.updateStrategy(strategy);
			StrategyCheckService.removeAllStrategyCache();
			StrategyCheckService.watch();
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
