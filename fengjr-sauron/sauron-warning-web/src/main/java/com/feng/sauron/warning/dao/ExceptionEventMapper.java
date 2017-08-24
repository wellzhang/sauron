package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.ExceptionEvent;

public interface ExceptionEventMapper {
    int insert(ExceptionEvent record);

    int insertSelective(ExceptionEvent record);
}