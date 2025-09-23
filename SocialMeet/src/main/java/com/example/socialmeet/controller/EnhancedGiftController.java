package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.GiftDTO;
import com.example.socialmeet.dto.GiftRecordDTO;
import com.example.socialmeet.entity.Gift;
import com.example.socialmeet.service.GiftService;
import com.example.socialmeet.service.VirtualCurrencyService;
import com.example.socialmeet.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 增强的礼物控制器
 */
@RestController
@RequestMapping("/api/enhanced-gifts")
@CrossOrigin(originPatterns = "*")
@Slf4j
public class EnhancedGiftController {
    
    @Autowired
    private GiftService giftService;
    
    @Autowired
    private VirtualCurrencyService virtualCurrencyService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取礼物列表 - 支持多种筛选条件
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<GiftDTO>>> getGifts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String subCategory,
            @RequestParam(required = false) String rarity,
            @RequestParam(required = false) Boolean isLimited,
            @RequestParam(required = false) Boolean isHot,
            @RequestParam(required = false) Boolean isNew,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "sortOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            // 构建排序
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // 这里需要实现增强的查询逻辑
            // 暂时使用原有的方法
            Page<GiftRecordDTO> gifts = giftService.getUserGiftHistory(0L, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            log.error("获取礼物列表失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取礼物列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取礼物分类
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getGiftCategories() {
        try {
            List<Map<String, String>> categories = List.of(
                Map.of("value", "EMOTION", "label", "情感表达", "icon", "❤️"),
                Map.of("value", "CELEBRATION", "label", "庆祝", "icon", "🎉"),
                Map.of("value", "ROMANCE", "label", "浪漫", "icon", "💕"),
                Map.of("value", "FRIENDSHIP", "label", "友谊", "icon", "🤝"),
                Map.of("value", "HOLIDAY", "label", "节日", "icon", "🎊"),
                Map.of("value", "SPECIAL", "label", "特殊", "icon", "⭐"),
                Map.of("value", "LIMITED", "label", "限量", "icon", "💎")
            );
            return ResponseEntity.ok(ApiResponse.success(categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取礼物分类失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取稀有度列表
     */
    @GetMapping("/rarities")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getGiftRarities() {
        try {
            List<Map<String, Object>> rarities = List.of(
                Map.of("value", "COMMON", "label", "普通", "level", 1, "color", "#8B8B8B"),
                Map.of("value", "RARE", "label", "稀有", "level", 2, "color", "#4A90E2"),
                Map.of("value", "EPIC", "label", "史诗", "level", 3, "color", "#9013FE"),
                Map.of("value", "LEGENDARY", "label", "传说", "level", 4, "color", "#FFD700")
            );
            return ResponseEntity.ok(ApiResponse.success(rarities));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取稀有度列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取热门礼物
     */
    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<GiftDTO>>> getHotGifts(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            // 这里需要实现获取热门礼物的逻辑
            List<GiftDTO> gifts = giftService.getAllGifts();
            if (gifts.size() > limit) {
                gifts = gifts.subList(0, limit);
            }
            return ResponseEntity.ok(ApiResponse.success(gifts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取热门礼物失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取新品礼物
     */
    @GetMapping("/new")
    public ResponseEntity<ApiResponse<List<GiftDTO>>> getNewGifts(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            // 这里需要实现获取新品礼物的逻辑
            List<GiftDTO> gifts = giftService.getAllGifts();
            if (gifts.size() > limit) {
                gifts = gifts.subList(0, limit);
            }
            return ResponseEntity.ok(ApiResponse.success(gifts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取新品礼物失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取限量礼物
     */
    @GetMapping("/limited")
    public ResponseEntity<ApiResponse<List<GiftDTO>>> getLimitedGifts() {
        try {
            // 这里需要实现获取限量礼物的逻辑
            List<GiftDTO> gifts = giftService.getAllGifts();
            return ResponseEntity.ok(ApiResponse.success(gifts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取限量礼物失败: " + e.getMessage()));
        }
    }
    
    /**
     * 搜索礼物
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<GiftDTO>>> searchGifts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            // 这里需要实现搜索逻辑
            List<GiftDTO> gifts = giftService.getAllGifts();
            return ResponseEntity.ok(ApiResponse.success(gifts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("搜索礼物失败: " + e.getMessage()));
        }
    }
    
    /**
     * 发送礼物 - 增强版
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<GiftRecordDTO>> sendGift(
            @RequestHeader("Authorization") String token,
            @RequestParam Long receiverId,
            @RequestParam Long giftId,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam(required = false) String message,
            @RequestParam(defaultValue = "COINS") String currencyType) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long senderId = jwtUtil.getUserIdFromToken(jwt);
            
            if (quantity <= 0) {
                return ResponseEntity.badRequest().body(ApiResponse.error("礼物数量必须大于0"));
            }
            
            // 获取礼物信息
            GiftDTO gift = giftService.getGiftById(giftId);
            if (gift == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("礼物不存在"));
            }
            
            // 计算总金额
            BigDecimal totalAmount = gift.getPrice().multiply(BigDecimal.valueOf(quantity));
            
            // 检查余额
            BigDecimal userBalance = virtualCurrencyService.getUserBalance(senderId, currencyType);
            if (userBalance.compareTo(totalAmount) < 0) {
                return ResponseEntity.badRequest().body(ApiResponse.error("余额不足"));
            }
            
            // 消费货币
            if (!virtualCurrencyService.consumeCurrency(senderId, currencyType, totalAmount, 
                    "发送礼物: " + gift.getName() + " x" + quantity, giftId, "GIFT")) {
                return ResponseEntity.badRequest().body(ApiResponse.error("扣费失败"));
            }
            
            // 发送礼物
            GiftRecordDTO giftRecord = giftService.sendGift(senderId, receiverId, giftId, quantity, message);
            
            return ResponseEntity.ok(ApiResponse.success(giftRecord));
        } catch (Exception e) {
            log.error("发送礼物失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("发送礼物失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户货币余额
     */
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getUserBalance(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Map<String, BigDecimal> balances = Map.of(
                "COINS", virtualCurrencyService.getUserBalance(userId, "COINS"),
                "DIAMONDS", virtualCurrencyService.getUserBalance(userId, "DIAMONDS"),
                "POINTS", virtualCurrencyService.getUserBalance(userId, "POINTS"),
                "GOLD", virtualCurrencyService.getUserBalance(userId, "GOLD")
            );
            
            return ResponseEntity.ok(ApiResponse.success(balances));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取余额失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取礼物特效
     */
    @GetMapping("/{giftId}/effects")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getGiftEffects(@PathVariable Long giftId) {
        try {
            // 这里需要实现获取礼物特效的逻辑
            List<Map<String, Object>> effects = List.of();
            return ResponseEntity.ok(ApiResponse.success(effects));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取礼物特效失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取礼物统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGiftStats(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Map<String, Object> stats = Map.of(
                "totalSent", giftService.getTotalSentAmount(userId),
                "totalReceived", giftService.getTotalReceivedAmount(userId),
                "currencyStats", virtualCurrencyService.getUserStats(userId)
            );
            
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取礼物统计失败: " + e.getMessage()));
        }
    }
}
