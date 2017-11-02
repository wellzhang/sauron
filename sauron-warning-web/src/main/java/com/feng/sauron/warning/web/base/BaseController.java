package com.feng.sauron.warning.web.base;

import com.feng.sauron.warning.service.base.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fengjr.upm.filter.util.UserUtils;
import com.fengjr.upm.tools.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class BaseController {

	protected final static String DATA_FORMAT_JSON = "json";

	/**
	 * 获取客户端ip地址<br/>
	 * eg.remoteAddr有可能为ngnix的ip, x-forwarded-for为ngnix转发的客户端真实ip地址;<br/>
	 *
	 * @param request
	 */
	public static String getRemoteIP(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		builder.append(request.getRemoteAddr());
		String forwardedIp = request.getHeader("x-forwarded-for");
		if (null != forwardedIp && !"unknown".equalsIgnoreCase(forwardedIp)) {
			builder.append(",").append(forwardedIp);
		}
		builder.trimToSize();
		return builder.toString();
	}

	/**
	 * @param model
	 * @param statusText
	 */
	protected void failedView(Model model, String statusText) {
		model.addAttribute(ResponseObject.STATUS, ResponseObject.FAIL);
		if (StringUtils.isNotBlank(statusText))
			model.addAttribute(ResponseObject.STATUS_TEXT, statusText);
	}

	/**
	 * @param model
	 */
	protected void successView(Model model) {
		this.successView(model, null);
	}

	protected void successView(Model model, String statusText) {
		model.addAttribute(ResponseObject.STATUS, ResponseObject.SUCCESS);
		if (StringUtils.isNotBlank(statusText))
			model.addAttribute(ResponseObject.STATUS_TEXT, statusText);
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	protected ModelAndView viewNegotiating(final HttpServletRequest request, final HttpServletResponse response, final Model model) {

		ModelAndView modelAndView = jsonView(request, response, model);
		return modelAndView;
	}

	/**
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	protected ModelAndView jsonView(final HttpServletRequest request, final HttpServletResponse response, final Model model) {
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		ModelAndView modelAndView = new ModelAndView(view, model.asMap());
		return modelAndView;
	}

	protected String getExceptionString(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw, true));
		return sw.toString();
	}

	protected void processException(Model model, Exception e, String msg, Logger logger) {
		e.printStackTrace();
		logger.info(msg + " {} " + getExceptionString(e));
		failedView(model, msg + " " + e.toString());
	}

	public static User getLoginUser() {
		return UserUtils.getUser();
	}


	public String getCreatorId(){
		String creatorId = null;
		if(UserUtils.getUser() != null){
			creatorId = UserUtils.getUser().getId();
			com.feng.sauron.warning.domain.User user = new com.feng.sauron.warning.domain.User();
			user.setName(UserUtils.getUser().getName());
			user.setUserId(creatorId);
			try {
				getUserService().addUser(user);
			}catch (Exception e){
				e.printStackTrace();
			}

//			System.out.println("creatorId:"+creatorId);
			//超级用户Id
			if(
//					"24526bf2-d900-4cdb-8e0b-e8fdd1a8fba3".equals(creatorId) || "44bdd8be-d429-41a7-a185-98d70aa92aaa".equals(creatorId)
					UserUtils.hasPermission("admin")
					){
				return null;
			}
		}
		return creatorId;
	}

	protected abstract UserService getUserService();

	public static void main(String[] args) {
		UserUtils.clean();
	}
}
