/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fengjr.sauron.dao.hbase.vo;

/**
 *
 * @author xubiao.fan@fengjr.com
 * @param <V>
 */
public class LimitedScanResult<V> {

    private long lastStartTime;

    private String lastTranSeq;

    private V data;

    public V getScanData() {
        return data;
    }

    public void setScanData(V scanData) {
        this.data = scanData;
    }

    public long getLastStartTime() {
        return lastStartTime;
    }

    public void setLastStartTime(long lastStartTime) {
        this.lastStartTime = lastStartTime;
    }

    public String getLastTranSeq() {
        return lastTranSeq;
    }

    public void setLastTranSeq(String lastTranSeq) {
        this.lastTranSeq = lastTranSeq;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LimitedScanResult{");
        sb.append("lastStartTime=").append(lastStartTime);
        sb.append("lastTranSeq=").append(lastTranSeq);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
