package com.fengjr.sauron.commons.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bingquan.an@fengjr.com on 2015/11/2.
 */
public class Sampleing {
    /**
     * 调用次数
     */
    int count;
    int sumMs;
    /**
     * 调用时间损耗
     */
    int avgMs;
    String target;
    String metrics;
    /**
     * 时间刻度
     */
    String timeStr;
    /**
     * tp指标
     */
    Map<String, String> tpMap = new HashMap<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSumMs() {
        return sumMs;
    }

    public void setSumMs(int sumMs) {
        this.sumMs = sumMs;
    }

    public int getAvgMs() {
        return avgMs;
    }

    public void setAvgMs(int avgMs) {
        this.avgMs = avgMs;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }


    public Map<String, String> getTpMap() {
        return tpMap;
    }

    public void setTpMap(Map<String, String> tpMap) {
        this.tpMap = tpMap;
    }

    @Override
    public String toString() {
         ToStringBuilder sb = new ToStringBuilder(this);
         sb.append("count", count)
            .append("sumMs", sumMs)
            .append("avgMs", avgMs)
            .append("target", target)
            .append("metrics", metrics)
            .append("timeStr", timeStr);

        for(Map.Entry<String, String> entry : tpMap.entrySet()){
            sb.append(entry.getKey(), entry.getValue());
        }
        return sb.toString();
    }

    public Sampleing() {
    }

    /**
     * 
     * @param timeStr
     * @param count
     * @param sumMs
     * @param avgMs
     * @param target
     * @param metrics
     */
    public Sampleing(String timeStr, int count, int sumMs, int avgMs, String target, String metrics) {
        this.timeStr = timeStr;
        this.count = count;
        this.sumMs = sumMs;
        this.avgMs = avgMs;
        this.target = target;
        this.metrics = metrics;
    }
}
