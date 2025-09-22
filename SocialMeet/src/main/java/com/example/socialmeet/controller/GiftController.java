package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.GiftDTO;
import com.example.socialmeet.dto.GiftRecordDTO;
import com.example.socialmeet.entity.Gift;
import com.example.socialmeet.service.GiftService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/gifts")
@CrossOrigin(originPatterns = "*")
public class GiftController {
    
    @Autowired
    private GiftService giftService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<GiftDTO>>> getAllGifts() {
        try {
            List<GiftDTO> gifts = giftService.getAllGifts();
            return ResponseEntity.ok(ApiResponse.success(gifts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取礼物列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<GiftDTO>>> getGiftsByCategory(@PathVariable String category) {
        try {
            String giftCategory = String.valueOf(category.toUpperCase());
            List<GiftDTO> gifts = giftService.getGiftsByCategory(giftCategory);
            return ResponseEntity.ok(ApiResponse.success(gifts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取礼物列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<List<GiftDTO>>> getGiftsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        try {
            List<GiftDTO> gifts = giftService.getGiftsByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(ApiResponse.success(gifts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取礼物列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GiftDTO>> getGiftById(@PathVariable Long id) {
        try {
            GiftDTO gift = giftService.getGiftById(id);
            if (gift == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("礼物不存在"));
            }
            return ResponseEntity.ok(ApiResponse.success(gift));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取礼物详情失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<GiftRecordDTO>> sendGift(
            @RequestHeader("Authorization") String token,
            @RequestParam Long receiverId,
            @RequestParam Long giftId,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam(required = false) String message) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long senderId = jwtUtil.getUserIdFromToken(jwt);
            
            if (quantity <= 0) {
                return ResponseEntity.badRequest().body(ApiResponse.error("礼物数量必须大于0"));
            }
            
            GiftRecordDTO giftRecord = giftService.sendGift(senderId, receiverId, giftId, quantity, message);
            return ResponseEntity.ok(ApiResponse.success(giftRecord));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("发送礼物失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<Page<GiftRecordDTO>>> getSentGifts(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long senderId = jwtUtil.getUserIdFromToken(jwt);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<GiftRecordDTO> gifts = giftService.getSentGifts(senderId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(gifts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取发送的礼物失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<Page<GiftRecordDTO>>> getReceivedGifts(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long receiverId = jwtUtil.getUserIdFromToken(jwt);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<GiftRecordDTO> gifts = giftService.getReceivedGifts(receiverId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(gifts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取收到的礼物失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<GiftRecordDTO>>> getGiftHistory(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<GiftRecordDTO> gifts = giftService.getUserGiftHistory(userId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(gifts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取礼物历史失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats/sent-amount")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalSentAmount(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            BigDecimal amount = giftService.getTotalSentAmount(userId);
            return ResponseEntity.ok(ApiResponse.success(amount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取发送总额失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats/received-amount")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalReceivedAmount(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            BigDecimal amount = giftService.getTotalReceivedAmount(userId);
            return ResponseEntity.ok(ApiResponse.success(amount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取接收总额失败: " + e.getMessage()));
        }
    }
}
