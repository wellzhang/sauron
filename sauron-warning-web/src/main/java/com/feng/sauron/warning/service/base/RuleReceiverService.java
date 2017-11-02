package com.feng.sauron.warning.service.base;

import com.feng.sauron.warning.dao.RuleReceiversMapper;
import com.feng.sauron.warning.domain.RuleReceiversKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Liuyb on 2015/12/10.
 */
@Service
public class RuleReceiverService {
    @Autowired
    RuleReceiversMapper ruleReceiversMapper;

    @Transactional(readOnly = true)
    public List<RuleReceiversKey> findReceiversByRule(long rule_id,byte type) {
        return ruleReceiversMapper.selectByRuleId(rule_id,type);
    }

    @Transactional
    public void addReceiversToRule(List<RuleReceiversKey> receivers) {
        for (RuleReceiversKey receiver : receivers) {
            ruleReceiversMapper.insert(receiver);
        }
    }

    @Transactional
    public void delReceiversByRuleId(long ruleId,byte type) {
        ruleReceiversMapper.deleteByRuleId(ruleId,type);
    }


}
