package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private DataSource dataSource;
    
    @PostMapping("/recharge/{userId}")
    public ResponseEntity<ApiResponse<String>> rechargeUser(@PathVariable Long userId, 
                                                           @RequestParam Double amount,
                                                           @RequestParam(required = false) String description) {
        try {
            String desc = description != null ? description : "管理员充值";
            
            // 1. 确保用户存在
            ensureUserExists(userId);
            
            // 2. 创建或更新钱包
            createOrUpdateWallet(userId, amount);
            
            // 3. 添加交易记录
            addTransactionRecord(userId, amount, desc);
            
            return ResponseEntity.ok(ApiResponse.success("充值成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("充值失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/balance/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserBalance(@PathVariable Long userId) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 查询用户信息
            Map<String, Object> userInfo = getUserInfo(userId);
            result.put("user", userInfo);
            
            // 查询钱包信息
            Map<String, Object> walletInfo = getWalletInfo(userId);
            result.put("wallet", walletInfo);
            
            // 查询交易记录
            List<Map<String, Object>> transactions = getTransactions(userId);
            result.put("transactions", transactions);
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
    
    private void ensureUserExists(Long userId) throws SQLException {
        String sql = "INSERT INTO users (id, username, phone, nickname, gender, is_active, is_online, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) " +
                    "ON DUPLICATE KEY UPDATE username = username";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setString(2, "user_" + userId);
            stmt.setString(3, "13800138008");
            stmt.setString(4, "神秘小猫咪887");
            stmt.setString(5, "FEMALE");
            stmt.setBoolean(6, true);
            stmt.setBoolean(7, true);
            stmt.executeUpdate();
        }
    }
    
    private void createOrUpdateWallet(Long userId, Double amount) throws SQLException {
        String sql = "INSERT INTO wallets (user_id, balance, frozen_amount, currency, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) " +
                    "ON DUPLICATE KEY UPDATE balance = ?, updated_at = CURRENT_TIMESTAMP";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setDouble(2, amount);
            stmt.setDouble(3, 0.0);
            stmt.setString(4, "CNY");
            stmt.setDouble(5, amount);
            stmt.executeUpdate();
        }
    }
    
    private void addTransactionRecord(Long userId, Double amount, String description) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, type, amount, balance_after, description, status, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setString(2, "RECHARGE");
            stmt.setDouble(3, amount);
            stmt.setDouble(4, amount);
            stmt.setString(5, description);
            stmt.setString(6, "SUCCESS");
            stmt.executeUpdate();
        }
    }
    
    private Map<String, Object> getUserInfo(Long userId) throws SQLException {
        String sql = "SELECT id, username, phone, nickname FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            Map<String, Object> userInfo = new HashMap<>();
            if (rs.next()) {
                userInfo.put("id", rs.getLong("id"));
                userInfo.put("username", rs.getString("username"));
                userInfo.put("phone", rs.getString("phone"));
                userInfo.put("nickname", rs.getString("nickname"));
            }
            return userInfo;
        }
    }
    
    private Map<String, Object> getWalletInfo(Long userId) throws SQLException {
        String sql = "SELECT user_id, balance, frozen_amount, currency FROM wallets WHERE user_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            Map<String, Object> walletInfo = new HashMap<>();
            if (rs.next()) {
                walletInfo.put("userId", rs.getLong("user_id"));
                walletInfo.put("balance", rs.getDouble("balance"));
                walletInfo.put("frozenAmount", rs.getDouble("frozen_amount"));
                walletInfo.put("currency", rs.getString("currency"));
            }
            return walletInfo;
        }
    }
    
    private List<Map<String, Object>> getTransactions(Long userId) throws SQLException {
        String sql = "SELECT id, user_id, type, amount, balance_after, description, status FROM transactions WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            List<Map<String, Object>> transactions = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("id", rs.getLong("id"));
                transaction.put("userId", rs.getLong("user_id"));
                transaction.put("type", rs.getString("type"));
                transaction.put("amount", rs.getDouble("amount"));
                transaction.put("balanceAfter", rs.getDouble("balance_after"));
                transaction.put("description", rs.getString("description"));
                transaction.put("status", rs.getString("status"));
                transactions.add(transaction);
            }
            return transactions;
        }
    }
}
