package com.example.socialmeet.repository;

import com.example.socialmeet.entity.RechargeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 充值订单Repository
 */
@Repository
public interface RechargeOrderRepository extends JpaRepository<RechargeOrder, String> {
    
    /**
     * 根据用户ID查询充值订单（按创建时间倒序）
     */
    Page<RechargeOrder> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和状态查询充值订单
     */
    List<RechargeOrder> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * 根据第三方订单号查询
     */
    Optional<RechargeOrder> findByThirdPartyOrderId(String thirdPartyOrderId);
    
    /**
     * 根据第三方交易号查询
     */
    Optional<RechargeOrder> findByThirdPartyTransactionId(String thirdPartyTransactionId);
    
    /**
     * 查询过期的待支付订单
     */
    @Query("SELECT r FROM RechargeOrder r WHERE r.status = 'PENDING' AND r.expiredAt < :now")
    List<RechargeOrder> findExpiredPendingOrders(@Param("now") LocalDateTime now);
    
    /**
     * 根据状态统计订单数量
     */
    @Query("SELECT COUNT(r) FROM RechargeOrder r WHERE r.userId = :userId AND r.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
    
    /**
     * 统计用户总充值金额
     */
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM RechargeOrder r WHERE r.userId = :userId AND r.status = 'SUCCESS'")
    BigDecimal getTotalRechargeAmountByUserId(@Param("userId") Long userId);
}
