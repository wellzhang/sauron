package com.feng.sauron.warning.domain;

import java.util.Date;

public class UrlRules {
    private Long id;

    private Long appId;

    private String appName;

    private String monitorKey;

    private String monitorUrl;

    private Byte isEnabled;

    private String param;

    private Integer requestInterval;

    private Integer timeout;

    private Byte requestMode;

    private Byte isConfigHost;

    private String hostIp;

    private String matchContent;

    private Byte isContain;

    private String cookies;

    private Byte isDefaultCode;

    private Integer customCode;

    private Long template;

    private Long creatorId;

    private Date createdTime;

    private Date updataTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    public String getMonitorKey() {
        return monitorKey;
    }

    public void setMonitorKey(String monitorKey) {
        this.monitorKey = monitorKey == null ? null : monitorKey.trim();
    }

    public String getMonitorUrl() {
        return monitorUrl;
    }

    public void setMonitorUrl(String monitorUrl) {
        this.monitorUrl = monitorUrl == null ? null : monitorUrl.trim();
    }

    public Byte getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Byte isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param == null ? null : param.trim();
    }

    public Integer getRequestInterval() {
        return requestInterval;
    }

    public void setRequestInterval(Integer requestInterval) {
        this.requestInterval = requestInterval;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Byte getRequestMode() {
        return requestMode;
    }

    public void setRequestMode(Byte requestMode) {
        this.requestMode = requestMode;
    }

    public Byte getIsConfigHost() {
        return isConfigHost;
    }

    public void setIsConfigHost(Byte isConfigHost) {
        this.isConfigHost = isConfigHost;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp == null ? null : hostIp.trim();
    }

    public String getMatchContent() {
        return matchContent;
    }

    public void setMatchContent(String matchContent) {
        this.matchContent = matchContent == null ? null : matchContent.trim();
    }

    public Byte getIsContain() {
        return isContain;
    }

    public void setIsContain(Byte isContain) {
        this.isContain = isContain;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies == null ? null : cookies.trim();
    }

    public Byte getIsDefaultCode() {
        return isDefaultCode;
    }

    public void setIsDefaultCode(Byte isDefaultCode) {
        this.isDefaultCode = isDefaultCode;
    }

    public Integer getCustomCode() {
        return customCode;
    }

    public void setCustomCode(Integer customCode) {
        this.customCode = customCode;
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

    public Date getUpdataTime() {
        return updataTime;
    }

    public void setUpdataTime(Date updataTime) {
        this.updataTime = updataTime;
    }

    public enum RequestMode {
        HEAD(0), GET(1), POST(2);

        private int value;

        RequestMode(int value) {
            this.value = value;
        }

        public int val() {
            return value;
        }

        @Override
        public String toString() {
            return super.toString() + "(" + value + ")";
        }
    }

    public enum IsDefaultCode{
        DefaultCode(0),NoneDefaultCode(1);
        private int value;
        IsDefaultCode( int value){ this.value = value;}
        public int val(){return value;}
    }

    public enum IsConfigHost{
        ConfigHost(0),NoneConfigHost(1);
        private int value;
        IsConfigHost (int value){this.value = value;}
        public int val(){return value;}
    }

    public enum IsContain{
        Contain(0),NoneContain(1);
        private int value;
        IsContain(int value){this.value = value;}
        public int val(){return value;}
    }

    public enum IsEnabled{
        Enabled(0),NoneEnabled(1);
        private int value;
        IsEnabled(int value){this.value = value;}
        public int val(){return value;}
    }




}
