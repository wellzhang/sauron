package com.fengjr.sauron.test.web.controller;


import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import com.fengjr.sauron.test.web.TestHttpClient;

import com.fengjr.sauron.test.web.service.CommonService;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年12月18日 上午10:30:51
 */

@Controller
@RequestMapping("/common")
public class CommonInfoController {


    @Resource
    CommonService commonService;


    @ResponseBody
    @RequestMapping(value = "/test", method = {RequestMethod.GET, RequestMethod.POST})
    public String test() {


        System.out.println("2222222222222222222222222");


        System.out.println("3333333333333333333333333");

        commonService.printError("34");
        System.out.println("555555555555555555555555555555555555");


        try {

            TestHttpClient.post();
        } catch (Exception e) {

            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return "success";
    }
}
