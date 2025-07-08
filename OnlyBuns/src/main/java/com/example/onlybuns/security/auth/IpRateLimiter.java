package com.example.onlybuns.security.auth;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IpRateLimiter {
    private final RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(5)
            .limitRefreshPeriod(Duration.ofSeconds(60))
            .timeoutDuration(Duration.ZERO)
            .build();

    private final RateLimiterRegistry registry = RateLimiterRegistry.of(config);
    private final ConcurrentHashMap<String, RateLimiter> ipLimiters = new ConcurrentHashMap<>();

    public boolean isBlocked(String ip) {
        RateLimiter limiter = ipLimiters.computeIfAbsent(ip,
                i -> registry.rateLimiter("ipLimiter-" + i));
        int remaining = limiter.getMetrics().getAvailablePermissions();
        System.out.println("Preostalo pokušaja: " + remaining);
        return remaining <= 0;
    }
    public void loginSuccess(String ip) {
        String limiterName = "ipLimiter-" + ip;
        registry.remove(limiterName);         // uklanja iz registry
        ipLimiters.remove(ip);                // uklanja iz lokalne mape
        System.out.println("Resetovan limiter za IP: " + ip);
    }
    public void recordFailedAttempt(String ip) {
        RateLimiter limiter = ipLimiters.computeIfAbsent(ip,
                i -> registry.rateLimiter("ipLimiter-" + i));
        limiter.acquirePermission();
        System.out.println("NEUSPEO pokušaj u: " + java.time.Instant.now());
    }
}
