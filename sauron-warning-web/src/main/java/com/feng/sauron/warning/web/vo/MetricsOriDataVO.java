package com.feng.sauron.warning.web.vo;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/18.
 */
public class MetricsOriDataVO {

    //traceid 執行時間
    private long time;
    //執行耗時(ms)
    private long timeCost;

    private String traceID;

    //成功還是失敗
    //成功："success"，失敗"fail"
    private String status;

    //调用类型,联动散点图过滤
    private String appType;

    public MetricsOriDataVO(long time, long timeCost, String traceID, String status) {
        this.time = time;
        this.timeCost = timeCost;
        this.traceID = traceID;
        this.status = status;
    }

    public MetricsOriDataVO() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
