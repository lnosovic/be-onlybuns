package com.example.onlybuns.service.impl;

import com.example.onlybuns.dto.LocationDTO;
import com.example.onlybuns.dto.UserRequest;
import com.example.onlybuns.dto.UserSearchCriteria;
import com.example.onlybuns.dto.UserViewDTO;
import com.example.onlybuns.exception.ResourceConflictException;
import com.example.onlybuns.model.Location;
import com.example.onlybuns.model.Role;
import com.example.onlybuns.model.User;
import com.example.onlybuns.repository.UserRepository;
import com.example.onlybuns.service.RoleService;
import com.example.onlybuns.service.UserService;
import com.example.onlybuns.specification.UserSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;
    @Autowired
    private LocationServiceImpl locationServiceImpl;

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public User findById(Integer id) throws AccessDeniedException {
        return userRepository.findById(id).orElseGet(null);
    }

    public List<User> findAll() throws AccessDeniedException {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User save(UserRequest userRequest) {
        User u = new User();
        u.setUsername(userRequest.getUsername());

        // pre nego sto postavimo lozinku u atribut hesiramo je kako bi se u bazi nalazila hesirana lozinka
        // treba voditi racuna da se koristi isi password encoder bean koji je postavljen u AUthenticationManager-u kako bi koristili isti algoritam
        u.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        u.setName(userRequest.getName());
        u.setSurname(userRequest.getSurname());
        u.setActivated(false);
        u.setEmail(userRequest.getEmail());
        Location location =locationServiceImpl.createLocation(userRequest.getLocation());
        u.setAddress(location);
        // u primeru se registruju samo obicni korisnici i u skladu sa tim im se i dodeljuje samo rola USER
        Role role = roleService.findByName("ROLE_USER");
        u.setRole(role);

        try{
            Thread.sleep(3000); // 3 sekunde kašnjenje
            return this.userRepository.save(u);
        }catch (DataIntegrityViolationException exception){
            throw new ResourceConflictException(0, "Already exists in database (DB constraint)");
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while saving user", e);
        }
    }
    @Override
    public User updateUser(User updatedUser) throws AccessDeniedException {
        return userRepository.save(updatedUser);
    }
    @Override
    public UserViewDTO getUserById(Integer id){
        List<User> users = userRepository.findAll();
        UserViewDTO dto = new UserViewDTO();
        for (User user : users) {
            if(user.getId().equals(id)){
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setName(user.getName());
                dto.setSurname(user.getSurname());
                dto.setEmail(user.getEmail());
                dto.setRole(user.getRole());
                dto.setLocation(new LocationDTO(user.getAddress()));
                dto.setPostCount(user.getPostCount());
                dto.setFollowerCount(user.getFollowerCount());
                dto.setFollowingCount(user.getFollowingCount());
            }
        }
        return dto;
    }
    public UserViewDTO getUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        UserViewDTO dto = new UserViewDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setRole(user.getRole());
        dto.setEmail(user.getEmail());
        dto.setLocation(new LocationDTO(user.getAddress()));
        dto.setPostCount(user.getPostCount());
        dto.setFollowerCount(user.getFollowerCount());
        dto.setFollowingCount(user.getFollowingCount());
        return dto;
    }
    public UserViewDTO getUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(email);
        UserViewDTO dto = new UserViewDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setRole(user.getRole());
        dto.setEmail(user.getEmail());
        dto.setLocation(new LocationDTO(user.getAddress()));
        dto.setPostCount(user.getPostCount());
        dto.setFollowerCount(user.getFollowerCount());
        dto.setFollowingCount(user.getFollowingCount());
        return dto;
    }
    @Override
    public List<UserViewDTO> getFollowingUsers(Integer userId){
        List<User> followings = userRepository.findFollowingUsers(userId);
        return followings.stream().map(user ->{
            UserViewDTO dto = new UserViewDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setName(user.getName());
            dto.setSurname(user.getSurname());
            dto.setRole(user.getRole());
            dto.setEmail(user.getEmail());
            dto.setLocation(new LocationDTO(user.getAddress()));
            dto.setPostCount(user.getPostCount());
            dto.setFollowerCount(user.getFollowerCount());
            dto.setFollowingCount(user.getFollowingCount());
            return dto;
        }).toList();

    }
    @Override
    public List<UserViewDTO> getFollowerUsers(Integer userId){
        List<User> followers = userRepository.findFollowerUsers(userId);
        return followers.stream().map(user ->{
            UserViewDTO dto = new UserViewDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setName(user.getName());
            dto.setSurname(user.getSurname());
            dto.setRole(user.getRole());
            dto.setEmail(user.getEmail());
            dto.setLocation(new LocationDTO(user.getAddress()));
            dto.setPostCount(user.getPostCount());
            dto.setFollowerCount(user.getFollowerCount());
            dto.setFollowingCount(user.getFollowingCount());
            return dto;
        }).toList();
    }
    @Override
    public List<UserViewDTO> getTop10MostUserLikesInLast7Days(){
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<User> users = userRepository.findTop10MostUserLikesInLast7Days(sevenDaysAgo);
        List<UserViewDTO> dtos = new ArrayList<UserViewDTO>();
        for (User user : users) {
            UserViewDTO dto = new UserViewDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setName(user.getName());
            dto.setSurname(user.getSurname());
            dto.setRole(user.getRole());
            dto.setEmail(user.getEmail());
            dto.setLocation(new LocationDTO(user.getAddress()));
            dto.setPostCount(user.getPostCount());
            dto.setFollowerCount(user.getFollowerCount());
            dto.setFollowingCount(user.getFollowingCount());
            dtos.add(dto);
        }
        return dtos;
    }
    @Override
    public User getByEmail(String email)  throws UsernameNotFoundException {
        return userRepository.getUserByEmail(email);
    }


    //following
    @Override
    @Transactional()
    public void followUser(Integer followerId, Integer followedId) {
        User follower = userRepository.findById(followerId).orElse(null);
        User followed = userRepository.findById(followedId).orElse(null);

        if (follower == null || followed == null) {
            System.out.println("Follower or followed user not found.");
            return;
        }
        if (follower.getFollowings().contains(followed)) {
            // Ako već prati, bacamo izuzetak da obavestimo frontend
            throw new RuntimeException("User " + followerId + " is already following " + followedId + ".");
        }

        follower.getFollowings().add(followed);
        //followed.getFollowers().add(follower);
        //userRepository.save(follower);
        //userRepository.save(followed);
        System.out.println("User " + followerId + " is now following " + followedId + ".");

    }
    @Override
    @Transactional
    public void unfollowUser(Integer followerId, Integer followedId) {
        User follower = userRepository.findById(followerId).orElse(null);
        User followed = userRepository.findById(followedId).orElse(null);

        if (follower == null || followed == null) {
            System.out.println("Follower or followed user not found.");
            return;
        }

        follower.getFollowings().remove(followed);
//        followed.getFollowers().remove(follower);
//
//        userRepository.save(follower);
//        userRepository.save(followed);

        System.out.println("User " + followerId + " is no longer following  " + followedId + ".");
    }
    @Override
    public boolean isFollowing(Integer followerId, Integer followedId) {
        User follower = userRepository.findById(followerId).orElse(null);
        User followed = userRepository.findById(followedId).orElse(null);
        if (follower == null || followed == null) {
            return false;
        }

        return follower.getFollowings().contains(followed);
    }
    @Override
    public User updateByUserId(Integer id,UserRequest newUser)throws AccessDeniedException {
        User user = userRepository.findById(id).orElseGet(null);
        if (user == null) {
            return null;
        }
        user.setName(newUser.getName());
        user.setSurname(newUser.getSurname());
        if(!passwordEncoder.matches(newUser.getPassword(), user.getPassword())){
            System.out.println("Lozinka se promenila");
            user.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }
        Role role = roleService.findByName("ROLE_USER");
        user.setRole(role);
        Location location =locationServiceImpl.createLocation(newUser.getLocation());
        user.setAddress(location);
        return userRepository.save(user);
    }

    @Override
    public Page<UserViewDTO> searchUsers(UserSearchCriteria criteria) {
        // 1. Specification
        Specification<User> spec = UserSpecification.filterByCriteria(criteria);

        // 2. Sort
        Sort sort = Sort.by("email"); // default
        if ("followerCount".equalsIgnoreCase(criteria.getSortBy())) {
            sort = Sort.by("followerCount"); // assuming it's mapped
        }
        if ("desc".equalsIgnoreCase(criteria.getSortDirection())) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        // 3. Pageable
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);

        // 4. Query
        Page<User> users = userRepository.findAll(spec, pageable);

        // 5. DTO map
        return users.map(user -> {
            UserViewDTO dto = new UserViewDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setName(user.getName());
            dto.setSurname(user.getSurname());
            dto.setEmail(user.getEmail());
            dto.setLocation(new LocationDTO(user.getAddress()));
            dto.setPostCount(user.getPostCount());
            dto.setFollowerCount(user.getFollowerCount());
            dto.setFollowingCount(user.getFollowings().size());
            return dto;
        });
    }

}
