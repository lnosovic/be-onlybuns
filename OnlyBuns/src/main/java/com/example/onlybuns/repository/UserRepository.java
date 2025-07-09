package com.example.onlybuns.repository;

import com.example.onlybuns.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User getUserByEmail(String email);
    @Query("SELECT u FROM User u JOIN u.followers f WHERE f.id=:userId")
    List<User> findFollowingUsers(@Param("userId")Integer userId);
    @Query("SELECT u FROM User u JOIN u.followings f WHERE f.id=:userId")
    List<User> findFollowerUsers(@Param("userId")Integer userId);
    @Query(value = """
    SELECT u.* , COUNT(pul.user_id) AS totalLikes
    FROM Users u 
    JOIN post_user_likes pul on u.id = pul.user_id
    JOIN Post p ON pul.post_id = p.id
    WHERE p.time_of_publishing >=:sevenDaysAgo
    GROUP BY u.id
    ORDER BY totalLikes DESC
    LIMIT 10
    """,nativeQuery = true)
    List<User> findTop10MostUserLikesInLast7Days(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
    @Query("SELECT u FROM User u WHERE u.isActivated = false")
    List<User> findByIsActivatedFalse();
}
