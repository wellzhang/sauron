package com.feng.sauron.warning.domain;

import java.util.Date;

public class ZookeeperIps {
    private Long id;

    private String zkIp;

    private String name;

    private String describes;

    private Date createdTime;

    private Date updataTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getZkIp() {
        return zkIp;
    }

    public void setZkIp(String zkIp) {
        this.zkIp = zkIp == null ? null : zkIp.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes == null ? null : describes.trim();
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