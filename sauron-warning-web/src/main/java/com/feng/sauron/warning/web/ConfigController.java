package com.feng.sauron.warning.web;

import com.feng.sauron.warning.domain.App;
import com.feng.sauron.warning.service.base.AppService;
import com.feng.sauron.warning.service.base.UserService;
import com.feng.sauron.warning.util.WatchableConfigClient;
import com.feng.sauron.warning.web.base.BaseController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jian.zhang on 2016/11/18.
 */

@Controller
@RequestMapping("/config")
public class ConfigController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @Resource
    private AppService appService;
    @Autowired
    private UserService userService;


    @RequestMapping(value = "/list")
    public String list(Model model) {
        List<App> appNameList = appService.findByPage(1,1000,null,getCreatorId());
        model.addAttribute("appNameList", appNameList);
        return "mp/config";
    }

    @RequestMapping(value = "/getValue")
    @ResponseBody
    public Object getValue(String appName){
        String switch_string = WatchableConfigClient.getInstance().get(appName, "sauron-warning-task-switch", "OFF");
        Map<String,String> map = new HashMap<>();
        map.put("option",switch_string);
        return map;
    }

    @RequestMapping(value = "/set")
    @ResponseBody
    public Object setvalue(String appName,String option) {
        Map<String,String> map = new HashMap<>();
        try {
            if(WatchableConfigClient.getInstance().isCreate(appName,"sauron-methods") == null){
                WatchableConfigClient.getInstance().create(appName,"sauron-warning-task-switch",option);
            }else{
                WatchableConfigClient.getInstance().set(appName,"sauron-warning-task-switch",option);
            }

            WatchableConfigClient.getInstance().setLocalCache(appName,"sauron-warning-task-switch",option);
            map.put("option",option);
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    @Override
    protected UserService getUserService() {
        return userService;
    }
}
