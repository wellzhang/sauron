package com.fengjr.sauron.commons.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by bingquan.an@fengjr.com on 2015/11/2.
 */
public class UpRequest {
    /**
     * 目标方法
     */
    String target;
    /**
     * 方法指标
     */
    String metrics;
    /**
     * 时间类型
     */
    String unit;//days, hours, minutes
    /**
     * 时间刻度
     */
    int tick;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("target", target)
                .append("unit", unit)
                .append("tick", tick)
                .append("metrics", metrics)
                .toString();
    }
}
