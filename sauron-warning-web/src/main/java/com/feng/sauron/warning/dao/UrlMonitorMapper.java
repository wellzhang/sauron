package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.UrlMonitor;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UrlMonitorMapper {
    int deleteByPrimaryKey(Long id);

    int deleteByUrlRuleId(Long rulesId);

    int insert(UrlMonitor record);

    int insertSelective(UrlMonitor record);

    UrlMonitor selectByPrimaryKey(Long id);

    UrlMonitor selectByUrlRuleId (Long rulesId);

    int updateByPrimaryKeySelective(UrlMonitor record);

    int updateByPrimaryKey(UrlMonitor record);

    List<Map<String,Object>> selectByPage(@Param(value = "startIndex") int startIndex,
                           @Param(value = "pageSize") int pageSize,
                           @Param(value = "appName") String appName,
                           @Param(value = "monitorKey") String monitorKey,
                                          @Param(value = "creatorId") String creatorId);

    int selectCount(@Param(value = "appName") String appName,
                    @Param(value = "monitorKey") String monitorKey,
                    @Param(value = "creatorId") String creatorId);

    int updateByUrlRuleId(UrlMonitor record);

}