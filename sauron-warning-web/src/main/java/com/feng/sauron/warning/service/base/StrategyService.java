package com.feng.sauron.warning.service.base;

import com.feng.sauron.warning.dao.StrategyMapper;
import com.feng.sauron.warning.domain.Strategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Liuyb on 2015/12/31.
 */
@Service
public class StrategyService {
    @Autowired
    StrategyMapper strategyMapper;

    public List<Strategy> selectAll() {
        return strategyMapper.selectAll();
    }

    public List<Strategy> findByPager(int pageNo, int pageSize, long id, String name) {
        pageNo = pageNo - 1 > 0 ? pageNo - 1 : 0;
        return strategyMapper.selectByPager(pageNo * pageSize, pageSize, id, name);
    }

    public int selectCount(long id, String name) {
        return strategyMapper.selectCount(id, name);
    }

    @Transactional
    public void addNewStrategy(Strategy strategy) {
        strategyMapper.insert(strategy);
    }

    @Transactional
    public void removceStrategyById(long id) {
        strategyMapper.deleteByPrimaryKey(id);
    }

    public Strategy findById(long id) {
        return strategyMapper.selectByPrimaryKey(id);
    }

    public void updateStrategy(Strategy strategy) {
        strategyMapper.updateByPrimaryKeySelective(strategy);
    }
}
