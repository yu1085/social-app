package com.socialmeet.backend.controller;

import com.socialmeet.backend.dto.ApiResponse;
import com.socialmeet.backend.dto.LuckyNumberDTO;
import com.socialmeet.backend.dto.PurchaseRequest;
import com.socialmeet.backend.security.JwtUtil;
import com.socialmeet.backend.service.PropMallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 道具商城控制器
 */
@RestController
@RequestMapping("/api/prop-mall")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PropMallController {
    
    private final PropMallService propMallService;
    private final JwtUtil jwtUtil;
    
    /**
     * 获取可购买的靓号列表
     */
    @GetMapping("/lucky-numbers")
    public ApiResponse<Page<LuckyNumberDTO>> getAvailableLuckyNumbers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.info("获取可购买的靓号列表 - page: {}, size: {}", page, size);
            
            Page<LuckyNumberDTO> luckyNumbers = propMallService.getAvailableLuckyNumbers(page, size);
            
            return ApiResponse.success("获取成功", luckyNumbers);
            
        } catch (Exception e) {
            log.error("获取靓号列表失败", e);
            return ApiResponse.error("获取靓号列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据等级获取靓号列表
     */
    @GetMapping("/lucky-numbers/tier/{tier}")
    public ApiResponse<Page<LuckyNumberDTO>> getLuckyNumbersByTier(
            @PathVariable String tier,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.info("根据等级获取靓号列表 - tier: {}, page: {}, size: {}", tier, page, size);
            
            Page<LuckyNumberDTO> luckyNumbers = propMallService.getLuckyNumbersByTier(tier, page, size);
            
            return ApiResponse.success("获取成功", luckyNumbers);
            
        } catch (Exception e) {
            log.error("根据等级获取靓号列表失败", e);
            return ApiResponse.error("获取靓号列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据价格范围获取靓号列表
     */
    @GetMapping("/lucky-numbers/price-range")
    public ApiResponse<Page<LuckyNumberDTO>> getLuckyNumbersByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.info("根据价格范围获取靓号列表 - minPrice: {}, maxPrice: {}, page: {}, size: {}", minPrice, maxPrice, page, size);
            
            Page<LuckyNumberDTO> luckyNumbers = propMallService.getLuckyNumbersByPriceRange(minPrice, maxPrice, page, size);
            
            return ApiResponse.success("获取成功", luckyNumbers);
            
        } catch (Exception e) {
            log.error("根据价格范围获取靓号列表失败", e);
            return ApiResponse.error("获取靓号列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取特殊靓号列表
     */
    @GetMapping("/lucky-numbers/special")
    public ApiResponse<Page<LuckyNumberDTO>> getSpecialLuckyNumbers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.info("获取特殊靓号列表 - page: {}, size: {}", page, size);
            
            Page<LuckyNumberDTO> luckyNumbers = propMallService.getSpecialLuckyNumbers(page, size);
            
            return ApiResponse.success("获取成功", luckyNumbers);
            
        } catch (Exception e) {
            log.error("获取特殊靓号列表失败", e);
            return ApiResponse.error("获取靓号列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 购买靓号
     */
    @PostMapping("/lucky-numbers/purchase")
    public ApiResponse<LuckyNumberDTO> purchaseLuckyNumber(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PurchaseRequest request) {
        try {
            log.info("购买靓号 - luckyNumberId: {}", request.getLuckyNumberId());
            
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            LuckyNumberDTO luckyNumber = propMallService.purchaseLuckyNumber(userId, request);
            
            return ApiResponse.success("购买成功", luckyNumber);
            
        } catch (Exception e) {
            log.error("购买靓号失败", e);
            return ApiResponse.error("购买失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户拥有的靓号
     */
    @GetMapping("/my-lucky-numbers")
    public ApiResponse<List<LuckyNumberDTO>> getUserLuckyNumbers(
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("获取用户拥有的靓号");
            
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            List<LuckyNumberDTO> luckyNumbers = propMallService.getUserLuckyNumbers(userId);
            
            return ApiResponse.success("获取成功", luckyNumbers);
            
        } catch (Exception e) {
            log.error("获取用户靓号失败", e);
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取靓号详情
     */
    @GetMapping("/lucky-numbers/{id}")
    public ApiResponse<LuckyNumberDTO> getLuckyNumberDetail(@PathVariable Long id) {
        try {
            log.info("获取靓号详情 - id: {}", id);
            
            LuckyNumberDTO luckyNumber = propMallService.getLuckyNumberDetail(id);
            
            return ApiResponse.success("获取成功", luckyNumber);
            
        } catch (Exception e) {
            log.error("获取靓号详情失败", e);
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查靓号是否可用
     */
    @GetMapping("/lucky-numbers/check-availability")
    public ApiResponse<Boolean> checkLuckyNumberAvailability(@RequestParam String number) {
        try {
            log.info("检查靓号是否可用 - number: {}", number);
            
            boolean isAvailable = propMallService.isLuckyNumberAvailable(number);
            
            return ApiResponse.success("检查完成", isAvailable);
            
        } catch (Exception e) {
            log.error("检查靓号可用性失败", e);
            return ApiResponse.error("检查失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户靓号统计
     */
    @GetMapping("/my-lucky-numbers/stats")
    public ApiResponse<Object> getUserLuckyNumberStats(
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("获取用户靓号统计");
            
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            Object stats = propMallService.getUserLuckyNumberStats(userId);
            
            return ApiResponse.success("获取成功", stats);
            
        } catch (Exception e) {
            log.error("获取用户靓号统计失败", e);
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }
}
