package com.feng.sauron.warning.domain;

import java.util.Date;

public class WarningEvent {
    private Long id;

    private String appName;

    private String methodName;

    private String hostName;

    private String instantName;

    private Date occurTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName == null ? null : methodName.trim();
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName == null ? null : hostName.trim();
    }

    public String getInstantName() {
        return instantName;
    }

    public void setInstantName(String instantName) {
        this.instantName = instantName == null ? null : instantName.trim();
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }
}