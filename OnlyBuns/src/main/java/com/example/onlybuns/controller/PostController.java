package com.example.onlybuns.controller;

import com.example.onlybuns.dto.PostViewDTO;
import com.example.onlybuns.model.Post;
import com.example.onlybuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="api/posts")
public class PostController {
    @Autowired
    private PostService postService;
    @GetMapping
    public List<PostViewDTO> getPosts(){
        return postService.getAllPosts();
    }
    @GetMapping("/getAllUserPosts/{userId}")
    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    public List<PostViewDTO> getAllUserPosts(@PathVariable Integer userId){
        return postService.getAllUserPosts(userId);

    }
    @GetMapping("/countAllPosts")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Integer> getTotalPostsCount(){
        int count = postService.getAllPostsCount();
        return ResponseEntity.ok(count);
    }
    @GetMapping("/countLastMonthPosts")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Integer> getPostsCountInLastMonth(){
        int count = postService.getPostsCountInLastMonth();
        return ResponseEntity.ok(count);
    }
    @GetMapping("/mostLikedPostsLast7Days")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<PostViewDTO>> getTop5MostLikedPostsInLast7Days(){
        List<PostViewDTO> posts = postService.getTop5MostLikedPostsInLast7Days();
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/top10MostLikedPostsEver")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<PostViewDTO>> getTop10MostLikedPostsEver(){
        List<PostViewDTO> posts = postService.getTop10MostLikedPostsEver();
        return ResponseEntity.ok(posts);
    }
}
