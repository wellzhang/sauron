package com.feng.sauron.warning.web.vo;

/**
 * Created by lianbin.wang on 2016/11/15.
 */
public class Top10Iterm {
    private String appName;
    private String host;
    private String type;
    private String url;
    private String time;

    public Top10Iterm(String appName, String host, String type, String url) {
        this.appName = appName;
        this.host = host;
        this.type = type;
        this.url = url;
    }

    public Top10Iterm() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
