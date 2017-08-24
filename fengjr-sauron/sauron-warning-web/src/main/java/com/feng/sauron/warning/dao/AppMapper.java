package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.App;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppMapper {
    int deleteByPrimaryKey(Long id);

    int insert(App record);

    int insertSelective(App record);

    App selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(App record);

    int updateByPrimaryKey(App record);

    List<App> selectByPage(@Param(value = "startIndx") int startIndex,
                            @Param(value = "pageSize") int pageSize,
                            @Param(value = "appName") String appName,
                           @Param(value = "userId") String userId);

    int selectCount(@Param(value = "appName") String appName);

    App selectByUserId(Long userId);
}