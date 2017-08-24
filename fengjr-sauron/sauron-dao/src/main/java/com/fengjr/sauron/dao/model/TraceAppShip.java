package com.fengjr.sauron.dao.model;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/10.
 */
public class TraceAppShip {

    private String traceId;

    private String appName;

    public TraceAppShip(){}

    public TraceAppShip(String traceId,String appName){
        this.traceId = traceId;
        this.appName = appName;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }


    @Override
    public String toString() {
        return "TraceAppShip{" +
                "traceId='" + traceId + '\'' +
                ", appName='" + appName + '\'' +
                '}';
    }
}
