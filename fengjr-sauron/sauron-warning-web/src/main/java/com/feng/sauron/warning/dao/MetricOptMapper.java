package com.feng.sauron.warning.dao;

import java.util.List;

import com.feng.sauron.warning.domain.MetricOpt;

public interface MetricOptMapper {
	int deleteByPrimaryKey(Long id);

	int insert(MetricOpt record);

	int insertSelective(MetricOpt record);

	MetricOpt selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(MetricOpt record);

	int updateByPrimaryKey(MetricOpt record);

	List<MetricOpt> selectAllMetricsOpt(Integer status);
}