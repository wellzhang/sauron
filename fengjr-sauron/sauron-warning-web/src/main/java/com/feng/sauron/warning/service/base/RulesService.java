package com.feng.sauron.warning.service.base;

import com.feng.sauron.warning.dao.RulesMapper;
import com.feng.sauron.warning.domain.RuleReceiversKey;
import com.feng.sauron.warning.domain.Rules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Liuyb on 2015/12/10.
 */
@Service
public class RulesService {
    @Autowired
    private RulesMapper rulesMapper;

    public Rules getRuleById(long id) {
        Rules rule = rulesMapper.selectByPrimaryKey(id);
        return rule;
    }

    public List<Rules> getAllRules() {
        List<Rules> rulesList = rulesMapper.selectAll(Rules.TypeEnum.Function.val());
        return rulesList;
    }

    public void updateRule(Rules rule) {
        rulesMapper.updateByPrimaryKeySelective(rule);
    }

    public void addNewRule(Rules rule) {
        rulesMapper.insertSelective(rule);
    }

    public List<Rules> findByPager(int pageNo, int pageSize, String hostName, String appName, String methodName, String  creatorId,byte type) {
        pageNo = pageNo - 1 > 0 ? pageNo - 1 : 0;
        return rulesMapper.selectByPager(pageNo * pageSize, pageSize, hostName, appName, methodName,creatorId,type);

    }

    public int findCount(String hostName, String appName, String methodName,String  creatorId,byte type) {
        return rulesMapper.selectCount(hostName, appName, methodName,creatorId, type);

    }

    public List<Rules> findConstomByPager(int pageNo, int pageSize, String hostName, String appName, String methodName, String  creatorId) {
        pageNo = pageNo - 1 > 0 ? pageNo - 1 : 0;
        return rulesMapper.selectCustomByPager(pageNo * pageSize, pageSize, hostName, appName, methodName,creatorId);

    }

    public int findConstomCount(String hostName, String appName, String methodName,String  creatorId) {
        return rulesMapper.selectCustomCount(hostName, appName, methodName,creatorId);

    }

    public void delRule(long ruleId) {
        rulesMapper.deleteByPrimaryKey(ruleId);
    }

    public List<Rules> findByStrategyId(long strategyId, byte type) {
        return rulesMapper.selectByStrategyId(strategyId,type);
    }
}
