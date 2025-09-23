package com.example.socialmeet.repository;

import com.example.socialmeet.entity.CurrencyTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 货币交易记录Repository
 */
@Repository
public interface CurrencyTransactionRepository extends JpaRepository<CurrencyTransaction, Long> {
    
    /**
     * 根据用户ID查找交易记录
     */
    Page<CurrencyTransaction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和货币类型查找交易记录
     */
    Page<CurrencyTransaction> findByUserIdAndCurrencyTypeOrderByCreatedAtDesc(Long userId, String currencyType, Pageable pageable);
    
    /**
     * 根据用户ID和交易类型查找交易记录
     */
    Page<CurrencyTransaction> findByUserIdAndTransactionTypeOrderByCreatedAtDesc(Long userId, String transactionType, Pageable pageable);
    
    /**
     * 根据时间范围查找交易记录
     */
    @Query("SELECT ct FROM CurrencyTransaction ct WHERE ct.userId = :userId AND ct.createdAt BETWEEN :startTime AND :endTime ORDER BY ct.createdAt DESC")
    Page<CurrencyTransaction> findByUserIdAndTimeRange(@Param("userId") Long userId, 
                                                      @Param("startTime") LocalDateTime startTime, 
                                                      @Param("endTime") LocalDateTime endTime, 
                                                      Pageable pageable);
    
    /**
     * 统计用户总消费
     */
    @Query("SELECT SUM(ct.amount) FROM CurrencyTransaction ct WHERE ct.userId = :userId AND ct.transactionType = 'SPEND'")
    java.math.BigDecimal getTotalSpentByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户总收入
     */
    @Query("SELECT SUM(ct.amount) FROM CurrencyTransaction ct WHERE ct.userId = :userId AND ct.transactionType = 'EARN'")
    java.math.BigDecimal getTotalEarnedByUserId(@Param("userId") Long userId);
    
    /**
     * 根据关联ID和类型查找交易记录
     */
    List<CurrencyTransaction> findByRelatedIdAndRelatedType(Long relatedId, String relatedType);
    
    /**
     * 统计今日交易次数
     */
    @Query("SELECT COUNT(ct) FROM CurrencyTransaction ct WHERE ct.userId = :userId AND ct.createdAt >= :today")
    Long countTodayTransactions(@Param("userId") Long userId, @Param("today") LocalDateTime today);
}
