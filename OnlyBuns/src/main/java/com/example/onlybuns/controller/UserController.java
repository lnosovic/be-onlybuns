package com.example.onlybuns.controller;

import com.example.onlybuns.dto.LocationDTO;
import com.example.onlybuns.dto.UserDTO;
import com.example.onlybuns.dto.UserSearchCriteria;
import com.example.onlybuns.dto.UserRequest;
import com.example.onlybuns.dto.UserViewDTO;
import com.example.onlybuns.mapper.UserDTOMapper;
import com.example.onlybuns.model.Location;
import com.example.onlybuns.model.User;
import com.example.onlybuns.security.auth.FollowRateLimiter;
import com.example.onlybuns.service.LocationService;
import com.example.onlybuns.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private FollowRateLimiter followRateLimiter;
    @Autowired
    private LocationService locationService;
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


    @PostMapping("/{followedId}/follow") // POST request to follow a user
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Only registered users can follow
    public ResponseEntity<String> followUser(@PathVariable Integer followedId) {
        // Get the username of the currently logged-in user from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Get the follower user object
        User follower = userService.findByUsername(currentUsername);

        // Basic check if follower exists and not following self
        if (follower == null) {
            return new ResponseEntity<>("Follower user not found.", HttpStatus.NOT_FOUND); // 404 Not Found
        }
        if (follower.getId().equals(followedId)) {
            return new ResponseEntity<>("Cannot follow yourself.", HttpStatus.BAD_REQUEST); // 400 Bad Request
        }

        //rate limiter check
        if (!followRateLimiter.isAllowed(follower.getId().longValue())) {
            return new ResponseEntity<>("You have reached the follow limit (max 50 per minute).",
                    HttpStatus.TOO_MANY_REQUESTS); // 429
        }

        try {
            userService.followUser(follower.getId(), followedId);
            return new ResponseEntity<>("Successfully followed user " + followedId, HttpStatus.CREATED); // 201 Created
        } catch (RuntimeException e) {
            // Catch any runtime exceptions from the service layer
            System.err.println("ctrl: Error following user " + followedId + " by " + follower.getId() + ": " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request for any other service error
        }
    }

    @DeleteMapping("/{followedId}/unfollow") // DELETE request to unfollow a user
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Only registered users can unfollow
    public ResponseEntity<String> unfollowUser(@PathVariable Integer followedId) {
        // Get the username of the currently logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User follower = userService.findByUsername(currentUsername);

        // Basic check if follower exists and not unfollowing self
        if (follower == null) {
            return new ResponseEntity<>("Follower user not found.", HttpStatus.NOT_FOUND); // 404 Not Found
        }
        if (follower.getId().equals(followedId)) {
            return new ResponseEntity<>("Cannot unfollow yourself.", HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
        try {
            userService.unfollowUser(follower.getId(), followedId);
            return new ResponseEntity<>("Successfully unfollowed user " + followedId, HttpStatus.NO_CONTENT); // 204 No Content
        } catch (RuntimeException e) {
            // Catch any runtime exceptions from the service layer
            System.err.println("Error unfollowing user " + followedId + " by " + follower.getId() + ": " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request for any other service error
        }
    }

    @GetMapping("/{followedId}/isFollowing") // GET request to check if current user is following another user
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Only registered users can check
    public ResponseEntity<Boolean> isFollowing(@PathVariable Integer followedId) {
        // Get the username of the currently logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Get the follower user object
        User follower = userService.findByUsername(currentUsername);

        // If follower user is not found, cannot be following anyone
        if (follower == null) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND); // 404 Not Found
        }

        try {
            boolean isFollowing = userService.isFollowing(follower.getId(), followedId);
            return new ResponseEntity<>(isFollowing, HttpStatus.OK); // 200 OK
        } catch (RuntimeException e) {
            // Catch any runtime exceptions from the service layer
            System.err.println("Error checking follow status for user " + followedId + " by " + follower.getId() + ": " + e.getMessage());
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> updateUser(@PathVariable Integer id,
                                           @RequestBody UserRequest userRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User u = (User) authentication.getPrincipal();
        System.out.println("u.getEmail():"+u.getEmail());
        System.out.println("userRequest,getEmail():"+userRequest.getEmail());
        if(!u.getEmail().equals(userRequest.getEmail())){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(id != userRequest.getId())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        User user = userService.updateByUserId(id,userRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Only registered users can check
    public Page<UserViewDTO> searchUsers(UserSearchCriteria criteria) {
        return userService.searchUsers(criteria);
    }

}
