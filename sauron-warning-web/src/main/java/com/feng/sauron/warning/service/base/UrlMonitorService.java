package com.feng.sauron.warning.service.base;

import com.feng.sauron.warning.dao.UrlMonitorMapper;
import com.feng.sauron.warning.domain.UrlMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * UrlMonitorService
 * Created by jianzhang
 */
@Service
public class UrlMonitorService {
    @Autowired
    private UrlMonitorMapper urlMonitorMapper;

    public int addUrlMonitor(UrlMonitor urlMonitor){
       return urlMonitorMapper.insert(urlMonitor);
    }

    public void delUrlMonitor(long id){
        urlMonitorMapper.deleteByPrimaryKey(id);
    }

    public void delUrlMonitorByUrlRuleId(long id){
        urlMonitorMapper.deleteByUrlRuleId(id);
    }

    public UrlMonitor findUrlMonitorById(long id){
        return urlMonitorMapper.selectByPrimaryKey(id);
    }

    public void updateUrlMonitor(UrlMonitor urlMonitor){
        urlMonitorMapper.updateByPrimaryKey(urlMonitor);
    }

    public UrlMonitor findByUrlRuleId(long urlRuleId){
        return  urlMonitorMapper.selectByUrlRuleId(urlRuleId);

    }

    @Transactional
    public void updateByUrlRuleId(long urlRuleId,Integer totalTimes,Integer failTimes){
        UrlMonitor urlMonitor = urlMonitorMapper.selectByUrlRuleId(urlRuleId);
        urlMonitor.setTotalTimes(totalTimes);
        urlMonitor.setFailTimes(failTimes);
        urlMonitorMapper.updateByPrimaryKeySelective(urlMonitor);
    }

    public List<Map<String,Object>> findByPage(int pageNo, int pageSize, String appName, String monitorKey,String creatorId) {
        pageNo = pageNo - 1 > 0 ? pageNo - 1 : 0;
        return urlMonitorMapper.selectByPage(pageNo * pageSize,pageSize,appName,monitorKey,creatorId);
    }

    public int findCount(String appName,String monitorKey,String creatorId) {
        return urlMonitorMapper.selectCount(appName,monitorKey,creatorId);

    }
}
