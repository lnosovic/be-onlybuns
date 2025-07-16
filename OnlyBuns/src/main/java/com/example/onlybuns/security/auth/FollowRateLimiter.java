package com.example.onlybuns.security.auth;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class FollowRateLimiter {


    private final Map<Long, List<LocalDateTime>> followAttempts = new ConcurrentHashMap<>();


    private static final int FOLLOW_LIMIT_PER_MINUTE = 50;

    public boolean isAllowed(Long userId) {
        LocalDateTime now = LocalDateTime.now();

        // Dohvati postojeće pokušaje ili kreiraj novu listu
        List<LocalDateTime> attempts =
                followAttempts.computeIfAbsent(userId, k -> new ArrayList<>());

        // Ukloni sve pokušaje starije od 60 sekundi
        attempts.removeIf(time -> time.isBefore(now.minusMinutes(1)));

        // Ako je korisnik već dostigao limit → blokiraj
        if (attempts.size() >= FOLLOW_LIMIT_PER_MINUTE) {
            return false;
        }

        // Inače dodaj novi pokušaj i dozvoli akciju
        attempts.add(now);
        return true;
    }
}
