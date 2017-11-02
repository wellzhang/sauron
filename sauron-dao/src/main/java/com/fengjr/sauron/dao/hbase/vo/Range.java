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



import java.util.concurrent.TimeUnit;

/**
 * @author  xubiao.fan@fengjr.com
 */
public final class Range {
    private final long from;
    private final long to;
    private final String fromTranSeq ;

    public Range(long from, long to) {
        this.from = from;
        this.to = to;
        this.fromTranSeq = null;
        validate();
    }
    public Range(long from, long to,String fromTranSeq) {
        this.from = from;
        this.to = to;
        this.fromTranSeq = fromTranSeq;
        validate();
    }

    public Range(long from, long to, boolean check) {
        this.from = from;
        this.to = to;
        this.fromTranSeq = null;
        if (check) {
            validate();
        }
    }

    public static Range createUncheckedRange(long from, long to) {
        return new Range(from, to, false);
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    public long getRange() {
        return to - from;
    }

    public String getFromTranSeq() {
        return fromTranSeq;
    }

    public void validate() {
        if (this.to < this.from) {
            throw new IllegalArgumentException("invalid range:" + this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range range = (Range) o;

        if (from != range.from) return false;
        if (to != range.to) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (from ^ (from >>> 32));
        result = 31 * result + (int) (to ^ (to >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Range{" +
                "from=" + from +
                ", to=" + to +
                ", fromTranSeq=" + fromTranSeq +
                ", range=" + getRange() +
                '}';
    }

}
