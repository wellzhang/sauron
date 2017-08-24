package com.feng.sauron.warning.service.base;

import com.feng.sauron.warning.dao.ClausesMapper;
import com.feng.sauron.warning.domain.Clauses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Liuyb on 2015/12/10.
 */
@Service
public class ClauseService {
    @Autowired
    ClausesMapper clausesMapper;

    @Transactional(readOnly = true)
    public List<Clauses> findClausesByRuleId(long id) {
        return clausesMapper.selectClausesByRuleId(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addClausesToRule(List<Clauses> clauses) {
        for (Clauses clause : clauses) {
            clausesMapper.insert(clause);
        }
    }


    @Transactional
    public void delClausesByRelRuleId(long id) {
        clausesMapper.deleteByRelRuleId(id);
    }
}
