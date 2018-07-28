package com.example.service;

import com.example.entity.User;

import java.lang.Long;

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
}
