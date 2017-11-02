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

package com.fengjr.sauron.dao.util;


import com.fengjr.sauron.dao.hbase.buffer.AutomaticBuffer;
import com.fengjr.sauron.dao.hbase.buffer.Buffer;
import com.fengjr.sauron.dao.hbase.buffer.FixedBuffer;
import com.fengjr.sauron.dao.model.TransactionId;

/**
 * @author xubiao.fan@fengjr.com
 */
public final class TransactionIdUtils {
    // value is displayed as html - should not use html syntax
    public static final String TRANSACTION_ID_DELIMITER = "^";
    public static final byte VERSION = 0;

    private TransactionIdUtils() {
    }

    public static String formatString(String appName, String traceId) {
        if (appName == null) {
            throw new NullPointerException("agentId must not be null");
        }
        StringBuilder sb = new StringBuilder(64);
        sb.append(appName);
        sb.append(TRANSACTION_ID_DELIMITER);
        sb.append(traceId);
        return sb.toString();
    }

    public static String formatString(String agentId, long agentStartTime, String transactionSequence) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        StringBuilder sb = new StringBuilder(64);
        sb.append(agentId);
        sb.append(TRANSACTION_ID_DELIMITER);
        sb.append(agentStartTime);
        sb.append(TRANSACTION_ID_DELIMITER);
        sb.append(transactionSequence);
        return sb.toString();
    }

//    public static byte[] formatBytes(String appName, long agentStartTime, String transactionSequence) {
//        // agentId may be null
//        //version + prefixed size + string + long + long
//        final Buffer buffer = new AutomaticBuffer(1 + 5 + 24 + 10 + 10);
//        buffer.put(VERSION);
//        buffer.putPrefixedString(appName);
//        buffer.putVar(agentStartTime);
//        buffer.putPrefixedString(transactionSequence);
//        return buffer.getBuffer();
//    }
//
//    public static byte[] formatBytes(String appName, long agentStartTime) {
//        // agentId may be null
//        //version + prefixed size + string + long + long
//        final Buffer buffer = new AutomaticBuffer(1 + 5 + 24 + 10 + 10);
//        buffer.put(VERSION);
//        buffer.putPrefixedString(appName);
//        buffer.putVar(agentStartTime);
//        return buffer.getBuffer();
//    }
//
//
//
//    public static TransactionId parseTransactionId(final byte[] transactionId) {
//        if (transactionId == null) {
//            throw new NullPointerException("transactionId must not be null");
//        }
//        final Buffer buffer = new FixedBuffer(transactionId);
//        final byte version = buffer.readByte();
//        if (version != VERSION) {
//            throw new IllegalArgumentException("invalid Version");
//        }
//
//        final String appName = buffer.readPrefixedString();
//        final long startTime = buffer.readVarLong();
//        final String transactionSequence = buffer.readPrefixedString();
//        if (appName == null) {
//            return new TransactionId(startTime, transactionSequence);
//        } else {
//            return new TransactionId(appName, startTime,transactionSequence);
//        }
//    }

    public static TransactionId parseTransactionId(final String transactionId) {
        if (transactionId == null) {
            throw new NullPointerException("transactionId must not be null");
        }

        final int agentIdIndex = nextIndex(transactionId, 0);
        if (agentIdIndex == -1) {
            throw new IllegalArgumentException("agentIndex not found:" + transactionId);
        }
        final String agentId = transactionId.substring(0, agentIdIndex);

        final int agentStartTimeIndex = nextIndex(transactionId, agentIdIndex + 1);
        if (agentStartTimeIndex == -1) {
            throw new IllegalArgumentException("startTimeIndex not found:" + transactionId);
        }
        final long agentStartTime = parseLong(transactionId, agentIdIndex + 1, agentStartTimeIndex);

        int transactionSequenceIndex = nextIndex(transactionId, agentStartTimeIndex + 1);
        if (transactionSequenceIndex == -1) {
            // next index may not exist since default value does not have a delimiter after transactionSequence.
            // may need fixing when id spec changes 
            transactionSequenceIndex = transactionId.length();
        }
        final String transactionSequence = parseStr(transactionId, agentStartTimeIndex + 1, transactionSequenceIndex);
        return new TransactionId(agentId, agentStartTime, transactionSequence);
    }

    private static int nextIndex(String transactionId, int fromIndex) {
        return transactionId.indexOf(TRANSACTION_ID_DELIMITER, fromIndex);
    }

    private static long parseLong(String transactionId, int beginIndex, int endIndex) {
        final String longString = transactionId.substring(beginIndex, endIndex);
        try {
            return Long.parseLong(longString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("parseLong Error. " + longString + " transactionId:" + transactionId);
        }
    }

    private static String parseStr(String transactionId, int beginIndex, int endIndex) {
        final String string = transactionId.substring(beginIndex, endIndex);
        return string;
    }



}
