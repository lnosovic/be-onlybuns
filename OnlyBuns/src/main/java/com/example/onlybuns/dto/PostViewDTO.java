package com.example.onlybuns.dto;

import com.example.onlybuns.model.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostViewDTO {
    private Integer id;
    private Integer userId;
    private String description;
    private String image;
    private LocationDTO location;
    private LocalDateTime timeOfPublishing;
    private int likes;
    private List<CommentDTO> comments;

    public PostViewDTO() {
    }

    public PostViewDTO(Integer id, Integer userId, String description, String image, LocationDTO location, LocalDateTime timeOfPublishing, int likes, List<CommentDTO> comments) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.image = image;
        this.location = location;
        this.timeOfPublishing = timeOfPublishing;
        this.likes = likes;
        this.comments = comments;
    }
    public PostViewDTO(Post post){
        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.description = post.getDescription();
        this.image = post.getImage();
        this.location = new LocationDTO(post.getLocation());
        this.timeOfPublishing = post.getTimeOfPublishing();
        this.likes = post.getLikesCount();
        this.comments = post.getComments().stream().map(CommentDTO::new).toList();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public LocalDateTime getTimeOfPublishing() {
        return timeOfPublishing;
    }

    public void setTimeOfPublishing(LocalDateTime timeOfPublishing) {
        this.timeOfPublishing = timeOfPublishing;
    }

    public int getLikes() {
        return likes;
    }
    public void setLikes(int likes) {
        this.likes = likes;
    }
    public List<CommentDTO> getComments() {
        return comments;
    }
    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }
}
