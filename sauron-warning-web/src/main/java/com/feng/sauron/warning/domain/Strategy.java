package com.feng.sauron.warning.domain;

import java.util.Date;

public class Strategy {
    private Long id;

    private String stgyName;

    private Integer minInterval;

    private Integer warnCountWithTime;

    private Integer warnMaxCount;

    private String warnDaysInWeek;

    private String startTimeInDay;

    private String endTimeInDay;

    private Date createTime;

    private Long creatorId;

    private Integer status;

    private Long bakNum;

    private String bakChar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStgyName() {
        return stgyName;
    }

    public void setStgyName(String stgyName) {
        this.stgyName = stgyName == null ? null : stgyName.trim();
    }

    public Integer getMinInterval() {
        return minInterval;
    }

    public void setMinInterval(Integer minInterval) {
        this.minInterval = minInterval;
    }

    public Integer getWarnCountWithTime() {
        return warnCountWithTime;
    }

    public void setWarnCountWithTime(Integer warnCountWithTime) {
        this.warnCountWithTime = warnCountWithTime;
    }

    public Integer getWarnMaxCount() {
        return warnMaxCount;
    }

    public void setWarnMaxCount(Integer warnMaxCount) {
        this.warnMaxCount = warnMaxCount;
    }

    public String getWarnDaysInWeek() {
        return warnDaysInWeek;
    }

    public void setWarnDaysInWeek(String warnDaysInWeek) {
        this.warnDaysInWeek = warnDaysInWeek == null ? null : warnDaysInWeek.trim();
    }

    public String getStartTimeInDay() {
        return startTimeInDay;
    }

    public void setStartTimeInDay(String startTimeInDay) {
        this.startTimeInDay = startTimeInDay == null ? null : startTimeInDay.trim();
    }

    public String getEndTimeInDay() {
        return endTimeInDay;
    }

    public void setEndTimeInDay(String endTimeInDay) {
        this.endTimeInDay = endTimeInDay == null ? null : endTimeInDay.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getBakNum() {
        return bakNum;
    }

    public void setBakNum(Long bakNum) {
        this.bakNum = bakNum;
    }

    public String getBakChar() {
        return bakChar;
    }

    public void setBakChar(String bakChar) {
        this.bakChar = bakChar == null ? null : bakChar.trim();
    }
}