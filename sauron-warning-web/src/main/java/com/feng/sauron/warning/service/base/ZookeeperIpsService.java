package com.feng.sauron.warning.service.base;

import com.feng.sauron.warning.dao.ZookeeperIpsMapper;
import com.feng.sauron.warning.domain.ZookeeperIps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ZookeeperIpsService
 * Created by jianzhang
 */
@Service
public class ZookeeperIpsService {

    @Autowired
    private ZookeeperIpsMapper zookeeperIpsMapper;

    public void addZookeeperIps(ZookeeperIps zookeeperIps){
        zookeeperIpsMapper.insert(zookeeperIps);
    }

    public void delZookeeperIps(String ids){
        String idArr [] = ids.split(";");
        for(String id : idArr){
            zookeeperIpsMapper.deleteByPrimaryKey(Long.valueOf(id));
        }
    }

	public List<ZookeeperIps> selectAll() {
		return zookeeperIpsMapper.selectAll();
	}

    public ZookeeperIps findZookeeperIpsById(long id){
        return zookeeperIpsMapper.selectByPrimaryKey(id);
    }

    public void updateZookeeperIps(ZookeeperIps zookeeperIps){
        zookeeperIpsMapper.updateByPrimaryKey(zookeeperIps);
    }

    public List<ZookeeperIps> findByPage(int pageNo, int pageSize) {
        pageNo = pageNo - 1 > 0 ? pageNo - 1 : 0;

        return zookeeperIpsMapper.selectByPage(pageNo * pageSize,pageSize);
    }

}
