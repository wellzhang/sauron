package com.feng.sauron.warning.service.base;

import com.feng.sauron.warning.dao.UrlTraceFailedMapper;
import com.feng.sauron.warning.domain.UrlTraceFailed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UrlTraceFailedService
 * Created by jianzhang
 */
@Service
public class UrlTraceFailedService {
    @Autowired
    private UrlTraceFailedMapper urlTraceFailedMapper;

    public void addUrlTraceFailed(UrlTraceFailed urlTraceFailed){
        urlTraceFailedMapper.insert(urlTraceFailed);
    }

    public void delUrlTraceFailed(long id){
        urlTraceFailedMapper.deleteByPrimaryKey(id);
    }

    public UrlTraceFailed findUrlTraceFailedById(long id){
        return urlTraceFailedMapper.selectByPrimaryKey(id);
    }


    public List<UrlTraceFailed> findByPage(int pageNo, int pageSize, long urlMonitorId) {
        pageNo = pageNo - 1 > 0 ? pageNo - 1 : 0;
        return urlTraceFailedMapper.selectByPage(pageNo * pageSize,pageSize,urlMonitorId);
    }
}
