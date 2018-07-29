package com.example.generated.service.impl;

import com.example.generated.entity.User;
import com.example.generated.repository.UserRepository;
import com.example.generated.service.UserService;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    public void setuserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
    public User updateUser(User user) throws Exception {
        User userObject = userRepository.findById(user.getId()).orElseThrow(() -> new Exception("对象不存在"));
        userObject.setName(user.getName());
        userObject.setCreatedAt(user.getCreatedAt());
        return userObject;
    }

    /**
     * 创建对象
     */
    @Override
    public User createUser(User user) {
        User userObject = new User();
        userObject.setName(user.getName());
        userObject.setCreatedAt(user.getCreatedAt());
        return userObject;
    }

    /**
     * 对象分页列表
     */
    @Override
    public List<User> paginateUsers(Integer page, Integer size, String sort, String[] sortby) {
        return userRepository.findAll();
    }
}
