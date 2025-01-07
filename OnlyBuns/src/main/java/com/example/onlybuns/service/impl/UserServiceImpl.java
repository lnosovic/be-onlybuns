package com.example.onlybuns.service.impl;

import com.example.onlybuns.dto.UserRequest;
import com.example.onlybuns.dto.UserViewDTO;
import com.example.onlybuns.model.Location;
import com.example.onlybuns.model.Role;
import com.example.onlybuns.model.User;
import com.example.onlybuns.repository.UserRepository;
import com.example.onlybuns.service.RoleService;
import com.example.onlybuns.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

        return this.userRepository.save(u);
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
                dto.setPostCount(user.getPostCount());
                dto.setFollowerCount(user.getFollowerCount());
                dto.setFollowingCount(user.getFollowingCount());
            }
        }
        return dto;
    }
}
