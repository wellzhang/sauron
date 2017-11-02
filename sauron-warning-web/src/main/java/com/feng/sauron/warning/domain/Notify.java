package com.feng.sauron.warning.domain;

public class Notify {
    private Long id;

    private String title;

    private String content;

    private Long relRuleId;

    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Long getRelRuleId() {
        return relRuleId;
    }

    public void setRelRuleId(Long relRuleId) {
        this.relRuleId = relRuleId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}