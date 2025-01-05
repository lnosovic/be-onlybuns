package com.example.onlybuns.dto;

import com.example.onlybuns.model.Role;
import com.example.onlybuns.model.User;

public class UserViewDTO {
    private Integer id;
    private String username;
    private String name;
    private String surname;
    private String email;
    private Role role;
    private int postCount;
    private int followerCount;
    private int followingCount;
    public UserViewDTO() {}
    public UserViewDTO(Integer id, String username, String name, String surname, String email, Role role,int postCount,int followerCount,int followingCount) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
        this.postCount = postCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }
    public UserViewDTO(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.postCount = user.getPostCount();
        this.followerCount = user.getFollowerCount();
        this.followingCount = user.getFollowingCount();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    public int getPostCount() {
        return postCount;
    }
    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }
    public int getFollowerCount() {
        return followerCount;
    }
    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }
    public int getFollowingCount() {
        return followingCount;
    }
    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }
}
