package com.example.onlybuns.repository;

import com.example.onlybuns.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
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
    @Query("""
    SELECT u, COUNT(f) AS followerCount
    FROM User u
    LEFT JOIN u.followers f
    LEFT JOIN u.posts p
    WHERE (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:surname IS NULL OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :surname, '%')))
      AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
    GROUP BY u
    HAVING (:minPostCount IS NULL OR COUNT(p) >= :minPostCount)
       AND (:maxPostCount IS NULL OR COUNT(p) <= :maxPostCount)
    ORDER BY COUNT(f) ASC
""")
    Page<Object[]> searchByFollowerCountAsc(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("email") String email,
            @Param("minPostCount") Integer minPostCount,
            @Param("maxPostCount") Integer maxPostCount,
            Pageable pageable
    );
    @Query("""
    SELECT u, COUNT(f) AS followerCount
    FROM User u
    LEFT JOIN u.followers f
    LEFT JOIN u.posts p
    WHERE (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:surname IS NULL OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :surname, '%')))
      AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
    GROUP BY u
    HAVING (:minPostCount IS NULL OR COUNT(p) >= :minPostCount)
       AND (:maxPostCount IS NULL OR COUNT(p) <= :maxPostCount)
    ORDER BY COUNT(f) DESC
""")
    Page<Object[]> searchByFollowerCountDesc(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("email") String email,
            @Param("minPostCount") Integer minPostCount,
            @Param("maxPostCount") Integer maxPostCount,
            Pageable pageable
    );
}
