package com.feng.sauron.warning.web;

import com.feng.sauron.warning.common.Page;
import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.domain.App;
import com.feng.sauron.warning.domain.User;
import com.feng.sauron.warning.service.base.AppService;
import com.feng.sauron.warning.service.base.UserService;
import com.feng.sauron.warning.web.base.BaseController;
import com.fengjr.upm.filter.util.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * AppController
 * Created by jianzhang
 */
@Controller
@RequestMapping("/app")
public class AppController extends BaseController {

    @Autowired
    private AppService appService;

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/list/{pageNo}/{pageSize}")
    public String list(@PathVariable(value = "pageNo") int pageNo, @PathVariable(value = "pageSize") int pageSize,String appName, Model model) {
        Page<App> pageData = new Page<App>();
        String creatorId = getCreatorId();
        pageData.setDataList(appService.findByPage(pageNo, pageSize,appName,creatorId));
        List<User> strategyList = userService.findByPage(0,1000);
        model.addAttribute("users", strategyList);
        model.addAttribute("pageData", pageData);
//        UserUtils.getUser().
        return "app/app";
    }

    @ResponseBody
    @RequestMapping(value = "/add")
    public ResponseDTO<App> addApp(String name, String describes,String userId,String userName) {
        ResponseDTO<App> responseDTO = new ResponseDTO<App>(ReturnCode.ACTIVE_EXCEPTION);
        App app = new App();
        app.setName(name);
        app.setDescribes(describes);
        app.setUserId(userId);
        app.setUserName(userName);
        try {
            appService.addApp(app);
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
        }
        return responseDTO;
    }


    @ResponseBody
    @RequestMapping(value = "/detail/{id}")
    public ResponseDTO<App> appDetail(@PathVariable("id") long id) {
        ResponseDTO<App> responseDTO = new ResponseDTO<App>(ReturnCode.ACTIVE_EXCEPTION);
        try {
            responseDTO.setAttach(appService.findAppById(id));
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
            responseDTO.setMsg(ex.getMessage());
        }
        return responseDTO;
    }

    @ResponseBody
    @RequestMapping(value = "/update")
    public ResponseDTO<App> modifyApp(long id,String name, String describes,String userId,String userName) {
        ResponseDTO<App> responseDTO = new ResponseDTO<App>(ReturnCode.ACTIVE_EXCEPTION);
        try {

            App app = appService.findAppById(id);
            app.setName(name);
            app.setDescribes(describes);
            app.setUserId(userId);
            app.setUserName(userName);
            appService.updateApp(app);
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
            responseDTO.setMsg(ex.getMessage());
        }
        return responseDTO;
    }


    @ResponseBody
    @RequestMapping(value = "/del")
    public ResponseDTO<App> delApp(String ids) {
        ResponseDTO<App> responseDTO = new ResponseDTO<App>(ReturnCode.ACTIVE_EXCEPTION);
        if(StringUtils.isBlank(ids)) {
            responseDTO.setMsg("参数不能为空");
        }
        try {
//            appService.delApp(ids);
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
