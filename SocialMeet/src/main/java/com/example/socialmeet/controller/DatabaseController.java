package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/admin/database/fix")
@CrossOrigin(originPatterns = "*")
public class DatabaseController {
    
    @Autowired
    private DataSource dataSource;
    
    @PostMapping("/init")
    public ResponseEntity<ApiResponse<String>> initDatabase() {
        try {
            System.out.println("=== 数据库初始化请求 ===");
            
            // 测试数据库连接
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("数据库连接成功");
                System.out.println("数据库URL: " + connection.getMetaData().getURL());
                System.out.println("数据库产品: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("数据库版本: " + connection.getMetaData().getDatabaseProductVersion());
                
                return ResponseEntity.ok(ApiResponse.success("数据库连接正常"));
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("数据库连接失败: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("数据库初始化失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> testDatabase() {
        try {
            System.out.println("=== 数据库测试请求 ===");
            
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("数据库连接测试成功");
                System.out.println("数据库URL: " + connection.getMetaData().getURL());
                System.out.println("数据库产品: " + connection.getMetaData().getDatabaseProductName());
                return ResponseEntity.ok(ApiResponse.success("数据库连接测试成功"));
            }
        } catch (SQLException e) {
            System.err.println("数据库连接测试失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("数据库连接测试失败: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("数据库测试失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("数据库测试失败: " + e.getMessage()));
        }
    }
}
