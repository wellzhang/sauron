package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.UrlTraceFailed;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UrlTraceFailedMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UrlTraceFailed record);

    int insertSelective(UrlTraceFailed record);

    UrlTraceFailed selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UrlTraceFailed record);

    int updateByPrimaryKey(UrlTraceFailed record);

    List<UrlTraceFailed> selectByPage(@Param(value = "startIndex") int startIndex,
                                      @Param(value = "pageSize") int pageSize,
                                      @Param(value = "urlMonitorId") long urlMonitorId);
}