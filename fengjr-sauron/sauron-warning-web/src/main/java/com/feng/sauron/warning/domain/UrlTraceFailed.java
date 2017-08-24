package com.feng.sauron.warning.domain;

import java.util.Date;

public class UrlTraceFailed {
    private Long id;

    private Long urlMonitorId;

    private String result;

    private Date createdTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUrlMonitorId() {
        return urlMonitorId;
    }

    public void setUrlMonitorId(Long urlMonitorId) {
        this.urlMonitorId = urlMonitorId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}