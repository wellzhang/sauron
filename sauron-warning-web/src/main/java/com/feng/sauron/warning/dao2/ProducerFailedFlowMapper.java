package com.feng.sauron.warning.dao2;

import java.util.List;

import com.feng.sauron.warning.domain.ProducerFailedFlow;

public interface ProducerFailedFlowMapper {
	int deleteByPrimaryKey(Long id);

	int insert(ProducerFailedFlow record);

	int insertSelective(ProducerFailedFlow record);

	ProducerFailedFlow selectByPrimaryKey(Long id);

	List<ProducerFailedFlow> selectWarning();

	int updateByPrimaryKeySelective(ProducerFailedFlow record);

	int updateByPrimaryKey(ProducerFailedFlow record);
}