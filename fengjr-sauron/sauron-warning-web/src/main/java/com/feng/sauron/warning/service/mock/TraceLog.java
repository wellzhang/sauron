package com.feng.sauron.warning.service.mock;

import java.util.Map;

/**
 * Created by lianbin.wang on 2016/11/16.
 */
public class TraceLog {
    private String parent;
    private int indent;

    /**
     * traceId : 1479203906841^0f00aff35a2
     * spanId : 0.1.1
     * appName : sauron
     * source : sauron
     * type : Redis
     * detail : Redis
     * methodName : r.c.j.BinaryClient.set(byte[],byte[])
     * result : 0
     * exception :
     * tracer : {"time":"0"}
     * params : {"String_0":"wangweitest"}
     */

    private String traceId;
    private String spanId;
    private String appName;
    private String source;
    private String type;
    private String detail;
    private String methodName;
    private String result;
    private String exception;
    private Tracer tracer;
    private Map<String,Object> params;

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Tracer getTracer() {
        return tracer;
    }

    public void setTracer(Tracer tracer) {
        this.tracer = tracer;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public static class Tracer {
        /**
         * time : 0
         */

        private String time;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

}
