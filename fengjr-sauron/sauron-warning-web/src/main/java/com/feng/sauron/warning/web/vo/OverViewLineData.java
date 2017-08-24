package com.feng.sauron.warning.web.vo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lianbin.wang on 2016/11/25.
 */
public class OverViewLineData {
    //进程号
    private String pID;
    private String[] lineNames;
    private LinkedHashMap<Long, List<Number>> lineDatas;

    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }

    public String[] getLineNames() {
        return lineNames;
    }

    public void setLineNames(String[] lineNames) {
        this.lineNames = lineNames;
    }

    public LinkedHashMap<Long, List<Number>> getLineDatas() {
        return lineDatas;
    }

    public void setLineDatas(LinkedHashMap<Long, List<Number>> lineDatas) {
        this.lineDatas = lineDatas;
    }
}
