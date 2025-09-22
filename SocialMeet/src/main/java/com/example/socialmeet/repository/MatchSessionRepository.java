package com.example.socialmeet.repository;

import com.example.socialmeet.entity.MatchSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchSessionRepository extends JpaRepository<MatchSession, Long> {
    
    /**
     * 根据会话ID查找匹配会话
     */
    Optional<MatchSession> findBySessionId(String sessionId);
    
    /**
     * 查找用户的活跃匹配会话
     */
    @Query("SELECT ms FROM MatchSession ms WHERE (ms.user1Id = :userId OR ms.user2Id = :userId) " +
           "AND ms.status = 'ACTIVE'")
    Optional<MatchSession> findActiveSessionByUserId(@Param("userId") Long userId);
    
    /**
     * 查找用户的所有匹配会话
     */
    @Query("SELECT ms FROM MatchSession ms WHERE ms.user1Id = :userId OR ms.user2Id = :userId " +
           "ORDER BY ms.createdAt DESC")
    List<MatchSession> findSessionsByUserId(@Param("userId") Long userId);
    
    /**
     * 结束匹配会话
     */
    @Query("UPDATE MatchSession ms SET ms.status = 'ENDED', ms.endedAt = CURRENT_TIMESTAMP " +
           "WHERE ms.sessionId = :sessionId")
    void endSession(@Param("sessionId") String sessionId);
}
