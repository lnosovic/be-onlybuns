package com.example.onlybuns.repository;

import com.example.onlybuns.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query(value="SELECT COUNT(*) FROM Post",nativeQuery = true)
    int countTotalPosts();
    @Query(value="SELECT COUNT(p) FROM Post p WHERE p.time_of_publishing >=:lastMonth",nativeQuery = true)
    int countPostsInLastMonth(@Param("lastMonth")LocalDateTime lastMonth);
    @Query(value = """
    SELECT p.*
    FROM Post p LEFT JOIN post_user_likes pul ON p.id = pul.post_id
    WHERE p.time_of_publishing >=:sevenDaysAgo
    GROUP BY p.id
    ORDER BY COUNT(pul.user_id) DESC
    LIMIT 5
    """,nativeQuery=true)
    List<Post> findTop5MostLikedPostsInLast7Days(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
    @Query(value = """
    SELECT p.*
    FROM Post p 
    LEFT JOIN post_user_likes pul ON p.id = pul.post_id
    GROUP BY p.id
    ORDER BY COUNT(pul.user_id) DESC
    LIMIT 10
    """,nativeQuery=true)
    List<Post> findTop10MostLikedPostsEver();
    @Query(value = """
    SELECT p.*
    FROM Post p
    JOIN location l ON p.location_id = l.id
    WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(l.latitude)) *cos(radians(l.longitude) - radians(:lon)) + sin(radians(:lat)) * sin(radians(l.latitude)))) < :radius
    """,nativeQuery=true)
    List<Post> getNearbyPosts(@Param("lat") double lat,
                                   @Param("lon") double lon,
                                   @Param("radius") double radiusInKm);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.timeOfPublishing BETWEEN :from AND :to")
    Integer countByTimeOfPublishingBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(DISTINCT p.user.id) FROM Post p")
    Long countDistinctUserId();}
