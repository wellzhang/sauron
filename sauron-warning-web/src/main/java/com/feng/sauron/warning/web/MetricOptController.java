package com.feng.sauron.warning.web;

import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.domain.MetricOpt;
import com.feng.sauron.warning.service.MetricOptInfoService;
import com.feng.sauron.warning.service.base.UserService;
import com.feng.sauron.warning.web.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年12月18日 上午10:30:29
 * 
 */ 

@Controller
@RequestMapping("/metric_opt")
public class MetricOptController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(MetricOptController.class);

	@Resource
	private MetricOptInfoService metricOptInfoService;

	@ResponseBody
	@RequestMapping(value = "/getMetrics", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseDTO<List<MetricOpt>> getMetrics() throws NoSuchRequestHandlingMethodException {

		ResponseDTO<List<MetricOpt>> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_FAILURE);
		try {

			ResponseDTO<List<MetricOpt>> selectAllMetricsOpt = metricOptInfoService.selectAllMetricsOpt(Integer.parseInt("0"));

			if (selectAllMetricsOpt.getCode() == ReturnCode.ACTIVE_SUCCESS.code()) {
				responseDTO = selectAllMetricsOpt;
			}
		} catch (Exception e) {
			responseDTO.setReturnCode(ReturnCode.ACTIVE_EXCEPTION);
			responseDTO.setMsg(e.getMessage());
		}
		return responseDTO;
	}

	@Override
	protected UserService getUserService() {
		return null;
	}
}
