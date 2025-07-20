package com.example.onlybuns.service.impl;

import com.example.onlybuns.dto.UserActivityBreakdownDTO;
import com.example.onlybuns.repository.CommentRepository;
import com.example.onlybuns.repository.PostRepository;
import com.example.onlybuns.repository.UserRepository;
import com.example.onlybuns.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Integer countPostsBetween(LocalDateTime from, LocalDateTime to) {
        return postRepository.countByTimeOfPublishingBetween(from, to);
    }

    @Override
    public Integer countCommentsBetween(LocalDateTime from, LocalDateTime to) {
        return commentRepository.countByCreatedAtBetween(from, to);
    }

    @Override
    public UserActivityBreakdownDTO getUserActivityBreakdown() {
        Long totalUsers = userRepository.count();

        Long usersWithPosts = postRepository.countDistinctUserId();

        Long usersWithOnlyComments = commentRepository.countUsersWhoOnlyCommented();

        Long inactiveUsers = totalUsers - usersWithPosts - usersWithOnlyComments;

        UserActivityBreakdownDTO dto = new UserActivityBreakdownDTO();
        dto.setPercentWithPosts(100.0 * usersWithPosts / totalUsers);
        dto.setPercentWithOnlyComments(100.0 * usersWithOnlyComments / totalUsers);
        dto.setPercentInactive(100.0 * inactiveUsers / totalUsers);

        return dto;
    }
}