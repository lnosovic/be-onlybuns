package com.example.onlybuns.model;

import java.time.LocalDateTime;

public class Post {
    private Integer id;
    private String description;
    private String image;
    private Location location;
    private LocalDateTime timeOfPublishing;

    public Post(){}

    public Post(Integer id, String description, String image, Location location, LocalDateTime timeOfPublishing) {
        this.id = id;
        this.description = description;
        this.image = image;
        this.location = location;
        this.timeOfPublishing = timeOfPublishing;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDateTime getTimeOfPublishing() {
        return timeOfPublishing;
    }

    public void setTimeOfPublishing(LocalDateTime timeOfPublishing) {
        this.timeOfPublishing = timeOfPublishing;
    }
}
