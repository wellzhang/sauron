package com.feng.sauron.warning.service;

import java.util.List;

/**
 * Created by lianbin.wang on 2016/11/16.
 */
public interface CallstackService {

    List<CallstackItem> loadCallStack(String traceId, String appName);

}
