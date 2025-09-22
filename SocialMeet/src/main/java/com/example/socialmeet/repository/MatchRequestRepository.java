package com.example.socialmeet.repository;

import com.example.socialmeet.entity.MatchRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {
    
    /**
     * 查找用户的待匹配请求
     */
    @Query("SELECT mr FROM MatchRequest mr WHERE mr.userId = :userId AND mr.status = 'PENDING'")
    Optional<MatchRequest> findPendingRequestByUserId(@Param("userId") Long userId);
    
    /**
     * 查找可匹配的请求（排除自己）
     */
    @Query("SELECT mr FROM MatchRequest mr WHERE mr.userId != :userId AND mr.status = 'PENDING' " +
           "AND mr.matchType = :matchType AND mr.preferenceLevel = :preferenceLevel " +
           "AND mr.expiresAt > :now")
    List<MatchRequest> findMatchableRequests(@Param("userId") Long userId, 
                                           @Param("matchType") String matchType,
                                           @Param("preferenceLevel") Integer preferenceLevel,
                                           @Param("now") LocalDateTime now);
    
    /**
     * 查找过期的请求
     */
    @Query("SELECT mr FROM MatchRequest mr WHERE mr.status = 'PENDING' AND mr.expiresAt < :now")
    List<MatchRequest> findExpiredRequests(@Param("now") LocalDateTime now);
    
    /**
     * 取消用户的所有待匹配请求
     */
    @Modifying
    @Query("UPDATE MatchRequest mr SET mr.status = 'CANCELLED' WHERE mr.userId = :userId AND mr.status = 'PENDING'")
    void cancelUserPendingRequests(@Param("userId") Long userId);
}
