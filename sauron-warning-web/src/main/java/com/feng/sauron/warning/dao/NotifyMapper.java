package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.Notify;

public interface NotifyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Notify record);

    int insertSelective(Notify record);

    Notify selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Notify record);

    int updateByPrimaryKey(Notify record);
}