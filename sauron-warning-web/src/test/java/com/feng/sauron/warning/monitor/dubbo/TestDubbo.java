package com.feng.sauron.warning.monitor.dubbo;

import com.feng.sauron.warning.BaseTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Created by xubiao.fan on 2016/5/20.
 */
public class TestDubbo extends BaseTest {


    @Autowired
    StartDam startDam;
    @Test
    public void testDubbo() throws IOException {
        System.out.println(startDam.toString());
        System.in.read();
    }


}
