package com.feng.sauron.warning.domain;

import java.util.Date;

public class DubboRules {
    private Long id;

    private Long appId;

    private Long zookeeperIpsId;

    private String applicationName;

    private Long template;

    private Long creatorId;

    private Byte isEnabled;

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

    public Long getZookeeperIpsId() {
        return zookeeperIpsId;
    }

    public void setZookeeperIpsId(Long zookeeperIpsId) {
        this.zookeeperIpsId = zookeeperIpsId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName == null ? null : applicationName.trim();
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

    public Byte getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Byte isEnabled) {
        this.isEnabled = isEnabled;
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
}