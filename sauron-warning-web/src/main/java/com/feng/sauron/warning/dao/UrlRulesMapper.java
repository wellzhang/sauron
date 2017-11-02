package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.UrlRules;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * UrlRulesService
 * Created by jianzhang
 */
public interface UrlRulesMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UrlRules record);

    int insertSelective(UrlRules record);

    UrlRules selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UrlRules record);

    int updateByPrimaryKey(UrlRules record);

    List<UrlRules> selectByPage(@Param(value = "startIndex") int startIndex,
                                @Param(value = "pageSize") int pageSize,
                                @Param(value = "appName") String appName,
                                @Param(value = "monitorKey") String monitorKey,
                                @Param(value = "creatorId") String  creatorId);

    int selectCount(@Param(value = "appName") String appName,
                    @Param(value = "monitorKey") String monitorKey,@Param(value = "creatorId") String creatorId);
}