package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.entity.RechargePackage;
import com.example.socialmeet.repository.RechargePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 充值套餐控制器
 */
@RestController
@RequestMapping("/api/recharge/packages")
@CrossOrigin(originPatterns = "*")
public class RechargePackageController {
    
    @Autowired
    private RechargePackageRepository packageRepository;
    
    /**
     * 获取所有可用的充值套餐
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RechargePackage>>> getAllPackages() {
        try {
            List<RechargePackage> packages = packageRepository.findByIsActiveTrueOrderBySortOrder();
            return ResponseEntity.ok(ApiResponse.success(packages));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取充值套餐失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取充值套餐
     */
    @GetMapping("/{packageId}")
    public ResponseEntity<ApiResponse<RechargePackage>> getPackage(@PathVariable String packageId) {
        try {
            RechargePackage rechargePackage = packageRepository.findById(packageId).orElse(null);
            if (rechargePackage == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ApiResponse.success(rechargePackage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取充值套餐失败: " + e.getMessage()));
        }
    }
}
