package com.example.onlybuns.service;

import com.example.onlybuns.dto.PostViewDTO;

import java.util.List;

public interface PostService {
    List<PostViewDTO> getAllPosts();
}
