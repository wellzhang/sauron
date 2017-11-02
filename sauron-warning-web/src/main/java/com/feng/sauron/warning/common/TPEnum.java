package com.feng.sauron.warning.common;

/**
 * Created by bingquan.an@fengjr.com on 2015/11/4.
 */
public enum TPEnum {
    TP_0(0, "tp0", 1),
    TP_90(1, "tp90", 0.1),
    TP_99(2, "tp99", 0.01),
    TP_999(3, "tp999", 0.001),
    TP_100(4, "tp100", 0);

    int id;
    String name;
    double value;

    TPEnum(int id, String name, double value) {
        this.id = id;
        this.name = name;
        this.value = value;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
