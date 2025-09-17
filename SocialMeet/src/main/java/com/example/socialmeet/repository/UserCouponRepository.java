package com.example.socialmeet.repository;

import com.example.socialmeet.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.userId = :userId AND uc.status = 'UNUSED' AND uc.expiresAt > :now")
    List<UserCoupon> findUsableCouponsByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.userId = :userId AND uc.status = :status ORDER BY uc.createdAt DESC")
    List<UserCoupon> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
    
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.userId = :userId AND uc.couponId = :couponId AND uc.status = 'UNUSED' AND uc.expiresAt > :now")
    List<UserCoupon> findUsableCouponsByUserIdAndCouponId(@Param("userId") Long userId, @Param("couponId") Long couponId, @Param("now") LocalDateTime now);
}
