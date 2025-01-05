package com.example.onlybuns.repository;

import com.example.onlybuns.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
}
