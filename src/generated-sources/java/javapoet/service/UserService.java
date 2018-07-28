package javapoet.service;

import java.lang.Long;
import javapoet.entity.User;

interface UserService {
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
