package com.fengjr.sauron.dao.model;

import com.fengjr.sauron.commons.utils.SauronConstants;
import com.fengjr.sauron.dao.hbase.buffer.AutomaticBuffer;
import com.fengjr.sauron.dao.hbase.buffer.Buffer;
import com.fengjr.sauron.dao.hbase.buffer.OffsetFixedBuffer;

import java.io.Serializable;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/1.
 */
public class MetricsOriData  implements Serializable {

    private String traceId;

    private long logTime;

    private int duration;

    private String spanId;

    private String paramStr;

    private String hostName;

    private String appName;

    private String methodName;

    private String type;

    private String version;

    private String detail;

    private String result;

    private Tracer tracer;

    private String source;

    private String exception;

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public Tracer getTracer() {
        return tracer;
    }

    public void setTracer(Tracer tracer) {
        this.tracer = tracer;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getParamStr() {
        return paramStr;
    }

    public void setParamStr(String paramStr) {
        this.paramStr = paramStr;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    private Object params;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public long getLogTime() {
        return logTime;
    }

    public void setLogTime(long logTime) {
        this.logTime = logTime;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }


    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAppName() {

        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getMethodName() {

        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public byte[] writeValue(){
        final Buffer buffer = new AutomaticBuffer(256);
        buffer.putPrefixedString(version);
        buffer.put(logTime);
        buffer.put(duration);
        buffer.putPrefixedString(hostName);
        buffer.putPrefixedString(paramStr);
        buffer.putPrefixedString(methodName);
        buffer.putPrefixedString(result);
        buffer.putPrefixedString(type);
        buffer.putPrefixedString(detail);
        buffer.putPrefixedString(source);
        buffer.putPrefixedString(exception);

        return buffer.getBuffer();

    }

    public int readValue(byte[] bytes, int offset){

        final Buffer buffer = new OffsetFixedBuffer(bytes, offset);
        this.version = buffer.readPrefixedString();
        switch (version){
            case SauronConstants.SAURON_V3:
                this.logTime = buffer.readLong();
                this.duration= buffer.readInt();
                hostName     = buffer.readPrefixedString();
                paramStr     = buffer.readPrefixedString();
                methodName   = buffer.readPrefixedString();
                result       = buffer.readPrefixedString();
                type         = buffer.readPrefixedString();
                detail       = buffer.readPrefixedString();
                source       = buffer.readPrefixedString();
                exception    = buffer.readPrefixedString();
                break;
            default :
                break;
        }

        return buffer.getOffset();
    }


    @Override
    public String toString() {
        return "MetricsOriData{" +
                "triceId='" + traceId + '\'' +
                ", logTime=" + logTime +
                ", duration=" + duration +
                ", spanId='" + spanId + '\'' +
                ", paramStr='" + paramStr + '\'' +
                ", hostName='" + hostName + '\'' +
                ", invokeResult='" + result + '\'' +
                ", appName='" + appName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", detail='" + detail + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}


