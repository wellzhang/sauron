package com.feng.sauron.warning.domain;

public class RuleReceiversKey {
    private Long ruleId;

    private Long contactId;

    private Byte type;

    private String notifyMode;

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getNotifyMode() {
        return notifyMode;
    }

    public void setNotifyMode(String notifyMode) {
        this.notifyMode = notifyMode;
    }

    public enum Type{
        Fuction((byte)1),Url((byte)2),Dubbo((byte)3),custom((byte)4);
        private byte value;
        Type(byte value){this.value = value;}
        public byte val(){return value;}
    }

    public enum NotifyMode{
        None(0,"不发送"),Email(1,"邮件"),Message(2,"短信"),Group(3,"邮件组");
        private int value;
        private String name;

        NotifyMode(int value,String name){
            this.value = value;
            this.name = name;
        }
        public int val(){return value;}
    }

}