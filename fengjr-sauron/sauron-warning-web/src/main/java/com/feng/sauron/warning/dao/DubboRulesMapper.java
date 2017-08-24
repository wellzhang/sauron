package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.DubboRules;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DubboRulesMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DubboRules record);

    int insertSelective(DubboRules record);

    DubboRules selectByPrimaryKey(Long id);
    
	List<DubboRules> selectAll();

    int updateByPrimaryKeySelective(DubboRules record);

    int updateByPrimaryKey(DubboRules record);

    List<Map<String,Object>> selectByPage(@Param(value = "startIndex") int startIndex,
                                          @Param(value = "pageSize") int pageSize,
                                          @Param(value = "appName") String appName,
                                          @Param(value = "applicationName") String applicationName,
                                          @Param(value = "creatorId") String  creatorId);

    int selectCount(@Param(value = "appName") String appName,
                    @Param(value = "applicationName") String applicationName,@Param(value = "creatorId") String  creatorId);
    
    
    DubboRules selectByzkid(@Param(value = "zookeeperIpsId") Long zookeeperIpsId,@Param(value = "applicationName") String applicationName);
}