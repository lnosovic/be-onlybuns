package com.example.onlybuns.controller;

import com.example.onlybuns.dto.UserDTO;
import com.example.onlybuns.dto.UserViewDTO;
import com.example.onlybuns.model.User;
import com.example.onlybuns.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping(value="/all")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<User> users = userService.findAll();
        List<UserDTO> usersDTO = new ArrayList<>();
        for(User user : users){
            usersDTO.add(new UserDTO(user));
        }
        return new ResponseEntity<>(usersDTO, HttpStatus.OK);

    }
    @GetMapping("/profile/{id}")
    public UserViewDTO getUserInfo(@PathVariable Integer id){return this.userService.getUserById(id);}
    @GetMapping("/userInfo")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public UserViewDTO user(Principal user){return this.userService.getUserByUsername(user.getName());}
    @GetMapping("/following/{userId}")
    public List<UserViewDTO> getFollowing(@PathVariable Integer userId){
        return userService.getFollowingUsers(userId);
    }
    @GetMapping("/followers/{userId}")
    public List<UserViewDTO> getFollowers(@PathVariable Integer userId){
        return userService.getFollowerUsers(userId);
    }
    @GetMapping("/top10MostUserLikes")
    public ResponseEntity<List<UserViewDTO>> getTop10MostUserLikesInLast7Days(){
        List<UserViewDTO> users = userService.getTop10MostUserLikesInLast7Days();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
