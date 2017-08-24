package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.StrategyTem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StrategyTemMapper {
	int deleteByPrimaryKey(String template);

	int insert(StrategyTem record);

	int insertSelective(StrategyTem record);

	StrategyTem selectByPrimaryKey(String template);

	List<StrategyTem> selectAll();

	int updateByPrimaryKeySelective(StrategyTem record);

	int updateByPrimaryKey(StrategyTem record);

	List<StrategyTem> selectByPager(@Param("startIndx") int startIndx,
									@Param("pageSize") int pageSize,
									@Param("template") String template,
									@Param("strategyId") long strategyId,
									@Param("channelId") String channelId);

	int selectCount(@Param("template") String template,
					@Param("strategyId") long strategyId,
					@Param("channelId") String channelId);
}