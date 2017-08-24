package com.fengjr.sauron.commons.constant;

/**
 * Created by bingquan.an@fengjr.com on 2015/11/2.
 */
public enum MetricsEnum {

    COST(1, "cost", "耗时"),
    COUNT(2, "count", "次数"),
    TP(3, "tp", "tp指标");

    int id;
    String name;
    String desc;


    MetricsEnum(int id, String name, String desc) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
