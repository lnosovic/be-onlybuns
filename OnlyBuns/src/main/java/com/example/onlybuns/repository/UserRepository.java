package com.example.onlybuns.repository;

import com.example.onlybuns.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
