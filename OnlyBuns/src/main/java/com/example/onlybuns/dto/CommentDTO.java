package com.example.onlybuns.dto;

import com.example.onlybuns.model.Comment;

import java.time.LocalDateTime;

public class CommentDTO {
    public Integer id;
    public int UserId;
    public String text;
    public String creatorUsername;
    public LocalDateTime createdAt;
    public CommentDTO(){}
    public CommentDTO(Integer id, int UserId, String text, String creatorUsername, LocalDateTime createdAt) {
        this.id = id;
        this.UserId = UserId;
        this.text = text;
        this.creatorUsername = creatorUsername;
        this.createdAt = createdAt;
    }
    public CommentDTO(Comment comment){
        this.id = comment.getId();
        this.UserId = comment.getUser().getId();
        this.text = comment.getText();
        this.creatorUsername = comment.getUser().getUsername();
        this.createdAt = comment.getCreatedAt();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
