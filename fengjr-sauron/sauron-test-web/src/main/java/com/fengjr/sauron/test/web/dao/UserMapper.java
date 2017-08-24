package com.fengjr.sauron.test.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fengjr.sauron.test.web.domain.User;

public interface UserMapper {

	int deleteByPrimaryKey(Long id);

	int insert(User record);

	User selectByPrimaryKey(Long id);

	List<User> selectAll();

	int updateByPrimaryKey(User record);

	List<User> selectByPage(@Param(value = "startIndx") int startIndex, @Param(value = "pageSize") int pageSize);

}