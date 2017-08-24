package com.fengjr.sauron.converger.kafka.decoder;

import java.text.ParseException;
import java.util.Map;

/**
 * Created by songlin on 2015/8/28.
 */
public interface MsgDecoder {

    Map<String, Object> decodeMsg(String line) throws ParseException;

    boolean filterLog(String log);
}
