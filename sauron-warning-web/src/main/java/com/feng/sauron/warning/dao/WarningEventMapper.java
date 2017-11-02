package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.WarningEvent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarningEventMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WarningEvent record);

    int insertSelective(WarningEvent record);

    WarningEvent selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WarningEvent record);

    int updateByPrimaryKey(WarningEvent record);

    List<WarningEvent> selectByPager(@Param("startIndx") int startIndx,
                                     @Param("pageSize") int pageSize,
                                     @Param("appName") String appName,
                                     @Param("methodName") String methodName,
                                     @Param("hostName") String hostName,
                                     @Param("instantName") String instantName);

    int selectCount(@Param("appName") String appName,
                    @Param("methodName") String methodName,
                    @Param("hostName") String hostName,
                    @Param("instantName") String instantName);
}