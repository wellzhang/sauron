package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.Metrics;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MetricsMapper {
	int deleteByPrimaryKey(Long id);

	int insert(Metrics record);

	int insertSelective(Metrics record);

	Metrics selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Metrics record);

	int updateByPrimaryKey(Metrics record);

	void batchInsert(@Param("list") List<Metrics> record);

	List<Metrics> selectByEventId(@Param("eventId") long eventId);

}