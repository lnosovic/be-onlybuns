package com.example.onlybuns.controller;

import com.example.onlybuns.dto.UserActivityBreakdownDTO;
import com.example.onlybuns.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/post-count")
    public Integer getPostCount(@RequestParam String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = switch (period) {
            case "weekly" -> now.minusWeeks(1);
            case "monthly" -> now.minusMonths(1);
            case "yearly" -> now.minusYears(1);
            default -> throw new IllegalArgumentException("Nepoznat period");
        };
        return analyticsService.countPostsBetween(from, now);
    }

    @GetMapping("/comment-count")
    public Integer getCommentCount(@RequestParam String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = switch (period) {
            case "weekly" -> now.minusWeeks(1);
            case "monthly" -> now.minusMonths(1);
            case "yearly" -> now.minusYears(1);
            default -> throw new IllegalArgumentException("Nepoznat period");
        };
        return analyticsService.countCommentsBetween(from, now);
    }

    @GetMapping("/user-activity-breakdown")
    public UserActivityBreakdownDTO getUserBreakdown() {
        return analyticsService.getUserActivityBreakdown();
    }
}