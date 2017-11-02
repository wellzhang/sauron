package com.feng.sauron.warning.dao2;

import java.util.List;

import com.feng.sauron.warning.domain.ScheduleFailedFlow;

public interface ScheduleFailedFlowMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(ScheduleFailedFlow record);

	int insertSelective(ScheduleFailedFlow record);

	ScheduleFailedFlow selectByPrimaryKey(Integer id);

	List<ScheduleFailedFlow> selectWarning();

	int updateByPrimaryKeySelective(ScheduleFailedFlow record);

	int updateByPrimaryKey(ScheduleFailedFlow record);
}