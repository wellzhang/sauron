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

package com.fengjr.sauron.dao.model;

import com.fengjr.sauron.commons.utils.SauronConstants;
import com.fengjr.sauron.dao.hbase.buffer.Buffer;
import com.fengjr.sauron.dao.hbase.buffer.BytesUtils;
import com.fengjr.sauron.dao.hbase.buffer.FixedBuffer;
import com.fengjr.sauron.dao.util.TransactionIdUtils;


/**
 * @author
 */

public class TransactionId implements Comparable<TransactionId> {
    public static final int APP_NAME_MAX_LEN = SauronConstants.APPLICATION_NAME_MAX_LEN;
    public static final int DISTRIBUTE_HASH_SIZE = 1;

    protected final String appName;
    protected final long startTime;
    protected final String transactionSequence;

    public TransactionId(byte[] transactionId) {
        if (transactionId == null) {
            throw new NullPointerException("transactionId must not be null");
        }
        this.appName = BytesUtils.toStringAndRightTrim(transactionId, 0, SauronConstants.AGENT_NAME_MAX_LEN);
        this.startTime = BytesUtils.bytesToLong(transactionId, SauronConstants.AGENT_NAME_MAX_LEN);
        this.transactionSequence = BytesUtils.toStringAndRightTrim(transactionId, BytesUtils.LONG_BYTE_LENGTH + SauronConstants.AGENT_NAME_MAX_LEN,SauronConstants.APPLICATION_SEQ_MAX_LEN);

    }

    public TransactionId(long startTime, String transactionSequence) {
        this.appName = null;
        this.startTime = startTime;
        this.transactionSequence = transactionSequence;
    }

//    public TransactionId(byte[] transactionId, int offset) {
//        if (transactionId == null) {
//            throw new NullPointerException("transactionId must not be null");
//        }
//        if (transactionId.length < BytesUtils.LONG_LONG_BYTE_LENGTH + APP_NAME_MAX_LEN + offset) {
//            throw new IllegalArgumentException("invalid transactionId length:" + transactionId.length);
//        }
//
//        this.appName = BytesUtils.toStringAndRightTrim(transactionId, offset, APP_NAME_MAX_LEN);
//        this.startTime = BytesUtils.bytesToLong(transactionId, offset + APP_NAME_MAX_LEN);
//        this.transactionSequence = BytesUtils.bytesToLong(transactionId, offset + BytesUtils.LONG_BYTE_LENGTH + APP_NAME_MAX_LEN);
//    }

    public String getAppName() {
        return appName;
    }

    public TransactionId(String agentId, long agentStartTime, String transactionSequence) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        this.appName = agentId;
        this.startTime = agentStartTime;
        this.transactionSequence = transactionSequence;
    }

    public TransactionId(String transactionId) {
        if (transactionId == null) {
            throw new NullPointerException("transactionId must not be null");
        }

        TransactionId parsedId = TransactionIdUtils.parseTransactionId(transactionId);
        this.appName = parsedId.getAppName();
        this.startTime = parsedId.getStartTime();
        this.transactionSequence = parsedId.getTransactionSequence();
    }



    public long getStartTime() {
        return startTime;
    }

    public String getTransactionSequence() {
        return transactionSequence;
    }

    public byte[] getBytes() {
        return BytesUtils.stringLongStringToBytes(appName, SauronConstants.APPLICATION_NAME_MAX_LEN, startTime, transactionSequence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionId traceId = (TransactionId) o;

        if (startTime != traceId.startTime) return false;
        if (transactionSequence != traceId.transactionSequence) return false;
        if (!appName.equals(traceId.appName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = appName.hashCode();
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + transactionSequence.hashCode()*31;
        return result;
    }

    @Override
    public String toString() {
        String traceId = TransactionIdUtils.formatString(appName, startTime, transactionSequence);
        return "TransactionId [" + traceId + "]";
    }

    public String getFormatString() {
        return TransactionIdUtils.formatString(appName, startTime, transactionSequence);
    }

    // FIXME remove
    @Override
    public int compareTo(TransactionId transactionId) {
        int r1 = this.appName.compareTo(transactionId.appName);
        if (r1 == 0) {
            if (this.startTime > transactionId.startTime) {
                return 1;
            } else if (this.startTime < transactionId.startTime) {
                return -1;
            } else {
                if (this.transactionSequence.compareTo(transactionId.transactionSequence) == 0) {
                    return 1;
//                } else if (this.transactionSequence < transactionId.transactionSequence) {
//                    return -1;
                } else {
                    return 0;
                }
            }
        } else {
            return r1;
        }
    }



}
