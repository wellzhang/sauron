package com.feng.sauron.warning.web.vo;

/**
 * Created by lianbin.wang on 2016/11/15.
 */
public class TopologyNodeURL {
    private String appName;
    private String url;
    //总调用次数
    private int totalCount;

    private int errorCount;

    private long timeCost;

    //最耗时Traceid
    private String traceID;


    public TopologyNodeURL(String appName, int totalCount, String url) {
        this.appName = appName;
        this.totalCount = totalCount;
        this.url = url;
    }

    public TopologyNodeURL() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public long getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(long timeCost) {
        this.timeCost = timeCost;
    }

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }
}
