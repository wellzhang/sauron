package com.feng.sauron.warning.service.base;

import com.feng.sauron.warning.dao.DubboRulesMapper;
import com.feng.sauron.warning.dao.RuleReceiversMapper;
import com.feng.sauron.warning.domain.DubboRules;
import com.feng.sauron.warning.domain.RuleReceiversKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DubboRulesService
 * Created by jianzhang
 */
@Service
public class DubboRulesService {

    @Autowired
    private DubboRulesMapper dubboRulesMapper;

    @Autowired
    private RuleReceiversMapper ruleReceiversMapper;

    public void addDubboRules(DubboRules dubboRules){
        dubboRulesMapper.insert(dubboRules);
    }

    @Transactional
    public void delDubboRules(String ids){
        String ruleIds[] = ids.split(";");
        for(String ruleId:ruleIds){
            dubboRulesMapper.deleteByPrimaryKey(Long.valueOf(ruleId));
            ruleReceiversMapper.deleteByRuleId(Long.valueOf(ruleId),RuleReceiversKey.Type.Dubbo.val());
        }
    }

    public DubboRules findDubboRulesById(long id){
        return dubboRulesMapper.selectByPrimaryKey(id);
    }

    public void updateDubboRules(DubboRules dubboRules){
        dubboRulesMapper.updateByPrimaryKey(dubboRules);
    }

    public List<Map<String,Object>> findByPage(int pageNo, int pageSize, String appName, String applicationName,String  creatorId) {
        pageNo = pageNo - 1 > 0 ? pageNo - 1 : 0;
        return dubboRulesMapper.selectByPage(pageNo * pageSize,pageSize,appName,applicationName,creatorId);
    }

    public int findCount(String appName,String applicationName,String  creatorId) {
        return dubboRulesMapper.selectCount(appName,applicationName,creatorId);

    }

	public DubboRules selectByzkid(Long zkid, String applicationName) {

		return dubboRulesMapper.selectByzkid(zkid, applicationName);
	}

    @Transactional
    public void enableOrDisableUrlRule(String ids,int status){
        String dubboIds[] = ids.split(";");
        DubboRules dubboRules = null;
        for(String ruleId:dubboIds){
            dubboRules = new DubboRules();
            dubboRules.setId(Long.valueOf(ruleId));
            dubboRules.setIsEnabled((byte)status);
            dubboRulesMapper.updateByPrimaryKeySelective(dubboRules);
        }
    }

    @Transactional
    public void addOrUpdateDubboRules(DubboRules dubboRules,String contacts,String contactType,boolean isExist){
        if(isExist){
            dubboRulesMapper.updateByPrimaryKeySelective(dubboRules);
            ruleReceiversMapper.deleteByRuleId(dubboRules.getId(), RuleReceiversKey.Type.Dubbo.val());
        }else{
            dubboRulesMapper.insertSelective(dubboRules);
        }
        if(StringUtils.isNotBlank(contacts)){
            if(StringUtils.isBlank(contactType)){
                contactType = "0";
            }
            String[] contactArr = contacts.split(";");
            RuleReceiversKey ruleReceiversKey = null;
            long urlRuleId = dubboRules.getId();
            for(String contact:contactArr){
                ruleReceiversKey = new RuleReceiversKey();
                ruleReceiversKey.setRuleId(urlRuleId);
                ruleReceiversKey.setType(RuleReceiversKey.Type.Dubbo.val());
                ruleReceiversKey.setContactId(Long.parseLong(contact));
                ruleReceiversKey.setNotifyMode(contactType);
                ruleReceiversMapper.insert(ruleReceiversKey);
            }
        }
    }

}
