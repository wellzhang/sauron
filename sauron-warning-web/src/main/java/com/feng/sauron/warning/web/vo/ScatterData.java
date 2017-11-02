package com.feng.sauron.warning.web.vo;

/**
 * 首頁散點圖
 * Created by lianbin.wang on 2016/11/15.
 */
public class ScatterData {

    //traceid 執行時間
    private long time;
    //執行耗時(ms)
    private long timeCost;

    private String traceID;

    //成功還是失敗
    //成功："success"，失敗"failed"
    private String type;

    public ScatterData(long time, long timeCost, String traceID, String type) {
        this.time = time;
        this.timeCost = timeCost;
        this.traceID = traceID;
        this.type = type;
    }

    public ScatterData() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
