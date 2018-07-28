package javapoet;

import java.lang.Long;

interface RoleService {
    /**
     * 通过ID获取用户对象
     */
    Role getRole(Long id);

    /**
     * 更新对象
     */
    Role updateRole(Role role);

    /**
     * 创建对象
     */
    Role createRole(Role role);
}
