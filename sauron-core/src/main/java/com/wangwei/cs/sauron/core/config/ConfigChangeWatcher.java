package com.wangwei.cs.sauron.core.config;

/**
 * Created by Liuyb on 2015/10/28.
 */
public interface ConfigChangeWatcher {
    /**
     *
     * @param newVal
     */
    void onValueChanged(String newVal);
}
