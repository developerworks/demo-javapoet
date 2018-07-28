package com.example.service;

import com.example.entity.User;

import java.util.List;

public interface UserService {
    /**
     * 通过ID获取用户对象
     */
    User getUser(Long id);

    /**
     * 更新对象
     */
    User updateUser(User user);

    /**
     * 创建对象
     */
    User createUser(User user);

    /**
     * 对象分页列表
     */
    List<User> paginateUsers(Integer page, Integer size);
}
