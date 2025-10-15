package com.example.onlybuns;

import com.example.onlybuns.dto.UserViewDTO;
import com.example.onlybuns.model.Location;
import com.example.onlybuns.model.Role;
import com.example.onlybuns.model.User;
import com.example.onlybuns.repository.LocationRepository;
import com.example.onlybuns.repository.RoleRepository;
import com.example.onlybuns.repository.UserRepository;
import com.example.onlybuns.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class IntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LocationRepository locationRepository;

    private User user1;
    private User user2;
    private Role roleUser;
    private Location location1;
    private Location location2;

    @BeforeEach
    void setup() {
        // Čistimo sve tabele
        userRepository.deleteAll();
        roleRepository.deleteAll();
        locationRepository.deleteAll();

        // Kreiramo role
        roleUser = new Role();
        roleUser.setName("ROLE_USER");
        roleUser = roleRepository.save(roleUser);

        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");
        roleAdmin = roleRepository.save(roleAdmin);

        // Kreiramo lokacije
        location1 = new Location();
        location1.setCountry("Serbia");
        location1.setCity("Belgrade");
        location1 = locationRepository.save(location1);

        location2 = new Location();
        location2.setCountry("Serbia");
        location2.setCity("Novi Sad");
        location2 = locationRepository.save(location2);

        // Kreiramo korisnike
        user1 = new User();
        user1.setUsername("johndoe");
        user1.setName("John");
        user1.setSurname("Doe");
        user1.setEmail("john@example.com");
        user1.setPassword("password123");
        user1.setRole(roleUser);
        user1.setAddress(location1);
        user1.setFollowers(new HashSet<>());
        user1.setFollowings(new HashSet<>());
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setUsername("janedoe");
        user2.setName("Jane");
        user2.setSurname("Doe");
        user2.setEmail("jane@example.com");
        user2.setPassword("password123");
        user2.setRole(roleUser);
        user2.setAddress(location2);
        user2.setFollowers(new HashSet<>());
        user2.setFollowings(new HashSet<>());
        user2 = userRepository.save(user2);
    }

    @Test
    void testGetUserById_UserExists() {
        UserViewDTO found = userService.getUserById(user1.getId());
        assertEquals("johndoe", found.getUsername());
        assertEquals("John", found.getName());
        assertEquals("Doe", found.getSurname());
        assertEquals("ROLE_USER", found.getRole().getName());
        assertEquals("Serbia", found.getLocation().getCountry());
        assertEquals("Belgrade", found.getLocation().getCity());
    }

    @Test
    void testGetUserById_UserDoesNotExist() {
        UserViewDTO result = userService.getUserById(999);
        assertNull(result.getId());
        assertNull(result.getUsername());
        assertNull(result.getEmail());
        assertEquals(0, result.getPostCount());
        assertEquals(0, result.getFollowerCount());
        assertEquals(0, result.getFollowingCount());
    }

    @Test
    void testFollowUser_Success() {
        userService.followUser(user1.getId(), user2.getId());
        User updatedFollower = userRepository.findById(user1.getId()).orElseThrow();
        User updatedFollowed = userRepository.findById(user2.getId()).orElseThrow();
        assertTrue(updatedFollower.getFollowings().contains(updatedFollowed));
    }

    @Test
    void testFollowUser_AlreadyFollowing() {
        userService.followUser(user1.getId(), user2.getId());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.followUser(user1.getId(), user2.getId());
        });
        assertTrue(exception.getMessage().contains("already following"));
    }

    @Test
    void testUnfollowUser_Success() {
        userService.followUser(user1.getId(), user2.getId());
        userService.unfollowUser(user1.getId(), user2.getId());
        User updatedFollower = userRepository.findById(user1.getId()).orElseThrow();
        assertFalse(updatedFollower.getFollowings().contains(user2));
    }
}
