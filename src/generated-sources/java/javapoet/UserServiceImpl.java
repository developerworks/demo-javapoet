package javapoet;

import java.lang.Long;

import org.springframework.stereotype.Service;

@Service
class UserServiceImpl implements UserService {
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
}
