package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.Strategy;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StrategyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Strategy record);

    int insertSelective(Strategy record);

    Strategy selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Strategy record);

    int updateByPrimaryKey(Strategy record);

    List<Strategy> selectAll();

    List<Strategy> selectByPager(@Param("startIndx") int startIndx,
                                 @Param("pageSize") int pageSize,
                                 @Param("strategyId") long id,
                                 @Param("stgyName") String stgyName);

    int selectCount(
            @Param("strategyId") long id,
            @Param("stgyName") String stgyName
    );
}