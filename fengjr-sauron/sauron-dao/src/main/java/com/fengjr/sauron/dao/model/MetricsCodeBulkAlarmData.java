package com.fengjr.sauron.dao.model;

import com.fengjr.sauron.commons.utils.SauronConstants;
import com.fengjr.sauron.dao.hbase.buffer.AutomaticBuffer;
import com.fengjr.sauron.dao.hbase.buffer.Buffer;
import com.fengjr.sauron.dao.hbase.buffer.OffsetFixedBuffer;

import java.io.Serializable;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/1.
 */
public class MetricsCodeBulkAlarmData  implements Serializable {

    private String traceId;

    private long logTime;

    private String result;

    private String lineNumber;

    private String key;

    private String hostName;

    private String appName;

    private String methodName;

    private String type;

    private String version;



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public long getLogTime() {
        return logTime;
    }

    public void setLogTime(long logTime) {
        this.logTime = logTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public byte[] writeValue() {

        final Buffer buffer = new AutomaticBuffer(256);

        buffer.putPrefixedString(version);
        buffer.put(logTime);
        buffer.putPrefixedString(result);
        buffer.putPrefixedString(lineNumber);
        buffer.putPrefixedString(key);
        buffer.putPrefixedString(hostName);
        buffer.putPrefixedString(methodName);
        buffer.putPrefixedString(type);
        return buffer.getBuffer();
    }



    public int readValue(byte[] bytes, int offset){

        final Buffer buffer = new OffsetFixedBuffer(bytes, offset);
        this.version = buffer.readPrefixedString();
        switch (version) {
            case SauronConstants.SAURON_V3:
                logTime     = buffer.readLong();
                result      = buffer.readPrefixedString();
                lineNumber  = buffer.readPrefixedString();
                key         = buffer.readPrefixedString();
                hostName    = buffer.readPrefixedString();
                methodName  = buffer.readPrefixedString();
                type        = buffer.readPrefixedString();
            default :
                break;
        }

        return buffer.getOffset();
    }

    @Override
    public String toString() {
        return "MetricsCodeBulkAlarmData{" +
                "triceId='" + traceId + '\'' +
                ", logTime=" + logTime +
                ", result='" + result + '\'' +
                ", lineNumber='" + lineNumber + '\'' +
                ", key='" + key + '\'' +
                ", hostName='" + hostName + '\'' +
                ", appName='" + appName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
