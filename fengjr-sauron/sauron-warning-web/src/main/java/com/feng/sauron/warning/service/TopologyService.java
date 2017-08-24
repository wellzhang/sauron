package com.feng.sauron.warning.service;

import com.feng.sauron.warning.web.vo.TopologyData;

import java.util.Date;

/**
 * Created by lianbin.wang on 2016/11/15.
 */
public interface TopologyService {
    TopologyData load(String appName, String host, Date startTime, Date endTime);
}
