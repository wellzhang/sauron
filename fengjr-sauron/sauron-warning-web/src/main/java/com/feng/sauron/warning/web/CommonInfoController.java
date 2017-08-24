package com.feng.sauron.warning.web;

import com.feng.sauron.warning.service.base.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.feng.sauron.warning.web.base.BaseController;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年12月18日 上午10:30:51
 * 
 */

@Controller
@RequestMapping("/common")
public class CommonInfoController extends BaseController {

	@ResponseBody
	@RequestMapping(value = "/test", method = { RequestMethod.GET, RequestMethod.POST })
	public String test() {
		return "success";
	}

	@Override
	protected UserService getUserService() {
		return null;
	}
}
