package com.example.onlybuns.dto;

import com.example.onlybuns.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
    private LocationDTO address;
    private boolean isActivated = false;
    private List<UserDTO> followers;
    private List<UserDTO> following;
    private List<Integer> posts;

    public UserDTO() {
    }
    public UserDTO(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.address = new LocationDTO(user.getAddress());

    }

    public UserDTO(Integer id, String username, String password, String name, String surname, String email, LocationDTO address) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.address = address;

    }
}
