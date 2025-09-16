package com.example.socialmeet.repository;

import com.example.socialmeet.entity.UserView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserViewRepository extends JpaRepository<UserView, Long> {
    @Query("SELECT uv FROM UserView uv WHERE uv.viewedId = :viewedId ORDER BY uv.createdAt DESC")
    List<UserView> findByViewedIdOrderByCreatedAtDesc(@Param("viewedId") Long viewedId);
    
    @Query("SELECT uv FROM UserView uv WHERE uv.viewerId = :viewerId ORDER BY uv.createdAt DESC")
    List<UserView> findByViewerIdOrderByCreatedAtDesc(@Param("viewerId") Long viewerId);
    
    @Query("SELECT uv FROM UserView uv WHERE uv.viewedId = :viewedId AND uv.createdAt BETWEEN :startDate AND :endDate ORDER BY uv.createdAt DESC")
    List<UserView> findByViewedIdAndDateRange(@Param("viewedId") Long viewedId, 
                                            @Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(uv) FROM UserView uv WHERE uv.viewedId = :viewedId")
    Long countByViewedId(@Param("viewedId") Long viewedId);
    
    @Query("SELECT uv FROM UserView uv WHERE uv.viewerId = :viewerId AND uv.viewedId = :viewedId ORDER BY uv.createdAt DESC")
    List<UserView> findByViewerIdAndViewedId(@Param("viewerId") Long viewerId, @Param("viewedId") Long viewedId);
}
