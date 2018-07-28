package com.example.service.impl;

import com.example.entity.User;
import com.example.service.UserService;
import java.lang.Integer;
import java.lang.Long;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    /**
     * 通过ID获取用户对象
     */
    public User getUser(Long id) {
        User user = new User();
        return user;
    }

    /**
     * 更新对象
     */
    public User updateUser(User user) {
        return user;
    }

    /**
     * 创建对象
     */
    public User createUser(User user) {
        return user;
    }

    /**
     * 对象分页列表
     */
    public List<User> paginateUser(Integer page, Integer size) {
        return user;
    }
}
