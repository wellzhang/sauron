package com.feng.sauron.warning.web.vo;

/**
 * Created by lianbin.wang on 2016/11/15.
 */
public class TopologyLink {
    // {"from": "A", "to": "B", "totalCount": 100, "failCount": 20, "cost": 100, "status": "ok"},

    //箭头起始节点，值为节点的key字段
    private String from;
    //箭头指向节点
    private String to;


    //总调用次数
    private int totalCount;
    //失败次数
    private int failCount;

    //耗时(ms)平均耗时？top90？
    private long timeCost;

    //用于判断线条颜色
    private String status;

    //所有调用中耗时最长的一次traceid
    private String traceID;

    //调用类型，用于联动散点图时做过滤
    private String type;


    public TopologyLink(String from, String to, int totalCount, int failCount, long timeCost, String status) {
        this.failCount = failCount;
        this.from = from;
        this.status = status;
        this.timeCost = timeCost;
        this.to = to;
        this.totalCount = totalCount;
    }

    public TopologyLink() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(long timeCost) {
        this.timeCost = timeCost;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}