package com.feng.sauron.warning.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.feng.sauron.warning.util.TopologyUtils;
import com.feng.sauron.warning.web.vo.TopologyData;

@Service("TopologyServiceImpl")
public class TopologyServiceImpl implements TopologyService {

	@Override
	public TopologyData load(String appName, String host, Date startTime, Date endTime) {

		TopologyData topologyData = null;
		try {
			topologyData = TopologyUtils.get(appName, host, startTime, endTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return topologyData;
	}
}
