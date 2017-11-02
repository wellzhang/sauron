package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.ZookeeperIps;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZookeeperIpsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ZookeeperIps record);

    int insertSelective(ZookeeperIps record);

    ZookeeperIps selectByPrimaryKey(Long id);
    
	List<ZookeeperIps> selectAll();

    int updateByPrimaryKeySelective(ZookeeperIps record);

    int updateByPrimaryKey(ZookeeperIps record);

    List<ZookeeperIps> selectByPage(@Param(value = "startIndx") int startIndex,
                                    @Param(value = "pageSize") int pageSize);


}