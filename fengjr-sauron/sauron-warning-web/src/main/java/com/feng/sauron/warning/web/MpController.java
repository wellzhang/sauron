package com.feng.sauron.warning.web;

import com.feng.sauron.warning.common.Page;
import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.domain.*;
import com.feng.sauron.warning.service.base.*;
import com.feng.sauron.warning.util.WatchableConfigClient;
import com.feng.sauron.warning.web.base.BaseController;
import com.feng.sauron.warning.web.vo.MpVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * UrlRulesController Created by jianzhang
 */
@Controller
@RequestMapping("/mp")
public class MpController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(MpController.class);

	@Resource
	private AppService appService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/list/{pageNo}/{pageSize}")
	public String list(@PathVariable(value = "pageNo") int pageNo, @PathVariable(value = "pageSize") int pageSize,
					   String methodName,String appName,Model model) {
		List<App> appNameList = appService.findByPage(1,1000,null,getCreatorId());
		model.addAttribute("appNameList", appNameList);
		if(StringUtils.isBlank(appName)){
			return "mp/mpList";
		}
		Page<MpVo> pageData = new Page<MpVo>();
		List<MpVo> methods = getMpVos(appName);
		if(StringUtils.isNotBlank(methodName)){
			List<MpVo> list = new ArrayList<>();
			int index = 1;
			for (MpVo mv: methods) {
				if(mv.getMethodName().indexOf(methodName)>=0){
					mv.setSequence(index);
					list.add(mv);
					index ++;
				}
			}
			pageData.setDataList(list);
		}else{
			pageData.setDataList(methods);
		}
		model.addAttribute("pageData", pageData);
		model.addAttribute("methodName",methodName);
		model.addAttribute("appName",appName);
		return "mp/mpList";
	}

	@RequestMapping(value = "/listJson/{pageNo}/{pageSize}")
	@ResponseBody
	public ResponseDTO listJson(String methodName,String appName) {
		ResponseDTO<List<MpVo>> responseDTO = new ResponseDTO<>();
		responseDTO.setCode(ReturnCode.ACTIVE_EXCEPTION.code());
		try{
			List<MpVo> list = getMpVos(appName);
			responseDTO.setAttach(list);
			if(list.size()>0){
				List<MpVo> result = new ArrayList<>();
				for(MpVo mpVo:list){
					if(mpVo.getMethodName().indexOf(methodName) >= 0){
						result.add(mpVo);
					}
				}
				responseDTO.setAttach(result);
			}
			responseDTO.setCode(ReturnCode.ACTIVE_SUCCESS.code());
		}catch (Exception e){
			logger.error("erro {}",e);
		}
		return responseDTO;
	}

	@RequestMapping(value = "/cmd")
	@ResponseBody
	public ResponseDTO cmdMethod(String appName,String methodName,String oldMethName,String option){
		ResponseDTO<List<MpVo>> responseDTO = new ResponseDTO<>();
		responseDTO.setCode(ReturnCode.ACTIVE_EXCEPTION.code());
		if(StringUtils.isBlank(methodName)){
			responseDTO.setCode(ReturnCode.ERROR_PARAMS.code());
			responseDTO.setMsg(ReturnCode.ERROR_PARAMS.msg());
			return responseDTO;
		}
		try{
			boolean isCreated = true;
			String methods = WatchableConfigClient.getInstance().get(appName, "sauron-methods", "ALL");
			if(methods.equals("ALL") && WatchableConfigClient.getInstance().isCreate(appName,"sauron-methods") == null){
//				responseDTO.setCode(ReturnCode.ERROR_NO_INFO.code());
//				responseDTO.setMsg(ReturnCode.ERROR_NO_INFO.msg());
				isCreated = false;
				methods = "";
//				return responseDTO;
			}
			String value = null;
			if(Option.CREATE.name().equals(option)){
				value = createAndDelValue(methods,methodName,Option.CREATE);
			}else if(Option.UPDATE.name().equals(option)){
				value = updateValue(methods,oldMethName,methodName);
			}else{
				value = createAndDelValue(methods,methodName,Option.DELETE);
			}
			if(isCreated){
				WatchableConfigClient.getInstance().set(appName, "sauron-methods",value);
			}else{
				WatchableConfigClient.getInstance().create(appName, "sauron-methods",value);
			}
			WatchableConfigClient.getInstance().setLocalCache(appName,"sauron-methods",value);
			responseDTO.setCode(ReturnCode.ACTIVE_SUCCESS.code());
		}catch (Exception e){
			logger.error("erro {}",e);
		}
		return responseDTO;
	}

	private List<MpVo> getMpVos(String appName){
		List<MpVo> list = new ArrayList<>();
		MpVo mpVo = null;
		String methods = WatchableConfigClient.getInstance().get(appName, "sauron-methods", "ALL");

		String[] methodArray = methods.split("#");
		String method;
		for (int loop = 0; loop < methodArray.length; loop ++){
			if(methods.equals("ALL")) break;
			method = methodArray[loop];
			if(method.trim().length() == 0) continue;
			mpVo = new MpVo();
			mpVo.setSequence(loop+1);
			mpVo.setMethodName(method);
			list.add(mpVo);
		}
		return list;
	}


	private String createAndDelValue(String methods,String methodName,Option option){
		String value = "";
		if(option.name().equals(option.CREATE.name())){
//			if(methods.lastIndexOf("#")>=0){
//				value = methods + methodName;
//			}else{
//
			if(methods.equals("")){
				value = methodName;
			}else{
				value = methods + "#" + methodName;
			}

//			}
		}else{
			String[] methodsArr = methods.split("#");
			StringBuffer sb = new StringBuffer();
			int index = 0;
			for(String method:methodsArr){
				if(index>0){
					sb.append("#");
				}
				if(method.equals(methodName)){
					continue;
				}else {
					if(method.trim().length()>0){
						sb.append(method);
						index ++;
					}
				}
			}
			value = sb.toString();
		}

		return value;
	}

	private String updateValue(String methods,String oldMethodNam,String methodName){
		String[] methodsArr = methods.split("#");
		StringBuffer sb = new StringBuffer();
		int index = 0;
		for (String method : methodsArr) {
			if(method.trim().length()<1){
				continue;
			}
			if(index>0){
				sb.append("#");
			}
			if(!(oldMethodNam.equals(method))){
				sb.append(method);
			}else{
				sb.append(methodName);
			}
			index ++;
		}
		return sb.toString();
	}

	@Override
	protected UserService getUserService() {
		return userService;
	}

	enum Option{
		CREATE,UPDATE,DELETE;
	}


}
