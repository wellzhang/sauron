package com.feng.sauron.warning.domain;

public class Clauses {
    private Long id;

    private Long metricOptId;

    private String operator;

    private Long varible;

    private Long relRuleId;

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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public Long getVarible() {
        return varible;
    }

    public void setVarible(Long varible) {
        this.varible = varible;
    }

    public Long getRelRuleId() {
        return relRuleId;
    }

    public void setRelRuleId(Long relRuleId) {
        this.relRuleId = relRuleId;
    }
}