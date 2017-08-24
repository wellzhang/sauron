package com.fengjr.sauron.dao;

import com.fengjr.sauron.dao.model.MetricsOriData;
import com.fengjr.sauron.dao.model.TraceAppShip;

import java.util.List;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/1.
 */
public interface TraceAppShipDao {

    void insert(TraceAppShip traceAppShip);

    List<TraceAppShip> getTraceAppShip(String traceId);

    void insertBatch(List<TraceAppShip> traceAppShips);
}
