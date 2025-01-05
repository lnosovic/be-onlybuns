package com.example.onlybuns.service.impl;

import com.example.onlybuns.dto.LocationDTO;
import com.example.onlybuns.dto.PostViewDTO;
import com.example.onlybuns.model.Post;
import com.example.onlybuns.repository.PostRepository;
import com.example.onlybuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;
    @Override
    public List<PostViewDTO> getAllPosts(){
        List<Post> posts = postRepository.findAll();
        List<PostViewDTO> postDTOs = new ArrayList<>();

        posts.sort((post1,post2)->post2.getTimeOfPublishing().compareTo(post1.getTimeOfPublishing()));
        for(Post post:posts){
            PostViewDTO postViewDTO = new PostViewDTO();
            postViewDTO.setId(post.getId());
            postViewDTO.setUserId(post.getUser().getId());
            postViewDTO.setDescription(post.getDescription());
            postViewDTO.setImage(post.getImage());
            LocationDTO locationDTO = new LocationDTO(post.getLocation());
            postViewDTO.setLocation(locationDTO);
            postViewDTO.setTimeOfPublishing(post.getTimeOfPublishing());
            postViewDTO.setLikes(post.getLikesCount());
            postDTOs.add(postViewDTO);
        }
        return postDTOs;
    }
}
