package com.example.onlybuns.service.impl;

import com.example.onlybuns.model.Role;
import com.example.onlybuns.repository.RoleRepository;
import com.example.onlybuns.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findById(Long id) {
        Role auth = this.roleRepository.getOne(id);
        return auth;
    }

    @Override
    public Role findByName(String name) {
        Role role = this.roleRepository.findByName(name);
        return role;
    }

}
