package com.example.onlybuns.service.impl;

import com.example.onlybuns.dto.CommentDTO;
import com.example.onlybuns.dto.LocationDTO;
import com.example.onlybuns.dto.PostViewDTO;
import com.example.onlybuns.model.Post;
import com.example.onlybuns.repository.PostRepository;
import com.example.onlybuns.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.View;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    private final Logger LOG = LoggerFactory.getLogger(PostServiceImpl.class);
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
            postViewDTO.setComments(post.getComments().stream().map(CommentDTO::new).toList());
            postDTOs.add(postViewDTO);
        }
        return postDTOs;
    }
    @Override
    public List<PostViewDTO> getAllUserPosts(Integer userId){
        List<Post> posts = postRepository.findAll();
        List<PostViewDTO> dto = new ArrayList<>();
        for(Post post:posts){
            if(post.getUser().getId().equals(userId)){
                PostViewDTO postViewDTO = new PostViewDTO();
                postViewDTO.setId(post.getId());
                postViewDTO.setUserId(post.getUser().getId());
                postViewDTO.setDescription(post.getDescription());
                postViewDTO.setImage(post.getImage());
                LocationDTO locationDTO = new LocationDTO(post.getLocation());
                postViewDTO.setLocation(locationDTO);
                postViewDTO.setTimeOfPublishing(post.getTimeOfPublishing());
                postViewDTO.setLikes(post.getLikesCount());
                postViewDTO.setComments(post.getComments().stream().map(CommentDTO::new).toList());
                dto.add(postViewDTO);
            }
        }
        return dto;
    }
    @Override
    public int getAllPostsCount(){
        return postRepository.countTotalPosts();
    }
    @Override
    public int getPostsCountInLastMonth(){
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
        return postRepository.countPostsInLastMonth(lastMonth);
    }
    @Override
    public List<PostViewDTO> getTop5MostLikedPostsInLast7Days(){
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Post> posts = postRepository.findTop5MostLikedPostsInLast7Days(sevenDaysAgo);
        List<PostViewDTO> dto = new ArrayList<>();
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
            postViewDTO.setComments(post.getComments().stream().map(CommentDTO::new).toList());
            dto.add(postViewDTO);
        }
        return dto;
    }
    @Override
    public List<PostViewDTO>  getTop10MostLikedPostsEver(){
        List<Post> posts = postRepository.findTop10MostLikedPostsEver();
        List<PostViewDTO> dto = new ArrayList<>();
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
            postViewDTO.setComments(post.getComments().stream().map(CommentDTO::new).toList());
            dto.add(postViewDTO);
        }
        return dto;
    }
    public void removeFromCache() {
        LOG.info("Products removed from cache!");

    }
}
