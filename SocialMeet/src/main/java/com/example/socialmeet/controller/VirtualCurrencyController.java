package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.entity.CurrencyTransaction;
import com.example.socialmeet.service.VirtualCurrencyService;
import com.example.socialmeet.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 虚拟货币控制器
 */
@RestController
@RequestMapping("/api/currency")
@CrossOrigin(originPatterns = "*")
@Slf4j
public class VirtualCurrencyController {
    
    @Autowired
    private VirtualCurrencyService virtualCurrencyService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取用户余额
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
            log.error("获取用户余额失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户余额失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取指定货币余额
     */
    @GetMapping("/balance/{currencyType}")
    public ResponseEntity<ApiResponse<BigDecimal>> getCurrencyBalance(
            @RequestHeader("Authorization") String token,
            @PathVariable String currencyType) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            BigDecimal balance = virtualCurrencyService.getUserBalance(userId, currencyType);
            return ResponseEntity.ok(ApiResponse.success(balance));
        } catch (Exception e) {
            log.error("获取货币余额失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取货币余额失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取交易记录
     */
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<CurrencyTransaction>>> getTransactions(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String currencyType) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<CurrencyTransaction> transactions;
            
            if (currencyType != null && !currencyType.isEmpty()) {
                transactions = virtualCurrencyService.getUserTransactionsByCurrency(userId, currencyType, pageable);
            } else {
                transactions = virtualCurrencyService.getUserTransactions(userId, pageable);
            }
            
            return ResponseEntity.ok(ApiResponse.success(transactions));
        } catch (Exception e) {
            log.error("获取交易记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取交易记录失败: " + e.getMessage()));
        }
    }
    
    /**
     * 转账
     */
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Boolean>> transferCurrency(
            @RequestHeader("Authorization") String token,
            @RequestParam Long toUserId,
            @RequestParam String currencyType,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long fromUserId = jwtUtil.getUserIdFromToken(jwt);
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(ApiResponse.error("转账金额必须大于0"));
            }
            
            String transferDescription = description != null ? description : "转账给用户" + toUserId;
            boolean success = virtualCurrencyService.transferCurrency(fromUserId, toUserId, currencyType, amount, transferDescription);
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success(true));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("转账失败"));
            }
        } catch (Exception e) {
            log.error("转账失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("转账失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Map<String, Object> stats = virtualCurrencyService.getUserStats(userId);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            log.error("获取用户统计失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户统计失败: " + e.getMessage()));
        }
    }
}
