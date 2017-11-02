package com.feng.sauron.warning.domain;

import java.util.Date;

public class Rules {
    private Long id;

    private String appName;

    private Long appId;

    private String hostName;

    private String methodName;

    private Long template;

    private Byte type;

    private String monitorKey;

    private Long creatorId;

    private Date createdTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
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

    public Long getTemplate() {
        return template;
    }

    public void setTemplate(Long template) {
        this.template = template;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getMonitorKey() {
        return monitorKey;
    }

    public void setMonitorKey(String monitorKey) {
        this.monitorKey = monitorKey;
    }

    public enum TypeEnum{
        Function((byte)1),DefinedKeyAlarm((byte)2),DefinedKeyAndBlock((byte)3);
        private byte value;
        TypeEnum (byte value){this.value = value;}
        public byte val(){return value;}
    }

}