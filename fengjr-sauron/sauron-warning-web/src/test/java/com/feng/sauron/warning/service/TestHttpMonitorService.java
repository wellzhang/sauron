package com.feng.sauron.warning.service;

import com.feng.sauron.warning.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xubiao.fan on 2016/5/20.
 */
public class TestHttpMonitorService  extends BaseTest{

    @Autowired
    HttpMonitorService httpMonitorService;


     @Test
     public void testRunByRuleId(){

         httpMonitorService.runUrlMonitorById(189);

     }


}
