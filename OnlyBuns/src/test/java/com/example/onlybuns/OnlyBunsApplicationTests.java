package com.example.onlybuns;

import com.example.onlybuns.dto.LocationDTO;
import com.example.onlybuns.dto.UserRequest;
import com.example.onlybuns.model.Location;
import com.example.onlybuns.model.Post;
import com.example.onlybuns.model.Role;
import com.example.onlybuns.model.User;
import com.example.onlybuns.repository.LocationRepository;
import com.example.onlybuns.repository.UserRepository;
import com.example.onlybuns.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class OnlyBunsApplicationTests {
	@Autowired
	private UserService userService;
	@Autowired
	private UserService followService;

	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private UserRepository userRepository;

	private Integer targetUserId;

	@Test
	public void testConcurrentUsernameRegistration() throws InterruptedException {
		AtomicBoolean success = new AtomicBoolean(false);
		AtomicBoolean conflict = new AtomicBoolean(false);
		Runnable task = () -> {
			UserRequest req = new UserRequest();
			req.setUsername("test");
			req.setPassword("test123");
			req.setName("Test");
			req.setSurname("Test");
			req.setEmail("test@mail.com");
			LocationDTO location = new  LocationDTO();
			location.setCountry("Serbia");
			location.setCity("Novi Sad");
			req.setLocation(location);

			try {
				userService.save(req);
				success.set(true);
				System.out.println("Uspešna registracija u thread: " + Thread.currentThread().getId());
			} catch (Exception e) {
				conflict.set(true);
				System.out.println("Greška u thread: " + Thread.currentThread().getId() + " - " + e.getMessage());
			}
		};

		Thread t1 = new Thread(task);
		Thread t2 = new Thread(task);

		t1.start();
		Thread.sleep(100);
		t2.start();

		t1.join();
		t2.join();
		assertTrue(success.get(), "Jedna registracija mora da uspe");
		assertTrue(conflict.get(), "Druga registracija mora da padne zbog konflikta");
	}

	@Test
	public void testConcurrentFollow_NoDuplicates() throws InterruptedException {
		// 1. Kreiraj target korisnika kome će drugi pratiti
		UserRequest targetUserReq = new UserRequest();
		targetUserReq.setUsername("targetUser");
		targetUserReq.setPassword("password");
		targetUserReq.setName("Target");
		targetUserReq.setSurname("User");
		targetUserReq.setEmail("targetUser@example.com");
		LocationDTO location = new LocationDTO();
		location.setCountry("Serbia");
		location.setCity("Novi Sad");
		targetUserReq.setLocation(location);
		userService.save(targetUserReq);

		User targetUser = userRepository.findByUsername("targetUser");
		Integer targetUserId = targetUser.getId();

		// 2. Kreiraj followers paralelno kroz UserRequest i sačuvaj ih
		int followerCount = 10;
		for (int i = 0; i < followerCount; i++) {
			UserRequest followerReq = new UserRequest();
			followerReq.setUsername("follower" + i);
			followerReq.setPassword("password");
			followerReq.setName("Follower");
			followerReq.setSurname("" + i);
			followerReq.setEmail("follower" + i + "@example.com");
			followerReq.setLocation(location);
			userService.save(followerReq);
		}

		// 3. Dohvati sve followere
		List<User> followers = userRepository.findAll().stream()
				.filter(u -> u.getUsername().startsWith("follower"))
				.toList();

		// 4. Paralelno svaki follower pokuša da prati targetUser-a
		ExecutorService executor = Executors.newFixedThreadPool(followerCount);
		CountDownLatch latch = new CountDownLatch(followerCount);

		for (User follower : followers) {
			executor.submit(() -> {
				try {
					userService.followUser(follower.getId(), targetUserId);
				} catch (Exception e) {
					// Ako već prati, može se ignorisati
				} finally {
					latch.countDown();
				}
			});
		}

		// 5. Čekaj da svi završe
		latch.await();

		// 6. Proveri broj followera target korisnika
		int followersSize = userService.getFollowerUsers(targetUserId).size();
		System.out.println("Follower count after concurrency test: " + followersSize);

		// 7. Provera da nema duplih pratilaca
		assertTrue(followersSize <= followerCount, "Ne sme biti više pratilaca nego što je pokušano");
	}

}
