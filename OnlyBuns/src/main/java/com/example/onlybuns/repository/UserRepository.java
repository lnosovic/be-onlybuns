package com.example.onlybuns.repository;

import com.example.onlybuns.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    @Query("SELECT u FROM User u JOIN u.followers f WHERE f.id=:userId")
    List<User> findFollowingUsers(@Param("userId")Integer userId);
    @Query("SELECT u FROM User u JOIN u.followings f WHERE f.id=:userId")
    List<User> findFollowerUsers(@Param("userId")Integer userId);
}
