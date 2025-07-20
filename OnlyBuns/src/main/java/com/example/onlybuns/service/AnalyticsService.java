package com.example.onlybuns.service;

import com.example.onlybuns.dto.UserActivityBreakdownDTO;

import java.time.LocalDateTime;

public interface AnalyticsService {
    Integer countPostsBetween(LocalDateTime from, LocalDateTime to);
    Integer countCommentsBetween(LocalDateTime from, LocalDateTime to);
    UserActivityBreakdownDTO getUserActivityBreakdown();
}