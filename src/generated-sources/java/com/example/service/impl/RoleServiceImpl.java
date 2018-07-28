package com.example.service.impl;

import com.example.entity.Role;
import com.example.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    /**
     * 通过ID获取用户对象
     */
    @Override
    public Role getRole(Long id) {
        Role role = new Role();
        return role;
    }

    /**
     * 更新对象
     */
    @Override
    public Role updateRole(Role role) {
        return role;
    }

    /**
     * 创建对象
     */
    @Override
    public Role createRole(Role role) {
        return role;
    }

    /**
     * 对象分页列表
     */
    @Override
    public List<Role> paginateRoles(Integer page, Integer size) {
        return null;
    }
}
