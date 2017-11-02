package com.feng.sauron.warning.dao;

import com.feng.sauron.warning.domain.Contact;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContactMapper {
	int deleteByPrimaryKey(Long id);

	int insert(Contact record);

	int insertSelective(Contact record);

	Contact selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Contact record);

	int updateByPrimaryKey(Contact record);

	List<Contact> selectAll();

	List<Contact> selectByPager(@Param("startIndx") int startIndx,
								@Param("pageSize") int pageSize,
								@Param("contactId") long contactId,
								@Param("mobile") String mobile,
								@Param("wechat") String wechat);

	int selectCount(@Param("contactId") long contactId,
					@Param("mobile") String mobile,
					@Param("wechat") String wechat);
}