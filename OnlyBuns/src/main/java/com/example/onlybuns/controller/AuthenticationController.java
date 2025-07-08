package com.example.onlybuns.controller;

import com.example.onlybuns.dto.JwtAuthenticationRequest;
import com.example.onlybuns.dto.UserRequest;
import com.example.onlybuns.dto.UserTokenState;
import com.example.onlybuns.exception.ResourceConflictException;
import com.example.onlybuns.model.User;
import com.example.onlybuns.repository.UserRepository;
import com.example.onlybuns.security.auth.IpRateLimiter;
import com.example.onlybuns.service.EmailService;
import com.example.onlybuns.service.UserService;
import com.example.onlybuns.util.TokenUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {
    @Autowired
    private IpRateLimiter ipRateLimiter;
    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;


    // Prvi endpoint koji pogadja korisnik kada se loguje.
    // Tada zna samo svoje korisnicko ime i lozinku i to prosledjuje na backend.
    @PostMapping("/login")
    public ResponseEntity<UserTokenState> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response, HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        System.out.println("ip: " + ip);
        if (ipRateLimiter.isBlocked(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        // Ukoliko kredencijali nisu ispravni, logovanje nece biti uspesno, desice se
        // AuthenticationException
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));

            // Ukoliko je autentifikacija uspesna, ubaci korisnika u trenutni security
            // kontekst
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Kreiraj token za tog korisnika
            boolean isActivated = userService.findByUsername(authenticationRequest.getUsername()).isActivated();
            if (isActivated) {
                User user = (User) authentication.getPrincipal();
                String jwt = tokenUtils.generateToken(user.getUsername());
                int expiresIn = tokenUtils.getExpiredIn();

                ipRateLimiter.loginSuccess(ip);
                // Vrati token kao odgovor na uspesnu autentifikaciju
                return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (AuthenticationException ex) {
            ipRateLimiter.recordFailedAttempt(ip);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Endpoint za registraciju novog korisnika
    @PostMapping("/signup")
    public ResponseEntity<User> addUser(@RequestBody UserRequest userRequest, UriComponentsBuilder ucBuilder) {
        User existUser = this.userService.findByUsername(userRequest.getUsername());

        if (existUser != null) {
            throw new ResourceConflictException(userRequest.getId(), "Username already exists");
        }

        User user = this.userService.save(userRequest);
        System.out.println("Thread id: " + Thread.currentThread().getId());
        try {
            //slanje emaila
            emailService.sendNotificaitionSync(userRequest);
        }catch( Exception e ){
            System.err.println("Greska prilikom slanja emaila: " + e.getMessage());
        }

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
    @PutMapping("/activate/{id}")
    public ResponseEntity<Void> activateUser(@PathVariable int id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setActivated(true);
        userService.updateUser(user); // Ažurirajte korisnika u bazi
        return ResponseEntity.ok().build(); // Vratite OK status bez tela odgovora
    }
}
