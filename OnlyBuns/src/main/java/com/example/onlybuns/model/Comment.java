package com.example.onlybuns.model;

public class Comment {
    private Integer id;
    private User user;
    private String comment;

    public Comment() {
    }

    public Comment(Integer id, User user, String comment) {
        this.id = id;
        this.user = user;
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
