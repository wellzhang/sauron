package com.fengjr.sauron.converger.kafka.handler;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/8.
 */
public interface BaseHandler {

    void handle(String line,String hostName,String logTime,String version);

}
