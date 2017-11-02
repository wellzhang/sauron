package com.feng.sauron.warning.web.vo;

import java.util.List;

/**
 * Created by lianbin.wang on 2016/11/15.
 */
public class TopologyNode {
//{"key": "A", "nodeName": "Hwllo", "category": "TOMCAT", fields: fields},


    //节点唯一标识
    private String key;

    //节点显示名称
    private String nodeName;

    //节点类型(tomcat?mysql?dubbo? TODO:需定义枚举)
    private String category;

    private List<TopologyNodeURL> urlList;

    public TopologyNode(String category, String key, String nodeName, List<TopologyNodeURL> urlList) {
        this.category = category;
        this.key = key;
        this.nodeName = nodeName;
        this.urlList = urlList;
    }

    public TopologyNode() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public List<TopologyNodeURL> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<TopologyNodeURL> urlList) {
        this.urlList = urlList;
    }
}