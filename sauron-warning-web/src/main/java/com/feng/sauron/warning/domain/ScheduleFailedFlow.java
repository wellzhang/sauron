package com.feng.sauron.warning.domain;

import java.util.Date;

public class ScheduleFailedFlow {
    private Integer id;

    private String mqgroup;

    private String mqtopic;

    private String mqbody;

    private String executeparams;

    private String failedreason;

    private String executeurl;

    private String mqbornhost;

    private Date mqborntimestamp;

    private String instanceid;

    private Date processtimestamp;

    private String httpmethod;

    private Byte retrytimes;

    private String retryreponse;

    private Byte status;

    private Date ctime;

    private Date utime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMqgroup() {
        return mqgroup;
    }

    public void setMqgroup(String mqgroup) {
        this.mqgroup = mqgroup == null ? null : mqgroup.trim();
    }

    public String getMqtopic() {
        return mqtopic;
    }

    public void setMqtopic(String mqtopic) {
        this.mqtopic = mqtopic == null ? null : mqtopic.trim();
    }

    public String getMqbody() {
        return mqbody;
    }

    public void setMqbody(String mqbody) {
        this.mqbody = mqbody == null ? null : mqbody.trim();
    }

    public String getExecuteparams() {
        return executeparams;
    }

    public void setExecuteparams(String executeparams) {
        this.executeparams = executeparams == null ? null : executeparams.trim();
    }

    public String getFailedreason() {
        return failedreason;
    }

    public void setFailedreason(String failedreason) {
        this.failedreason = failedreason == null ? null : failedreason.trim();
    }

    public String getExecuteurl() {
        return executeurl;
    }

    public void setExecuteurl(String executeurl) {
        this.executeurl = executeurl == null ? null : executeurl.trim();
    }

    public String getMqbornhost() {
        return mqbornhost;
    }

    public void setMqbornhost(String mqbornhost) {
        this.mqbornhost = mqbornhost == null ? null : mqbornhost.trim();
    }

    public Date getMqborntimestamp() {
        return mqborntimestamp;
    }

    public void setMqborntimestamp(Date mqborntimestamp) {
        this.mqborntimestamp = mqborntimestamp;
    }

    public String getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(String instanceid) {
        this.instanceid = instanceid == null ? null : instanceid.trim();
    }

    public Date getProcesstimestamp() {
        return processtimestamp;
    }

    public void setProcesstimestamp(Date processtimestamp) {
        this.processtimestamp = processtimestamp;
    }

    public String getHttpmethod() {
        return httpmethod;
    }

    public void setHttpmethod(String httpmethod) {
        this.httpmethod = httpmethod == null ? null : httpmethod.trim();
    }

    public Byte getRetrytimes() {
        return retrytimes;
    }

    public void setRetrytimes(Byte retrytimes) {
        this.retrytimes = retrytimes;
    }

    public String getRetryreponse() {
        return retryreponse;
    }

    public void setRetryreponse(String retryreponse) {
        this.retryreponse = retryreponse == null ? null : retryreponse.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getUtime() {
        return utime;
    }

    public void setUtime(Date utime) {
        this.utime = utime;
    }
}