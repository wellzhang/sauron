package com.feng.sauron.warning.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Liuyb on 2016/1/11.
 */
@Controller
@RequestMapping("/trace")
public class InvokingTraceController {

    @RequestMapping("")
    public String getInvokingSearchPage() {
        return "trace/invokeTrace";
    }
}
