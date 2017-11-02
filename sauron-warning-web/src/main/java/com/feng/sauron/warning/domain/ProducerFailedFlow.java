package com.feng.sauron.warning.domain;

import java.util.Date;

public class ProducerFailedFlow {
    private Long id;

    private String clientip;

    private String instancename;

    private String namesrvaddr;

    private String pgroup;

    private String topic;

    private String tag;

    private String mqkey;

    private String body;

    private String reason;

    private Byte status;

    private Date ctime;

    private Date utime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientip() {
        return clientip;
    }

    public void setClientip(String clientip) {
        this.clientip = clientip == null ? null : clientip.trim();
    }

    public String getInstancename() {
        return instancename;
    }

    public void setInstancename(String instancename) {
        this.instancename = instancename == null ? null : instancename.trim();
    }

    public String getNamesrvaddr() {
        return namesrvaddr;
    }

    public void setNamesrvaddr(String namesrvaddr) {
        this.namesrvaddr = namesrvaddr == null ? null : namesrvaddr.trim();
    }

    public String getPgroup() {
        return pgroup;
    }

    public void setPgroup(String pgroup) {
        this.pgroup = pgroup == null ? null : pgroup.trim();
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic == null ? null : topic.trim();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag == null ? null : tag.trim();
    }

    public String getMqkey() {
        return mqkey;
    }

    public void setMqkey(String mqkey) {
        this.mqkey = mqkey == null ? null : mqkey.trim();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body == null ? null : body.trim();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
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