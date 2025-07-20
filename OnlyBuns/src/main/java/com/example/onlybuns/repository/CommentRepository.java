package com.example.onlybuns.repository;

import com.example.onlybuns.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CommentRepository  extends JpaRepository<Post, Integer> {

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.createdAt BETWEEN :from AND :to")
    Integer countByCreatedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT COUNT(DISTINCT c.user.id)
        FROM Comment c
        WHERE c.user.id NOT IN (SELECT DISTINCT p.user.id FROM Post p)
    """)
    Long countUsersWhoOnlyCommented();
}
