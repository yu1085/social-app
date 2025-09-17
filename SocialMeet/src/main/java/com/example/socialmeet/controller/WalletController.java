package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.TransactionDTO;
import com.example.socialmeet.dto.WalletDTO;
import com.example.socialmeet.entity.Transaction;
import com.example.socialmeet.repository.TransactionRepository;
import com.example.socialmeet.service.WalletService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<WalletDTO>> getBalance(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            WalletDTO wallet = walletService.getWalletByUserId(userId);
            if (wallet == null) {
                wallet = walletService.createWallet(userId);
            }
            
            return ResponseEntity.ok(ApiResponse.success(wallet));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取钱包余额失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/recharge")
    public ResponseEntity<ApiResponse<String>> recharge(@RequestHeader("Authorization") String token,
                                                       @RequestParam BigDecimal amount,
                                                       @RequestParam(required = false) String description) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(ApiResponse.error("充值金额必须大于0"));
            }
            
            String desc = description != null ? description : "用户充值";
            boolean success = walletService.recharge(userId, amount, desc);
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("充值成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("充值失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("充值失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getTransactions(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Transaction> transactions = transactionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
            
            Page<TransactionDTO> transactionDTOs = transactions.map(TransactionDTO::new);
            
            return ResponseEntity.ok(ApiResponse.success(transactionDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取交易记录失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/transactions/type/{type}")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByType(
            @RequestHeader("Authorization") String token,
            @PathVariable String type) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            String transactionType = String.valueOf(type.toUpperCase());
            List<Transaction> transactions = transactionRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, transactionType);
            
            List<TransactionDTO> transactionDTOs = transactions.stream()
                    .map(TransactionDTO::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(transactionDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取交易记录失败: " + e.getMessage()));
        }
    }
    
    // 管理员API：直接通过用户ID操作钱包
    @GetMapping("/admin/balance/{userId}")
    public ResponseEntity<ApiResponse<WalletDTO>> getBalanceByUserId(@PathVariable Long userId) {
        try {
            WalletDTO wallet = walletService.getWalletByUserId(userId);
            if (wallet == null) {
                wallet = walletService.createWallet(userId);
            }
            
            return ResponseEntity.ok(ApiResponse.success(wallet));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取钱包余额失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/admin/recharge/{userId}")
    public ResponseEntity<ApiResponse<String>> rechargeByUserId(@PathVariable Long userId,
                                                               @RequestParam BigDecimal amount,
                                                               @RequestParam(required = false) String description) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(ApiResponse.error("充值金额必须大于0"));
            }
            
            String desc = description != null ? description : "管理员充值";
            boolean success = walletService.recharge(userId, amount, desc);
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("充值成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("充值失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("充值失败: " + e.getMessage()));
        }
    }
}
