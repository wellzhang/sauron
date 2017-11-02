package com.feng.sauron.warning.web.vo;

/**
 * Created by lianbin.wang on 2016/11/15.
 */
public class Top10LineItem {
    private String traceID;
    private long timestamp;
    private long timeCost;

    public Top10LineItem(String traceID, long timestamp, long timeCost) {
        this.traceID = traceID;
        this.timestamp = timestamp;
        this.timeCost = timeCost;
    }

    public long getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(long timeCost) {
        this.timeCost = timeCost;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }
}
