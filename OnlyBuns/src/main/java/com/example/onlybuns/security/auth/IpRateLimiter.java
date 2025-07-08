package com.example.onlybuns.security.auth;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IpRateLimiter {
    private final RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(5)
            .limitRefreshPeriod(Duration.ofSeconds(60))
            .timeoutDuration(Duration.ZERO)
            .build();

    private final ConcurrentHashMap<String, RateLimiter> ipLimiters = new ConcurrentHashMap<>();

    public boolean isBlocked(String ip) {
        RateLimiter limiter = ipLimiters.computeIfAbsent(ip, i -> RateLimiter.of("ipLimiter-" + i, config));
        return !limiter.acquirePermission();
    }
    public void loginSuccess(String ip) {
        // Kreira novi limiter za isti IP, efektivno resetujući broj pokušaja
        RateLimiter newLimiter = RateLimiter.of("ipLimiter-" + ip, config);
        ipLimiters.put(ip, newLimiter);
        System.out.println("Resetovan limiter za IP: " + ip);
    }
    public void recordFailedAttempt(String ip) {
        RateLimiter limiter = ipLimiters.computeIfAbsent(ip, i -> RateLimiter.of("ipLimiter-" + i, config));
        limiter.acquirePermission(); // registruje pokušaj
        System.out.println("NEUSPEO pokušaj sa IP: " + ip);
    }
}
