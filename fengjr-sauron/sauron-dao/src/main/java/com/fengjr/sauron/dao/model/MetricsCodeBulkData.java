package com.fengjr.sauron.dao.model;

import com.fengjr.sauron.commons.utils.SauronConstants;
import com.fengjr.sauron.dao.hbase.buffer.AutomaticBuffer;
import com.fengjr.sauron.dao.hbase.buffer.Buffer;
import com.fengjr.sauron.dao.hbase.buffer.OffsetFixedBuffer;

import java.io.Serializable;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/1.
 */
public class MetricsCodeBulkData implements Serializable {

    private String traceId;

    private long logTime;

    private String lineNumber;

    private String hostName;

    private String result;

    private int duration;

    private String appName;

    private String methodName;

    private String isSuccess;

    private String key;

    private String type;

    private String version;


    public Tracer getaTracerAdapter() {
        return aTracerAdapter;
    }

    public void setaTracerAdapter(Tracer aTracerAdapter) {
        this.aTracerAdapter = aTracerAdapter;
    }

    private Tracer aTracerAdapter;




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

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getKey() {
        return key;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public byte[] writeValue() {
        final Buffer buffer = new AutomaticBuffer(256);

        buffer.putPrefixedString(version);
        buffer.put(logTime);
        buffer.put(duration);
        buffer.putPrefixedString(lineNumber);
        buffer.putPrefixedString(hostName);
        buffer.putPrefixedString(result);
        buffer.putPrefixedString(methodName);
        buffer.putPrefixedString(isSuccess);
        buffer.putPrefixedString(key);
        buffer.putPrefixedString(type);

        return buffer.getBuffer();
    }

    public int readValue(byte[] bytes, int offset){

        final Buffer buffer = new OffsetFixedBuffer(bytes, offset);
        this.version = buffer.readPrefixedString();
        switch (version) {
            case SauronConstants.SAURON_V3:
                logTime     = buffer.readLong();
                duration    = buffer.readInt();
                lineNumber  = buffer.readPrefixedString();
                hostName    = buffer.readPrefixedString();
                result      = buffer.readPrefixedString();
                methodName  = buffer.readPrefixedString();
                isSuccess   = buffer.readPrefixedString();
                key         = buffer.readPrefixedString();
                type        = buffer.readPrefixedString();
            default :
                break;
        }

        return buffer.getOffset();
    }

    @Override
    public String toString() {
        return "MetricsCodeBulkData{" +
                "triceId='" + traceId + '\'' +
                ", logTime=" + logTime +
                ", lineNumber='" + lineNumber + '\'' +
                ", hostName='" + hostName + '\'' +
                ", result='" + result + '\'' +
                ", duration=" + duration +
                ", appName='" + appName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", isSuccess='" + isSuccess + '\'' +
                ", key='" + key + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
