package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.RuleReceiversKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RuleReceiversMapper {
	int deleteByPrimaryKey(RuleReceiversKey key);

	int insert(RuleReceiversKey record);

	int insertSelective(RuleReceiversKey record);

	List<RuleReceiversKey> selectByRuleId(@Param(value = "ruleId") Long ruleId, @Param(value = "type") byte type);

	void deleteByRuleId(@Param(value = "ruleId") Long ruleId, @Param(value = "type") byte type);

}