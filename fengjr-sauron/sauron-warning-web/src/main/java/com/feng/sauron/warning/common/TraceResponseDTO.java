package com.feng.sauron.warning.common;

/**
 * Created by jian.zhang on 2016/6/4.
 */
public class TraceResponseDTO<T> extends ResponseDTO {

    private String treeData;


    public String getTreeData() {
        return treeData;
    }

    public void setTreeData(String treeData) {
        this.treeData = treeData;
    }
}
