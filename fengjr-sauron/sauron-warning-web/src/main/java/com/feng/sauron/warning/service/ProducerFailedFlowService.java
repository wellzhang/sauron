package com.feng.sauron.warning.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.feng.sauron.warning.dao2.ProducerFailedFlowMapper;
import com.feng.sauron.warning.domain.ProducerFailedFlow;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年1月20日 下午2:38:26
 * 
 */

@Service
public class ProducerFailedFlowService {

	@Resource
	ProducerFailedFlowMapper producerFailedFlowMapper;

	public List<ProducerFailedFlow> selectWarning() {

		return producerFailedFlowMapper.selectWarning();
	}
}
