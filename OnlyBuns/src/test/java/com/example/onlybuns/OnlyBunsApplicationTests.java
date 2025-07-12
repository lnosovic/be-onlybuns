package com.example.onlybuns;

import com.example.onlybuns.dto.LocationDTO;
import com.example.onlybuns.dto.UserRequest;
import com.example.onlybuns.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class OnlyBunsApplicationTests {
	@Autowired
	private UserService userService;
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

}
