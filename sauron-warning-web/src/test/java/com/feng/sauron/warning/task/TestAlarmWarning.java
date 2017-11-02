package com.feng.sauron.warning.task;

import com.feng.sauron.warning.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * Created by xubiao.fan on 2016/5/19.
 */
public class TestAlarmWarning  extends BaseTest{

    @Resource(name = "CodeBulkTask")
    WarningTask warningTask;
    @Autowired
    CodeBulkAlarmWarningTask codeBulkAlarmWarningTask;


    @Test
    public void testCodebulkTask(){

        warningTask.readDataFromInfluxdb();

    }

    @Test
    public void testCodebulkAlarmRun(){
        codeBulkAlarmWarningTask.run();
    }



}
