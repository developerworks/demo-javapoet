package com.example.service.impl;

import com.example.entity.User;
import com.example.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    /**
     * 通过ID获取用户对象
     */
    @Override
    public User getUser(Long id) {
        User user = new User();
        return user;
    }

    /**
     * 更新对象
     */
    @Override
    public User updateUser(User user) {
        return user;
    }

    /**
     * 创建对象
     */
    @Override
    public User createUser(User user) {
        return user;
    }

    /**
     * 对象分页列表
     */
    @Override
    public List<User> paginateUsers(Integer page, Integer size) {
        return null;
    }
}
