package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.Rules;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RulesMapper {
	int deleteByPrimaryKey(Long id);

	int insert(Rules record);

	int insertSelective(Rules record);

	Rules selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Rules record);

	int updateByPrimaryKey(Rules record);

	List<Rules> selectAll(byte type);


	/**
	 *
	 * @param startIndex
	 * @param pageSize
	 * @param hostName
	 * @param appName
	 * @param methodName
	 * @return
	 */
	List<Rules> selectByPager(@Param(value = "startIndx") int startIndex,
							  @Param(value = "pageSize") int pageSize,
							  @Param(value = "hostName") String hostName,
							  @Param(value = "appName") String appName,
							  @Param(value = "methodName") String methodName,
							  @Param(value = "creatorId") String  creatorId,
							  @Param(value = "type") byte type);

	/**
	 * @param hostName
	 * @param appName
	 * @param methodName
	 * @return
	 */
	int selectCount(@Param(value = "hostName") String hostName,
					@Param(value = "appName") String appName,
					@Param(value = "methodName") String methodName,
					@Param(value = "creatorId") String  creatorId,
					@Param(value = "type") byte type);

	List<Rules> selectByStrategyId(@Param(value = "strategyId") long strategyId,@Param(value = "type") byte type);


	/**
	 *
	 * @param startIndex
	 * @param pageSize
	 * @param hostName
	 * @param appName
	 * @param methodName
	 * @return
	 */
	List<Rules> selectCustomByPager(@Param(value = "startIndx") int startIndex,
							  @Param(value = "pageSize") int pageSize,
							  @Param(value = "hostName") String hostName,
							  @Param(value = "appName") String appName,
							  @Param(value = "methodName") String methodName,
							  @Param(value = "creatorId") String  creatorId);

	/**
	 * @param hostName
	 * @param appName
	 * @param methodName
	 * @return
	 */
	int selectCustomCount(@Param(value = "hostName") String hostName,
					@Param(value = "appName") String appName,
					@Param(value = "methodName") String methodName,
					@Param(value = "creatorId") String  creatorId);



}