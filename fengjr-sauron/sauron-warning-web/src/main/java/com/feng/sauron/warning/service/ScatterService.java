package com.feng.sauron.warning.service;

import com.feng.sauron.warning.web.vo.MetricsOriDataVO;

import java.util.Date;
import java.util.List;

/**
 * Created by lianbin.wang on 2016/11/15.
 */
public interface ScatterService {
    List<MetricsOriDataVO> loadScatterData(Date startTime, Date endTime, String appName, String host);
}
