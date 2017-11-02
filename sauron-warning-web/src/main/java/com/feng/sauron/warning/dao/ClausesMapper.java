package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.Clauses;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClausesMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Clauses record);

    int insertSelective(Clauses record);

    Clauses selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Clauses record);

    int updateByPrimaryKey(Clauses record);

    List<Clauses> selectClausesByRuleId(@Param("relRuleId") Long ruleId);

    int deleteByRelRuleId(@Param("ruleId") Long ruleId);
}