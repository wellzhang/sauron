package com.feng.sauron.warning.domain;

import java.util.Date;

public class ExceptionEvent {
    private Long id;

    private String appName;

    private String methodName;

    private String hostName;

    private String exceptionInfo;

    private Date occurTime;

    private String params;

    private String bkchar1;

    private String bkchar2;

    private Long bknum1;

    private Integer bknum2;

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

    public String getExceptionInfo() {
        return exceptionInfo;
    }

    public void setExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo == null ? null : exceptionInfo.trim();
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    public String getBkchar1() {
        return bkchar1;
    }

    public void setBkchar1(String bkchar1) {
        this.bkchar1 = bkchar1 == null ? null : bkchar1.trim();
    }

    public String getBkchar2() {
        return bkchar2;
    }

    public void setBkchar2(String bkchar2) {
        this.bkchar2 = bkchar2 == null ? null : bkchar2.trim();
    }

    public Long getBknum1() {
        return bknum1;
    }

    public void setBknum1(Long bknum1) {
        this.bknum1 = bknum1;
    }

    public Integer getBknum2() {
        return bknum2;
    }

    public void setBknum2(Integer bknum2) {
        this.bknum2 = bknum2;
    }
}