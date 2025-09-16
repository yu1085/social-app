package com.example.socialmeet.repository;

import com.example.socialmeet.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    List<PaymentOrder> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Optional<PaymentOrder> findByOrderNo(String orderNo);
    
    @Query("SELECT po FROM PaymentOrder po WHERE po.userId = :userId AND po.status = :status ORDER BY po.createdAt DESC")
    List<PaymentOrder> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") PaymentOrder.OrderStatus status);
    
    @Query("SELECT po FROM PaymentOrder po WHERE po.status = 'PENDING' AND po.createdAt < :expiredTime")
    List<PaymentOrder> findExpiredPendingOrders(@Param("expiredTime") LocalDateTime expiredTime);
    
    @Query("SELECT po FROM PaymentOrder po WHERE po.paymentNo = :paymentNo")
    Optional<PaymentOrder> findByPaymentNo(@Param("paymentNo") String paymentNo);
}
