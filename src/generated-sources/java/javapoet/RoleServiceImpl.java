package javapoet;

import java.lang.Long;
import org.springframework.stereotype.Service;

@Service
class RoleServiceImpl implements RoleService {
    /**
     * 通过ID获取用户对象
     */
    public Role getRole(Long id) {
        Role role = new Role();
        return role;
    }

    /**
     * 更新对象
     */
    public Role updateRole(Role role) {
        return role;
    }

    /**
     * 创建对象
     */
    public Role createRole(Role role) {
        return role;
    }
}
