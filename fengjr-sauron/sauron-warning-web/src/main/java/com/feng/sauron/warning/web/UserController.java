package com.feng.sauron.warning.web;

import com.feng.sauron.warning.common.Page;
import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.domain.User;
import com.feng.sauron.warning.domain.ZookeeperIps;
import com.feng.sauron.warning.monitor.dubbo.StartDam;
import com.feng.sauron.warning.service.base.UserService;
import com.feng.sauron.warning.service.base.ZookeeperIpsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * userController
 * Created by jianzhang
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/list/{pageNo}/{pageSize}")
    public String list(@PathVariable(value = "pageNo") int pageNo, @PathVariable(value = "pageSize") int pageSize, Model model) {
        Page<User> pageData = new Page<User>();
        pageData.setDataList(userService.findByPage(pageNo, pageSize));
        model.addAttribute("pageData", pageData);
        return "user/user";
    }

    @ResponseBody
    @RequestMapping(value = "/add")
    public ResponseDTO<User> addUser(String name, String userId) {
        ResponseDTO<User> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
        User user = new User();
        user.setName(name);
        user.setUserId(userId);
        try {
            userService.addUser(user);
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return responseDTO;
    }


    @ResponseBody
    @RequestMapping(value = "/detail/{id}")
    public ResponseDTO<User> zookeeperIpsDetail(@PathVariable("id") long id) {
        ResponseDTO<User> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
        try {
            responseDTO.setAttach(userService.findUserById(id));
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
            responseDTO.setMsg(ex.getMessage());
        }
        return responseDTO;
    }

    @ResponseBody
    @RequestMapping(value = "/update")
    public ResponseDTO<User> modifyZookeeperIps(long id,String name, String userId) {
        ResponseDTO<User> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
        try {

            User user = userService.findUserById(id);
            user.setName(name);
            user.setUserId(userId);
            userService.updateUser(user);
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
            responseDTO.setMsg(ex.getMessage());
            ex.printStackTrace();
        }
        return responseDTO;
    }


    @ResponseBody
    @RequestMapping(value = "/del")
    public ResponseDTO<User> delZookeeperIps(String ids) {
        ResponseDTO<User> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
        if(StringUtils.isBlank(ids)) {
            responseDTO.setMsg("参数不能为空");
        }
        try {
            userService.delUser(ids);
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
            responseDTO.setMsg(ex.getMessage());
        }
        return responseDTO;
    }



}
