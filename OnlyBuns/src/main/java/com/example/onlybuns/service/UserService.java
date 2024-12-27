package com.example.onlybuns.service;

import com.example.onlybuns.model.User;
import com.example.onlybuns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public List<User> findAll() {return userRepository.findAll();}
}
