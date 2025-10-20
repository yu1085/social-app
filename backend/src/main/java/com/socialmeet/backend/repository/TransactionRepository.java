package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易记录Repository
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * 根据用户ID查找交易记录
     */
    List<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 计算用户总财富值（只计算购买的聊币）
     */
    @Query("SELECT COALESCE(SUM(t.wealthValue), 0) FROM Transaction t WHERE t.userId = :userId AND t.coinSource = 'PURCHASED' AND t.status = 'SUCCESS'")
    Integer calculateTotalWealthValue(@Param("userId") Long userId);
    
    /**
     * 计算用户购买的聊币总数
     */
    @Query("SELECT COALESCE(SUM(t.coinAmount), 0) FROM Transaction t WHERE t.userId = :userId AND t.coinSource = 'PURCHASED' AND t.status = 'SUCCESS'")
    Integer calculateTotalPurchasedCoins(@Param("userId") Long userId);
    
    /**
     * 根据时间范围查找交易记录
     */
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.createdAt BETWEEN :startTime AND :endTime ORDER BY t.createdAt DESC")
    List<Transaction> findByUserIdAndTimeRange(@Param("userId") Long userId, 
                                             @Param("startTime") LocalDateTime startTime, 
                                             @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计用户邀请奖励
     */
    @Query("SELECT COALESCE(SUM(t.coinAmount), 0) FROM Transaction t WHERE t.userId = :userId AND t.transactionType = 'BONUS' AND t.description LIKE '%邀请%' AND t.status = 'SUCCESS'")
    Integer calculateInviteRewards(@Param("userId") Long userId);
}
