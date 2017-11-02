package com.feng.sauron.warning.web;

import org.omg.CORBA.Object;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Map;

/**
 * Created by lianbin.wang on 11/11/2016.
 */
@RequestMapping("/tree")
@Controller
public class TestTreeController implements ApplicationContextAware {


    private ApplicationContext context;

    @RequestMapping("")
    @ResponseBody
    public Map<String, Object> tree(@RequestParam(required = false) String pkgOrClassName) {
        String[] beanDefinitionNames = this.context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.err.println(beanDefinitionName);
        }

        return Collections.emptyMap();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = context;
    }
}
