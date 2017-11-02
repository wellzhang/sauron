package com.feng.sauron.warning.service;

import com.feng.sauron.warning.web.vo.Top10Iterm;
import com.feng.sauron.warning.web.vo.Top10LineItem;

import java.util.List;

/**
 * Created by lianbin.wang on 2016/11/15.
 */
public interface Top10Service {

    List<Top10Iterm> loadTotal(String appName, String host, String type);

    List<Top10LineItem> loadLineTotal(String appName, String host, String type, String url);


	List<Top10Iterm> loadError(String appName, String host, String type);

	List<Top10LineItem> loadLineError(String appName, String host, String type, String url);
}
