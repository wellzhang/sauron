package com.feng.sauron.warning.domain;

import java.util.Date;

public class UrlMonitor {
    private Long id;

    private Long urlRulesId;

    private Integer failTimes;

    private Integer totalTimes;

    private Date createdTime;

    private Date updataTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUrlRulesId() {
        return urlRulesId;
    }

    public void setUrlRulesId(Long urlRulesId) {
        this.urlRulesId = urlRulesId;
    }

    public Integer getFailTimes() {
        return failTimes;
    }

    public void setFailTimes(Integer failTimes) {
        this.failTimes = failTimes;
    }

    public Integer getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(Integer totalTimes) {
        this.totalTimes = totalTimes;
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