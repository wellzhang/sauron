package com.feng.sauron.warning.service;

import com.feng.sauron.warning.web.vo.TraceItem;

import java.util.List;

/**
 * Created by lianbin.wang on 2016/11/15.
 */
public interface TraceService {
    List<TraceItem> loadTrace(String[] appName, String[] traceID);
}
