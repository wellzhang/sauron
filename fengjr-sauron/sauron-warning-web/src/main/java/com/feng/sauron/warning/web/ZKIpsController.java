package com.feng.sauron.warning.web;

import com.feng.sauron.warning.common.Page;
import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.domain.ZookeeperIps;
import com.feng.sauron.warning.monitor.dubbo.StartDam;
import com.feng.sauron.warning.service.base.ZookeeperIpsService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ZKIpsController
 * Created by jianzhang
 */
@Controller
@RequestMapping("/zk")
public class ZKIpsController {
    @Autowired
    private ZookeeperIpsService  zookeeperIpsService;

    @Autowired
    private StartDam startDam;

    @RequestMapping(value = "/list/{pageNo}/{pageSize}")
    public String list(@PathVariable(value = "pageNo") int pageNo, @PathVariable(value = "pageSize") int pageSize, Model model) {
        Page<ZookeeperIps> pageData = new Page<ZookeeperIps>();
        pageData.setDataList(zookeeperIpsService.findByPage(pageNo, pageSize));
        model.addAttribute("pageData", pageData);
        return "ips/zkIp";
    }

    @ResponseBody
    @RequestMapping(value = "/add")
    public ResponseDTO<ZookeeperIps> addZookeeperIps(String name, String zkIp, String describes) {
        ResponseDTO<ZookeeperIps> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
        ZookeeperIps zookeeperIps = new ZookeeperIps();
        zookeeperIps.setName(name);
        zookeeperIps.setZkIp(zkIp);
        zookeeperIps.setDescribes(describes);
        try {
            zookeeperIpsService.addZookeeperIps(zookeeperIps);
            startDam.createThread(zookeeperIps);
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
        }
        return responseDTO;
    }


    @ResponseBody
    @RequestMapping(value = "/detail/{id}")
    public ResponseDTO<ZookeeperIps> zookeeperIpsDetail(@PathVariable("id") long id) {
        ResponseDTO<ZookeeperIps> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
        try {
            responseDTO.setAttach(zookeeperIpsService.findZookeeperIpsById(id));
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
            responseDTO.setMsg(ex.getMessage());
        }
        return responseDTO;
    }

    @ResponseBody
    @RequestMapping(value = "/update")
    public ResponseDTO<ZookeeperIps> modifyZookeeperIps(long id,String name, String zkIp, String describes) {
        ResponseDTO<ZookeeperIps> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
        try {

            ZookeeperIps zookeeperIps = zookeeperIpsService.findZookeeperIpsById(id);
            startDam.destroyThread(zookeeperIps);
            zookeeperIps.setName(name);
            zookeeperIps.setZkIp(zkIp);
            zookeeperIps.setDescribes(describes);
            zookeeperIpsService.updateZookeeperIps(zookeeperIps);
            startDam.createThread(zookeeperIps);
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
            responseDTO.setMsg(ex.getMessage());
        }
        return responseDTO;
    }


    @ResponseBody
    @RequestMapping(value = "/del")
    public ResponseDTO<ZookeeperIps> delZookeeperIps(String ids) {
        ResponseDTO<ZookeeperIps> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
        if(StringUtils.isBlank(ids)) {
            responseDTO.setMsg("参数不能为空");
        }
        try {
            String zIds[] = ids.split(";");
            ZookeeperIps zookeeperIps = null;
            for(String id : zIds){
                zookeeperIps = zookeeperIpsService.findZookeeperIpsById(Long.valueOf(id));
                startDam.destroyThread(zookeeperIps);
            }
            zookeeperIpsService.delZookeeperIps(ids);
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
            responseDTO.setMsg(ex.getMessage());
        }
        return responseDTO;
    }



}
