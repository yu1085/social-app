package com.example.socialmeet.repository;

import com.example.socialmeet.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByIsActiveTrueOrderByCreatedAtDesc();
    List<Coupon> findByTypeAndIsActiveTrueOrderByCreatedAtDesc(Coupon.CouponType type);
}
