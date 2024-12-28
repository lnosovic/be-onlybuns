package com.example.onlybuns.service;

import com.example.onlybuns.model.Role;

import java.util.List;

public interface RoleService {
    Role findById(Long id);
    Role findByName(String name);
}
