package com.example.onlybuns;

import com.example.onlybuns.dto.LocationDTO;
import com.example.onlybuns.dto.UserViewDTO;
import com.example.onlybuns.model.Location;
import com.example.onlybuns.model.Role;
import com.example.onlybuns.model.User;
import com.example.onlybuns.repository.UserRepository;
import com.example.onlybuns.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UnitTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserById_UserExists() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("johndoe");
        mockUser.setName("John");
        mockUser.setSurname("Doe");
        mockUser.setEmail("john@example.com");

        // Role
        Role role = new Role();
        role.setName("ROLE_USER");
        mockUser.setRole(role);

        // Location
        Location location = new Location();
        location.setCountry("Serbia");
        location.setCity("Belgrade");
        mockUser.setAddress(location);

        when(userRepository.findAll()).thenReturn(java.util.List.of(mockUser));

        // Act
        UserViewDTO foundUser = userService.getUserById(1);

        // Assert
        assertEquals("johndoe", foundUser.getUsername());
        assertEquals("John", foundUser.getName());
        assertEquals("Doe", foundUser.getSurname());
        assertEquals("john@example.com", foundUser.getEmail());
        assertEquals("ROLE_USER", foundUser.getRole().getName());
        assertEquals("Serbia", foundUser.getLocation().getCountry());
        assertEquals("Belgrade", foundUser.getLocation().getCity());
    }

    @Test
    void testGetUserById_UserDoesNotExist() {
        // Mock: baza ne sadrži nijednog korisnika
        when(userRepository.findAll()).thenReturn(java.util.List.of());

        // Poziv metode
        UserViewDTO result = userService.getUserById(999); // neki ID koji ne postoji

        // Provera
        assertNull(result.getId(), "ID treba biti null jer korisnik ne postoji");
        assertNull(result.getUsername(), "Username treba biti null jer korisnik ne postoji");
        assertNull(result.getEmail(), "Email treba biti null jer korisnik ne postoji");
        assertEquals(0, result.getPostCount(), "Post count treba biti 0");
        assertEquals(0, result.getFollowerCount(), "Follower count treba biti 0");
        assertEquals(0, result.getFollowingCount(), "Following count treba biti 0");
    }
    @Test
    void testFollowUser_Success() {
        // 1. Kreiramo mock korisnike
        User follower = new User();
        follower.setId(1);
        follower.setUsername("follower");

        User followed = new User();
        followed.setId(2);
        followed.setUsername("followed");

        // Setovi followings/followers prazni
        follower.setFollowings(new HashSet<>());
        followed.setFollowers(new HashSet<>());

        // 2. Mockujemo repository
        when(userRepository.findById(1)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2)).thenReturn(Optional.of(followed));

        // 3. Pozivamo testiranu metodu
        userService.followUser(1, 2);

        // 4. Proveravamo da follower sada prati followed
        assertTrue(follower.getFollowings().contains(followed),
                "Follower treba da sadrži followed u followings setu");
    }
    @Test
    void testFollowUser_AlreadyFollowing() {
        // 1. Kreiramo mock korisnike
        User follower = new User();
        follower.setId(1);
        follower.setUsername("follower");

        User followed = new User();
        followed.setId(2);
        followed.setUsername("followed");

        // 2. Postavljamo da follower već prati followed
        Set<User> followings = new HashSet<>();
        followings.add(followed);
        follower.setFollowings(followings);

        // 3. Mock repository
        when(userRepository.findById(1)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2)).thenReturn(Optional.of(followed));

        // 4. Poziv metode i očekivanje izuzetka
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.followUser(1, 2);
        });

        // 5. Opcionalna provera poruke izuzetka
        assertTrue(exception.getMessage().contains("already following"),
                "Poruka izuzetka treba da sadrži 'already following'");
    }
    @Test
    void testUnfollowUser_Success() {
        // 1. Kreiramo mock korisnike
        User follower = new User();
        follower.setId(1);
        follower.setUsername("follower");

        User followed = new User();
        followed.setId(2);
        followed.setUsername("followed");

        // 2. Postavljamo da follower prati followed
        Set<User> followings = new HashSet<>();
        followings.add(followed);
        follower.setFollowings(followings);

        // 3. Mock repository
        when(userRepository.findById(1)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2)).thenReturn(Optional.of(followed));

        // 4. Poziv metode
        userService.unfollowUser(1, 2);

        // 5. Provera da follower više ne prati followed
        assertFalse(follower.getFollowings().contains(followed),
                "Follower više ne treba da prati followed korisnika");
    }
}
