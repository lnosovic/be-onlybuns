package com.example.onlybuns.controller;

import com.example.onlybuns.dto.PostViewDTO;
import com.example.onlybuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
