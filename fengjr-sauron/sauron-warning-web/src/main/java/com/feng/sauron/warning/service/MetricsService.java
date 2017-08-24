package com.feng.sauron.warning.service;

import com.feng.sauron.warning.dao.MetricsMapper;
import com.feng.sauron.warning.domain.Metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Liuyb on 2015/12/25.
 */
@Service
@Transactional
public class MetricsService {
    @Autowired
    MetricsMapper metricsMapper;

    public List<Metrics> getMetricsByEvent(long eventId) {
        return metricsMapper.selectByEventId(eventId);
    }
    
	public void insertSelective(Metrics record) {

		metricsMapper.insertSelective(record);

	}
    
    
}
