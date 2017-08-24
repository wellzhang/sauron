package com.feng.sauron.warning.domain;

public class Metrics {
    private Long id;

    private Long metricOptId;

    private Double value;

    private Long relEventId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMetricOptId() {
        return metricOptId;
    }

    public void setMetricOptId(Long metricOptId) {
        this.metricOptId = metricOptId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getRelEventId() {
        return relEventId;
    }

    public void setRelEventId(Long relEventId) {
        this.relEventId = relEventId;
    }
}