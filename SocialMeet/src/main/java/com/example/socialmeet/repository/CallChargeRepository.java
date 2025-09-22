package com.example.socialmeet.repository;

import com.example.socialmeet.entity.CallCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通话扣费记录Repository
 */
@Repository
public interface CallChargeRepository extends JpaRepository<CallCharge, Long> {
    
    /**
     * 根据通话会话ID查找扣费记录
     */
    List<CallCharge> findByCallSessionIdOrderByCreatedAtDesc(String callSessionId);
    
    /**
     * 根据用户ID查找扣费记录
     */
    List<CallCharge> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根据用户ID和时间范围查找扣费记录
     */
    @Query("SELECT cc FROM CallCharge cc WHERE cc.userId = :userId AND cc.createdAt BETWEEN :startTime AND :endTime ORDER BY cc.createdAt DESC")
    List<CallCharge> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId, 
                                                   @Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计用户总扣费金额
     */
    @Query("SELECT COALESCE(SUM(cc.chargedAmount), 0) FROM CallCharge cc WHERE cc.userId = :userId")
    Double sumChargedAmountByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户通话总时长
     */
    @Query("SELECT COALESCE(SUM(cc.durationSeconds), 0) FROM CallCharge cc WHERE cc.userId = :userId")
    Long sumDurationByUserId(@Param("userId") Long userId);
}
