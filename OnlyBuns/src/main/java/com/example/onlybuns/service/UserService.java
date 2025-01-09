package com.example.onlybuns.service;

import com.example.onlybuns.dto.UserRequest;
import com.example.onlybuns.dto.UserViewDTO;
import com.example.onlybuns.model.User;

import java.util.List;

public interface UserService {
    User findById(Integer id);
    User findByUsername(String username);
    List<User> findAll ();
    User save(UserRequest userRequest);
    User updateUser(User updatedUser);
    UserViewDTO getUserById(Integer id);
    UserViewDTO getUserByUsername(String username);
    List<UserViewDTO> getFollowingUsers(Integer userId);
    List<UserViewDTO> getFollowerUsers(Integer userId);
}
