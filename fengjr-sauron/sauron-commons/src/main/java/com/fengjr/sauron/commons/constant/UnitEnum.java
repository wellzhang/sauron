package com.fengjr.sauron.commons.constant;

/**
 * Created by bingquan.an@fengjr.com on 2015/11/2.
 */
public enum UnitEnum {
    MINUTE(1, "minute", "分钟"),
    HOURS(2, "hours", "小时"),
    DAYS(3, "days", "天");

    int id;
    String name;
    String desc;

    UnitEnum(int id, String name, String desc){
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
