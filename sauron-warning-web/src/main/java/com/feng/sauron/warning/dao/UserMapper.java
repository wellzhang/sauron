package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(User record);

    User selectByPrimaryKey(Long id);

    List<User> selectAll();

    int updateByPrimaryKey(User record);

    List<User> selectByPage(@Param(value = "startIndx") int startIndex,
                                    @Param(value = "pageSize") int pageSize);

}