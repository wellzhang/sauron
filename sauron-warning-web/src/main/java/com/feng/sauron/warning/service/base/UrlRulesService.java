package com.feng.sauron.warning.service.base;

import com.feng.sauron.warning.dao.RuleReceiversMapper;
import com.feng.sauron.warning.dao.UrlMonitorMapper;
import com.feng.sauron.warning.dao.UrlRulesMapper;
import com.feng.sauron.warning.domain.App;
import com.feng.sauron.warning.domain.RuleReceiversKey;
import com.feng.sauron.warning.domain.UrlRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UrlRulesService
 * Created by jianzhang
 */
@Service
public class UrlRulesService {

    @Autowired
    private UrlRulesMapper urlRulesMapper;

    @Autowired
    private UrlMonitorMapper urlMonitorMapper;

    @Autowired
    private RuleReceiversMapper ruleReceiversMapper;

    public void addUrlRules(UrlRules urlRules){
        urlRulesMapper.insert(urlRules);
    }

    public void addSelectiveRules(UrlRules urlRules){
        urlRulesMapper.insertSelective(urlRules);
    }

    @Transactional
    public void delUrlRules(long id){
        urlRulesMapper.deleteByPrimaryKey(id);
        urlMonitorMapper.deleteByUrlRuleId(id);
        ruleReceiversMapper.deleteByRuleId(id, RuleReceiversKey.Type.Url.val());

    }

    public UrlRules findUrlRulesById(long id){
        return urlRulesMapper.selectByPrimaryKey(id);
    }

    public void updateUrlRules(UrlRules urlRules){
        urlRulesMapper.updateByPrimaryKeySelective(urlRules);
    }

    public List<UrlRules> findByPage(int pageNo, int pageSize, String appName,String monitorKey,String creatorId) {
        pageNo = pageNo - 1 > 0 ? pageNo - 1 : 0;
        return urlRulesMapper.selectByPage(pageNo * pageSize,pageSize,appName,monitorKey,creatorId);
    }

    public int findCount(String appName,String monitorKey,String creatorId) {
        return urlRulesMapper.selectCount(appName,monitorKey,creatorId);

    }

}
