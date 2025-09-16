package com.example.socialmeet.service;

import com.example.socialmeet.dto.CouponDTO;
import com.example.socialmeet.dto.UserCouponDTO;
import com.example.socialmeet.entity.Coupon;
import com.example.socialmeet.entity.UserCoupon;
import com.example.socialmeet.repository.CouponRepository;
import com.example.socialmeet.repository.UserCouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CouponService {
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Autowired
    private UserCouponRepository userCouponRepository;
    
    public List<CouponDTO> getAllAvailableCoupons() {
        List<Coupon> coupons = couponRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        return coupons.stream().map(CouponDTO::new).collect(Collectors.toList());
    }
    
    public List<CouponDTO> getCouponsByType(Coupon.CouponType type) {
        List<Coupon> coupons = couponRepository.findByTypeAndIsActiveTrueOrderByCreatedAtDesc(type);
        return coupons.stream().map(CouponDTO::new).collect(Collectors.toList());
    }
    
    public UserCouponDTO giveCouponToUser(Long userId, Long couponId) {
        Optional<Coupon> couponOpt = couponRepository.findById(couponId);
        if (!couponOpt.isPresent()) {
            throw new RuntimeException("卡券不存在");
        }
        
        Coupon coupon = couponOpt.get();
        if (!coupon.getIsActive()) {
            throw new RuntimeException("卡券已下架");
        }
        
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(coupon.getValidDays());
        UserCoupon userCoupon = new UserCoupon(userId, couponId, expiresAt);
        userCoupon = userCouponRepository.save(userCoupon);
        
        UserCouponDTO dto = new UserCouponDTO(userCoupon);
        dto.setCouponName(coupon.getName());
        dto.setCouponDescription(coupon.getDescription());
        
        return dto;
    }
    
    public List<UserCouponDTO> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return userCoupons.stream().map(this::convertToUserCouponDTO).collect(Collectors.toList());
    }
    
    public List<UserCouponDTO> getUsableCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository
                .findUsableCouponsByUserId(userId, LocalDateTime.now());
        return userCoupons.stream().map(this::convertToUserCouponDTO).collect(Collectors.toList());
    }
    
    public List<UserCouponDTO> getUserCouponsByStatus(Long userId, UserCoupon.CouponStatus status) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndStatus(userId, status);
        return userCoupons.stream().map(this::convertToUserCouponDTO).collect(Collectors.toList());
    }
    
    public boolean useCoupon(Long userId, Long userCouponId) {
        Optional<UserCoupon> userCouponOpt = userCouponRepository.findById(userCouponId);
        if (!userCouponOpt.isPresent()) {
            return false;
        }
        
        UserCoupon userCoupon = userCouponOpt.get();
        if (!userCoupon.getUserId().equals(userId)) {
            return false;
        }
        
        if (!userCoupon.isUsable()) {
            return false;
        }
        
        userCoupon.use();
        userCouponRepository.save(userCoupon);
        
        return true;
    }
    
    public boolean useCouponByType(Long userId, Coupon.CouponType type) {
        List<UserCoupon> usableCoupons = userCouponRepository
                .findUsableCouponsByUserId(userId, LocalDateTime.now());
        
        for (UserCoupon userCoupon : usableCoupons) {
            Optional<Coupon> couponOpt = couponRepository.findById(userCoupon.getCouponId());
            if (couponOpt.isPresent() && couponOpt.get().getType() == type) {
                userCoupon.use();
                userCouponRepository.save(userCoupon);
                return true;
            }
        }
        
        return false;
    }
    
    private UserCouponDTO convertToUserCouponDTO(UserCoupon userCoupon) {
        UserCouponDTO dto = new UserCouponDTO(userCoupon);
        
        Optional<Coupon> couponOpt = couponRepository.findById(userCoupon.getCouponId());
        if (couponOpt.isPresent()) {
            Coupon coupon = couponOpt.get();
            dto.setCouponName(coupon.getName());
            dto.setCouponDescription(coupon.getDescription());
        }
        
        return dto;
    }
}
