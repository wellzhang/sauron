package com.feng.sauron.warning.service;

import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.dao.MetricOptMapper;
import com.feng.sauron.warning.domain.MetricOpt;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年12月18日 上午10:31:29
 * 
 */

@Service
@Transactional
public class MetricOptInfoService {

	@Resource
	MetricOptMapper metricOptMapper;

	public ResponseDTO<List<MetricOpt>> selectAllMetricsOpt(Integer status) {

		ResponseDTO<List<MetricOpt>> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_FAILURE);

		try {//缓存起来。。。
			List<MetricOpt> selectAllMetricsOpt = metricOptMapper.selectAllMetricsOpt(status);
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
			responseDTO.setAttach(selectAllMetricsOpt);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseDTO;
	}

	public List<MetricOpt> selectAllMetricOpt(int status) {
		List<MetricOpt> selectAllMetricsOpt = metricOptMapper.selectAllMetricsOpt(status);
		return selectAllMetricsOpt;
	}

	public ResponseDTO<MetricOpt> selectMetricsOptById(Long id) {

		ResponseDTO<MetricOpt> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_FAILURE);

		try {
			MetricOpt selectAllMetricsOpt = metricOptMapper.selectByPrimaryKey(id);
			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
			responseDTO.setAttach(selectAllMetricsOpt);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseDTO;
	}
}
