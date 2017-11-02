package com.feng.sauron.warning.service;

import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.dao.WarningEventMapper;
import com.feng.sauron.warning.domain.WarningEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2015年12月18日 上午10:25:36
 * 
 */

@Service
@Transactional
public class WarningEventService {

	@Resource
	WarningEventMapper warningEventMapper;

	
	@Transactional
	public ResponseDTO<Integer> insertSelective(WarningEvent warningEvent) {

		ResponseDTO<Integer> responseDTO = new ResponseDTO<Integer>(ReturnCode.ACTIVE_FAILURE);

		try {
			int insertSelective = warningEventMapper.insertSelective(warningEvent);
			if (insertSelective == 1) {
				responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
				responseDTO.setAttach(insertSelective);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDTO;
	}

	public List<WarningEvent> selectByPager(int pageNo, int pageSize, String appName, String methodName,
											String hostName, String instantName) {
		pageNo = pageNo - 1 > 0 ? pageNo - 1 : 0;
		return warningEventMapper.selectByPager(pageNo * pageSize, pageSize, appName, methodName, hostName, instantName);
	}

	public int selectCount(String appName, String methodName,
						   String hostName, String instantName) {
		return warningEventMapper.selectCount(appName, methodName, hostName, instantName);

	}

}
