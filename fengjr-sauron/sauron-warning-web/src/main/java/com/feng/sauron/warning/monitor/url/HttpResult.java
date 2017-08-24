package com.feng.sauron.warning.monitor.url;

/**
 * Created by xubiao.fan on 2016/5/11.
 */
public class HttpResult {

    public HttpResult(int statusCode, String result) {

        this.statusCode = statusCode;
        this.result = result;
    }

    private int statusCode;

    private String result;


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {

        return "HttpResult{" +
                "statusCode=" + statusCode +
                ", result='" + result + '\'' +
                '}';
    }
}
