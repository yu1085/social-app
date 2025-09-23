package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.entity.LuckyNumber;
import com.example.socialmeet.repository.LuckyNumberRepository;
import com.example.socialmeet.service.WalletService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lucky-numbers")
@CrossOrigin(originPatterns = "*")
public class LuckyNumberController {
    
    @Autowired
    private LuckyNumberRepository luckyNumberRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private WalletService walletService;
    
    /**
     * 获取靓号列表
     */
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<LuckyNumber>>> getLuckyNumbers(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tier,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Boolean isLimited,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<LuckyNumber> luckyNumbers;
            
            // 根据参数筛选和排序
            if (tier != null) {
                LuckyNumber.LuckyNumberTier tierEnum = LuckyNumber.LuckyNumberTier.valueOf(tier.toUpperCase());
                luckyNumbers = luckyNumberRepository.findByTierAndIsAvailableTrueOrderByPriceAsc(tierEnum);
            } else if (isLimited != null && isLimited) {
                luckyNumbers = luckyNumberRepository.findByIsLimitedTrueOrderByTierAscPriceAsc();
            } else if (minPrice != null && maxPrice != null) {
                luckyNumbers = luckyNumberRepository.findByPriceRange(minPrice, maxPrice);
            } else {
                // 默认按等级排序：限量-顶级-超级-普通
                luckyNumbers = luckyNumberRepository.findAllOrderByTierAndPrice();
            }
            
            return ResponseEntity.ok(ApiResponse.success(luckyNumbers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取靓号列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取靓号详情
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<ApiResponse<LuckyNumber>> getLuckyNumberById(@PathVariable Long id) {
        try {
            Optional<LuckyNumber> luckyNumber = luckyNumberRepository.findById(id);
            if (luckyNumber.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(luckyNumber.get()));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("靓号不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取靓号详情失败: " + e.getMessage()));
        }
    }
    
    /**
     * 购买靓号
     */
    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<LuckyNumber>> purchaseLuckyNumber(
            @RequestHeader("Authorization") String token,
            @RequestBody PurchaseRequest request) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            // 查找靓号
            Optional<LuckyNumber> luckyNumberOpt = luckyNumberRepository.findById(request.getItemId());
            if (!luckyNumberOpt.isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("靓号不存在"));
            }
            
            LuckyNumber luckyNumber = luckyNumberOpt.get();
            if (!luckyNumber.getIsAvailable()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("靓号已售出"));
            }
            
            // 计算最终价格（考虑折扣）
            Long finalPrice = request.getPrice() != null ? request.getPrice() : luckyNumber.getPrice();
            
            // 检查用户余额
            if (!walletService.hasEnoughBalance(userId, finalPrice)) {
                return ResponseEntity.status(422).body(ApiResponse.error("余额不足"));
            }
            
            // 扣除用户金币
            boolean deductSuccess = walletService.consume(userId, finalPrice, 
                "购买靓号: " + luckyNumber.getNumber(), luckyNumber.getId());
            
            if (!deductSuccess) {
                return ResponseEntity.status(422).body(ApiResponse.error("扣费失败"));
            }
            
            // 更新靓号状态为已售出
            luckyNumber.setIsAvailable(false);
            luckyNumberRepository.save(luckyNumber);
            
            return ResponseEntity.ok(ApiResponse.success(luckyNumber));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("购买失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取我的靓号
     */
    @GetMapping("/my-items")
    public ResponseEntity<ApiResponse<List<LuckyNumber>>> getMyLuckyNumbers(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String type) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            // TODO: 根据用户ID查找用户拥有的靓号
            // 暂时返回空列表
            List<LuckyNumber> myLuckyNumbers = List.of();
            
            return ResponseEntity.ok(ApiResponse.success(myLuckyNumbers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取我的靓号失败: " + e.getMessage()));
        }
    }
    
    /**
     * 购买请求
     */
    public static class PurchaseRequest {
        private Long itemId;
        private String itemType;
        private Long price; // 最终价格（考虑折扣后）
        
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        
        public String getItemType() { return itemType; }
        public void setItemType(String itemType) { this.itemType = itemType; }
        
        public Long getPrice() { return price; }
        public void setPrice(Long price) { this.price = price; }
    }
}
