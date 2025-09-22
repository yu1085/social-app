package com.example.socialmeet.repository;

import com.example.socialmeet.entity.CallSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CallSessionRepository extends JpaRepository<CallSession, String> {
    
    /**
     * 根据发起方ID查找通话会话
     */
    List<CallSession> findByCallerIdOrderByCreatedAtDesc(Long callerId);
    
    /**
     * 根据接收方ID查找通话会话
     */
    List<CallSession> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    
    /**
     * 根据用户ID查找通话会话（发起方或接收方）
     */
    @Query("SELECT cs FROM CallSession cs WHERE cs.callerId = :userId OR cs.receiverId = :userId ORDER BY cs.createdAt DESC")
    List<CallSession> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据状态查找通话会话
     */
    List<CallSession> findByStatusOrderByCreatedAtDesc(String status);
    
    /**
     * 查找活跃的通话会话
     */
    @Query("SELECT cs FROM CallSession cs WHERE cs.status IN ('INITIATED', 'RINGING', 'ACTIVE') ORDER BY cs.createdAt DESC")
    List<CallSession> findActiveCallSessions();
    
    /**
     * 根据发起方和接收方查找通话会话
     */
    @Query("SELECT cs FROM CallSession cs WHERE (cs.callerId = :callerId AND cs.receiverId = :receiverId) OR (cs.callerId = :receiverId AND cs.receiverId = :callerId) ORDER BY cs.createdAt DESC")
    List<CallSession> findByCallerAndReceiver(@Param("callerId") Long callerId, @Param("receiverId") Long receiverId);
    
    /**
     * 查找指定时间范围内的通话会话
     */
    @Query("SELECT cs FROM CallSession cs WHERE cs.createdAt BETWEEN :startTime AND :endTime ORDER BY cs.createdAt DESC")
    List<CallSession> findByCreatedAtBetween(@Param("startTime") java.time.LocalDateTime startTime, @Param("endTime") java.time.LocalDateTime endTime);
    
    /**
     * 统计用户通话次数
     */
    @Query("SELECT COUNT(cs) FROM CallSession cs WHERE (cs.callerId = :userId OR cs.receiverId = :userId) AND cs.status = 'ENDED'")
    Long countCompletedCallsByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户通话总时长
     */
    @Query("SELECT COALESCE(SUM(cs.duration), 0) FROM CallSession cs WHERE (cs.callerId = :userId OR cs.receiverId = :userId) AND cs.status = 'ENDED'")
    Long sumCallDurationByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户通话总费用
     */
    @Query("SELECT COALESCE(SUM(cs.totalCost), 0) FROM CallSession cs WHERE cs.callerId = :userId AND cs.status = 'ENDED'")
    java.math.BigDecimal sumCallCostByUserId(@Param("userId") Long userId);
    
    /**
     * 根据接收方ID和状态查找通话会话
     */
    List<CallSession> findByReceiverIdAndStatusOrderByCreatedAtDesc(Long receiverId, String status);
    
    /**
     * 根据接收方ID和多个状态查找通话会话
     */
    List<CallSession> findByReceiverIdAndStatusInOrderByCreatedAtDesc(Long receiverId, List<String> statuses);
}
