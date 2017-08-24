package com.feng.sauron.warning.service.base;

import com.feng.sauron.warning.dao.UserMapper;
import com.feng.sauron.warning.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jian.zhang on 2016/6/21.
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;


    public void addUser(User user){
        userMapper.insert(user);
    }

    public void delUser(String ids){
        String idArr [] = ids.split(";");
        for(String id : idArr){
            userMapper.deleteByPrimaryKey(Long.valueOf(id));
        }

    }

    public List<User> selectAll() {
        return userMapper.selectAll();
    }

    public User findUserById(long id){
        return userMapper.selectByPrimaryKey(id);
    }

    public void updateUser(User user){
        userMapper.updateByPrimaryKey(user);
    }

    public List<User> findByPage(int pageNo, int pageSize) {
        pageNo = pageNo - 1 > 0 ? pageNo - 1 : 0;
        return userMapper.selectByPage(pageNo * pageSize,pageSize);
    }

}
