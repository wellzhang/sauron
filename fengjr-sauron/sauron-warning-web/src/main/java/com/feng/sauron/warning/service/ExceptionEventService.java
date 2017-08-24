package com.feng.sauron.warning.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.dao.ExceptionEventMapper;
import com.feng.sauron.warning.domain.ExceptionEvent;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年1月15日 下午4:58:58
 * 
 */

@Service
@Transactional
public class ExceptionEventService {

	@Resource
	ExceptionEventMapper exceptionEventMapper;

	@Transactional
	public ResponseDTO<Integer> insertSelective(ExceptionEvent exceptionEvent) {

		ResponseDTO<Integer> responseDTO = new ResponseDTO<Integer>(ReturnCode.ACTIVE_FAILURE);

		try {
			int insertSelective = exceptionEventMapper.insertSelective(exceptionEvent);
			if (insertSelective == 1) {
				responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
				responseDTO.setAttach(insertSelective);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDTO;
	}

}
