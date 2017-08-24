package com.feng.sauron.warning.service.base;

import com.feng.sauron.warning.dao.AppMapper;
import com.feng.sauron.warning.domain.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AppService
 * Created by jianzhang
 */
@Service
public class AppService {

    @Autowired
    private AppMapper appMapper;

    public void addApp(App app){
        appMapper.insert(app);
    }

    public void delApp(long id){
        appMapper.deleteByPrimaryKey(id);
    }

    public App findAppById(long id){
        return appMapper.selectByPrimaryKey(id);
    }

    public void updateApp(App app){
        appMapper.updateByPrimaryKey(app);
    }

    public List<App> findByPage(int pageNo, int pageSize, String appName, String userId) {
        pageNo = pageNo - 1 > 0 ? pageNo - 1 : 0;
        List<App> list = appMapper.selectByPage(pageNo * pageSize,pageSize,appName,userId);
//        Set<String> set = new HashSet<String>();
//        for(App app:list){
//            set.add(app.getName());
//        }
//        List<App> result = new ArrayList<App>();
//        for(String name:set){
//            for(App app :list){
//                if(app.getName().equals(name)){
//                    result.add(app);
//                    break;
//                }
//            }
//        }
//        return  result;
        return list;
    }

    public int findCount(String appName) {
        return appMapper.selectCount(appName);

    }

    public App selectByUserId(long userId){
        return appMapper.selectByUserId(userId);
    }

}
