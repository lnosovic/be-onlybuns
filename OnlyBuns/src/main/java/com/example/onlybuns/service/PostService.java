package com.example.onlybuns.service;

import com.example.onlybuns.dto.PostViewDTO;

import java.util.List;

public interface PostService {
    List<PostViewDTO> getAllPosts();
    List<PostViewDTO> getAllUserPosts(Integer userId);
    int getAllPostsCount();
    int getPostsCountInLastMonth();
    List<PostViewDTO> getTop5MostLikedPostsInLast7Days();
    List<PostViewDTO> getTop10MostLikedPostsEver();
}
