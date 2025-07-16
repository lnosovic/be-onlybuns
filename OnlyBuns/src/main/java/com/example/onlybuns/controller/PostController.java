package com.example.onlybuns.controller;

import com.example.onlybuns.dto.PostViewDTO;
import com.example.onlybuns.model.Post;
import com.example.onlybuns.service.PostService;
import com.example.onlybuns.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import com.example.onlybuns.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="api/posts")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @GetMapping
    public List<PostViewDTO> getPosts(){
        return postService.getAllPosts();
    }
    @GetMapping("/getAllUserPosts/{userId}")
    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    public List<PostViewDTO> getAllUserPosts(@PathVariable Integer userId){
        return postService.getAllUserPosts(userId);
    }
    @GetMapping("/countAllPosts")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Integer> getTotalPostsCount(){
        int count = postService.getAllPostsCount();
        return ResponseEntity.ok(count);
    }
    @GetMapping("/countLastMonthPosts")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Integer> getPostsCountInLastMonth(){
        int count = postService.getPostsCountInLastMonth();
        return ResponseEntity.ok(count);
    }
    @GetMapping("/mostLikedPostsLast7Days")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<PostViewDTO>> getTop5MostLikedPostsInLast7Days(){
        List<PostViewDTO> posts = postService.getTop5MostLikedPostsInLast7Days();
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/top10MostLikedPostsEver")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<PostViewDTO>> getTop10MostLikedPostsEver(){
        List<PostViewDTO> posts = postService.getTop10MostLikedPostsEver();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostViewDTO> getPostById(@PathVariable Integer postId) {
        try {
            PostViewDTO post = postService.getPostById(postId);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{postId}/like")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> likePost(@PathVariable Integer postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        User user = userService.findByUsername(currentPrincipalName);

        if (user == null) {
            System.out.println("Error while trying to like the post. User: '" + currentPrincipalName + "' not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }

        try {
            postService.likePost(postId, user.getId());
            return new ResponseEntity<>(HttpStatus.CREATED); // 201 Created
        } catch (RuntimeException e) {
            System.out.println("Error while trying to like post with id " + postId + " , userId: " + user.getId() + ": " + e.getMessage());
            if (e.getMessage().contains("Post not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found ako post ne postoji
            }
            // Ako je korisnik već lajkovao, tvoj servis preskače operaciju (return),
            // tako da ovde neće doći do "already liked" greške iz servisa.
            // Ako bi servis bacio izuzetak, moglo bi se rukovati i sa 409 Conflict.
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request za ostale greške
        }
    }

    @DeleteMapping("/{postId}/unlike")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> unlikePost(@PathVariable Integer postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        User user = userService.findByUsername(currentPrincipalName);

        if (user == null) {
            System.out.println("Failed while trying to remove like from the post. User:  '" + currentPrincipalName + "' not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }

        try {
            postService.unlikePost(postId, user.getId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (RuntimeException e) {
            System.out.println("Error unliking the post with id: " + postId + " by user with id: " + user.getId() + ": " + e.getMessage());
            if (e.getMessage().contains("Post not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
            }
            // Tvoj servis već proverava postojanje lajka pre brisanja, tako da ovde ne bi trebalo da dođe do "lajk ne postoji" greške iz servisa
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }

    @GetMapping("/{postId}/isLikedByUser")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Boolean> isPostLikedByUser(@PathVariable Integer postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        User user = userService.findByUsername(currentPrincipalName);

        if (user == null) {
            System.out.println("Pokušaj provere statusa lajka neuspešan: Korisnik '" + currentPrincipalName + "' not found.");
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND); // 404 Not Found, vraćamo false jer korisnik nije ni pronađen
        }

        try {
            boolean isLiked = postService.isPostLikedByUser(postId, user.getId());
            return new ResponseEntity<>(isLiked, HttpStatus.OK); // 200 OK
        } catch (RuntimeException e) {
            System.out.println("Greška prilikom provere statusa lajka za objavu " + postId + " od strane korisnika " + user.getId() + ": " + e.getMessage());
            if (e.getMessage().contains("Post not found")) {
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND); // 404 Not Found, vraćamo false
            }
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
    @GetMapping("/nearby")
    public List<PostViewDTO> getNearbyPosts(@RequestParam double lat, @RequestParam double lon, @RequestParam double radius){
        return postService.getNearbyPosts(lat,lon,radius);
    }

    @PatchMapping("/{id}")
   // @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostViewDTO> updateDescription(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String newDescription = body.get("description");
        if (newDescription == null) {
            return ResponseEntity.badRequest().build();
        }

        PostViewDTO updatedPostDTO = postService.updateDescription(id, newDescription);
        return ResponseEntity.ok(updatedPostDTO);
    }


    @DeleteMapping("/{id}")
   // @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

}
