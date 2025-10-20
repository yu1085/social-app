package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.PaymentOrder;
import com.socialmeet.backend.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 支付订单Repository
 */
@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {

    /**
     * 根据订单ID查找订单
     */
    Optional<PaymentOrder> findByOrderId(String orderId);

    /**
     * 根据用户ID查找订单列表
     */
    List<PaymentOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据支付宝交易号查找订单
     */
    Optional<PaymentOrder> findByAlipayTradeNo(String alipayTradeNo);

    /**
     * 根据支付宝商户订单号查找订单
     */
    Optional<PaymentOrder> findByAlipayOutTradeNo(String alipayOutTradeNo);

    /**
     * 根据状态查找订单
     */
    List<PaymentOrder> findByStatus(OrderStatus status);

    /**
     * 查找过期的待支付订单
     */
    @Query("SELECT p FROM PaymentOrder p WHERE p.status = :status AND p.expiredAt < :now")
    List<PaymentOrder> findExpiredOrders(@Param("status") OrderStatus status, @Param("now") LocalDateTime now);

    /**
     * 根据用户ID和状态查找订单
     */
    List<PaymentOrder> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status);
    
    /**
     * 根据用户ID查找订单列表（分页）
     */
    Page<PaymentOrder> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和状态查找订单（分页）
     */
    Page<PaymentOrder> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status, Pageable pageable);

    /**
     * 统计用户总充值金额
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentOrder p WHERE p.userId = :userId AND p.status = :status")
    Double sumAmountByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status);

    /**
     * 统计用户充值次数
     */
    Long countByUserIdAndStatus(Long userId, OrderStatus status);
}
