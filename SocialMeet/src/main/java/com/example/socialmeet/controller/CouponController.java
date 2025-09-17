package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.CouponDTO;
import com.example.socialmeet.dto.UserCouponDTO;
import com.example.socialmeet.entity.Coupon;
import com.example.socialmeet.entity.UserCoupon;
import com.example.socialmeet.service.CouponService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "*")
public class CouponController {
    
    @Autowired
    private CouponService couponService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponDTO>>> getAllAvailableCoupons() {
        try {
            List<CouponDTO> coupons = couponService.getAllAvailableCoupons();
            return ResponseEntity.ok(ApiResponse.success(coupons));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取卡券列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<CouponDTO>>> getCouponsByType(@PathVariable String type) {
        try {
            String couponType = String.valueOf(type.toUpperCase());
            List<CouponDTO> coupons = couponService.getCouponsByType(couponType);
            return ResponseEntity.ok(ApiResponse.success(coupons));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取卡券列表失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/give")
    public ResponseEntity<ApiResponse<UserCouponDTO>> giveCouponToUser(
            @RequestHeader("Authorization") String token,
            @RequestParam Long couponId) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            UserCouponDTO userCoupon = couponService.giveCouponToUser(userId, couponId);
            return ResponseEntity.ok(ApiResponse.success(userCoupon));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("领取卡券失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<UserCouponDTO>>> getUserCoupons(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            List<UserCouponDTO> userCoupons = couponService.getUserCoupons(userId);
            return ResponseEntity.ok(ApiResponse.success(userCoupons));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取我的卡券失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/usable")
    public ResponseEntity<ApiResponse<List<UserCouponDTO>>> getUsableCoupons(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            List<UserCouponDTO> userCoupons = couponService.getUsableCoupons(userId);
            return ResponseEntity.ok(ApiResponse.success(userCoupons));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取可用卡券失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<UserCouponDTO>>> getUserCouponsByStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable String status) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            String couponStatus = String.valueOf(status.toUpperCase());
            List<UserCouponDTO> userCoupons = couponService.getUserCouponsByStatus(userId, couponStatus);
            return ResponseEntity.ok(ApiResponse.success(userCoupons));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取卡券失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/use/{userCouponId}")
    public ResponseEntity<ApiResponse<String>> useCoupon(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userCouponId) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            boolean success = couponService.useCoupon(userId, userCouponId);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("使用卡券成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("使用卡券失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("使用卡券失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/use-by-type/{type}")
    public ResponseEntity<ApiResponse<String>> useCouponByType(
            @RequestHeader("Authorization") String token,
            @PathVariable String type) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            String couponType = String.valueOf(type.toUpperCase());
            boolean success = couponService.useCouponByType(userId, couponType);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("使用卡券成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("使用卡券失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("使用卡券失败: " + e.getMessage()));
        }
    }
}
