package com.feng.sauron.warning.web.vo;

import java.util.List;

/**
 * 首页拓扑图
 * Created by lianbin.wang on 2016/11/15.
 */
public class TopologyData {

    //开始时间和结束时间
    private long startTime;
    private long endTime;

    private List<TopologyNode> nodeDataArray;

    private List<TopologyLink> linkDataArray;

    public TopologyData(List<TopologyLink> linkDataArray, List<TopologyNode> nodeDataArray) {
        this.linkDataArray = linkDataArray;
        this.nodeDataArray = nodeDataArray;
    }

    public TopologyData() {
    }

    public List<TopologyLink> getLinkDataArray() {
        return linkDataArray;
    }

    public void setLinkDataArray(List<TopologyLink> linkDataArray) {
        this.linkDataArray = linkDataArray;
    }

    public List<TopologyNode> getNodeDataArray() {
        return nodeDataArray;
    }

    public void setNodeDataArray(List<TopologyNode> nodeDataArray) {
        this.nodeDataArray = nodeDataArray;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
