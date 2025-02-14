package com.example.onlybuns.service;

import com.example.onlybuns.dto.PostViewDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface PostService {
    List<PostViewDTO> getAllPosts();
    List<PostViewDTO> getAllUserPosts(Integer userId);
    int getAllPostsCount();
    int getPostsCountInLastMonth();
    @Cacheable(cacheNames = "5mostLikedPostsInLast7Days")
    List<PostViewDTO> getTop5MostLikedPostsInLast7Days();
    @Cacheable(cacheNames = "mostLikedPostsEver")
    List<PostViewDTO> getTop10MostLikedPostsEver();
    @CacheEvict(cacheNames = {"5mostLikedPostsInLast7Days","mostLikedPostsEver"}, allEntries = true)
    void removeFromCache();
}
