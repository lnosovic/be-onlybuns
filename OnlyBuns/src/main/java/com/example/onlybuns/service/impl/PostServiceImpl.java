package com.example.onlybuns.service.impl;

import com.example.onlybuns.dto.CommentDTO;
import com.example.onlybuns.dto.LocationDTO;
import com.example.onlybuns.dto.PostViewDTO;
import com.example.onlybuns.model.Like;
import com.example.onlybuns.model.Post;
import com.example.onlybuns.model.User;
import com.example.onlybuns.repository.LikeRepository;
import com.example.onlybuns.repository.PostRepository;
import com.example.onlybuns.repository.UserRepository;
import com.example.onlybuns.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private LikeRepository likeRepository;

    @Autowired // Ubaci UserRepository (za dohvaćanje User entiteta)
    private UserRepository userRepository;
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


    @Override
    public PostViewDTO getPostById(Integer postId) {
        // Sada koristimo .get() direktno
        Post post = postRepository.findById(postId).get();

        PostViewDTO postViewDTO = new PostViewDTO();
        postViewDTO.setId(post.getId());
        postViewDTO.setUserId(post.getUser().getId());
        postViewDTO.setDescription(post.getDescription());
        postViewDTO.setImage(post.getImage());
        LocationDTO locationDTO = new LocationDTO(post.getLocation());
        postViewDTO.setLocation(locationDTO);
        postViewDTO.setTimeOfPublishing(post.getTimeOfPublishing());
        postViewDTO.setLikes(post.getLikesCount());
        if (post.getComments() != null) {
            postViewDTO.setComments(post.getComments().stream().map(CommentDTO::new).toList());
        } else {
            postViewDTO.setComments(new ArrayList<>());
        }

        return postViewDTO;
    }

    @Override
    @Transactional // Važno: Operacije koje menjaju bazu podataka treba da budu transakcione
    public void likePost(Integer postId, Integer userId) {
        // 1. Proveri da li post postoji
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        // 2. Proveri da li korisnik postoji
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 3. Proveri da li korisnik već lajkuje ovaj post
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            LOG.info("Korisnik {} već lajkuje objavu {}. Preskačem operaciju.", userId, postId);
            return; // Korisnik je već lajkovao, nema potrebe da se radi ništa
        }

        // 4. Kreiraj novi Like entitet
        Like like = new Like(post, user);

        // 5. Sačuvaj lajk u bazi
        likeRepository.save(like);
        LOG.info("Korisnik {} lajkovao objavu {}.", userId, postId);
    }

    @Override
    @Transactional // Važno: Operacije koje menjaju bazu podataka treba da budu transakcione
    public void unlikePost(Integer postId, Integer userId) {
        // 1. Proveri da li post postoji (opciono, ali dobra praksa)
        // Iako deleteByPostIdAndUserId neće baciti grešku ako ne nađe, dobro je potvrditi postojanje
        postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Objava nije pronađena sa ID-em: " + postId));

        // 2. Proveri da li korisnik postoji (opciono, ali dobra praksa)
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen sa ID-em: " + userId));

        // 3. Pokušaj da obrišeš lajk
        // Ova metoda iz LikeRepository će obrisati red ako postoji.
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        LOG.info("Korisnik {} je uklonio lajk sa objave {}.", userId, postId);
    }

    @Override
    public boolean isPostLikedByUser(Integer postId, Integer userId) {
        // Jednostavno proveravamo da li lajk postoji u bazi
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

}
