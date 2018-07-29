package com.example.generated.service;

import com.example.generated.entity.User;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.List;

public interface UserService {
    /**
     * 通过ID获取用户对象
     */
    User getUser(Long id);

    /**
     * 更新对象
     */
    User updateUser(User user) throws Exception;

    /**
     * 创建对象
     */
    User createUser(User user);

    /**
     * 对象分页列表
     */
    List<User> paginateUsers(Integer page, Integer size, String sort, String[] sortby);
}
