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
    @Cacheable("5mostLikedPostsInLast7Days")
    List<PostViewDTO> getTop5MostLikedPostsInLast7Days();
    @Cacheable("mostLikedPostsEver")
    List<PostViewDTO> getTop10MostLikedPostsEver();
    @CacheEvict(cacheNames = {"5mostLikedPostsInLast7Days","mostLikedPostsEver"}, allEntries = true)
    void removeFromCache();
    PostViewDTO getPostById(Integer postId);

    // --- Nove metode za lajkovanje ---
    void likePost(Integer postId, Integer userId);
    void unlikePost(Integer postId, Integer userId);
    boolean isPostLikedByUser(Integer postId, Integer userId);
    // ---------------------------------
    List<PostViewDTO> getNearbyPosts(double lat, double lon, double radius);
    void deletePost(Long postId);
    PostViewDTO updateDescription(Long postId, String newDescription);
}
