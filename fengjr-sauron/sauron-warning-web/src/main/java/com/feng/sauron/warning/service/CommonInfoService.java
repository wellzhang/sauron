package com.feng.sauron.warning.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.dao.ContactMapper;
import com.feng.sauron.warning.domain.Contact;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年12月18日 上午10:25:36
 * 
 */

@Service
public class CommonInfoService {
	@Resource
	ContactMapper contactMapper;

	public ResponseDTO<Integer> insertContact(Contact contact) {

		ResponseDTO<Integer> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_FAILURE);

		try {
			int insertSelective = contactMapper.insertSelective(contact);
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
			responseDTO.setAttach(insertSelective);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseDTO;
	}

}
