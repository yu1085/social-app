package com.example.socialmeet.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 数据库修复工具 - 直接修复 device_tokens 表中的无效日期时间值
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
public class DatabaseFixRunner {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/socialmeet?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=convertToNull&autoReconnect=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";
    
    public static void main(String[] args) {
        System.out.println("开始修复 device_tokens 表中的无效日期时间值...");
        
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            // 1. 检查总记录数量
            String countQuery = "SELECT COUNT(*) as total_records FROM device_tokens";
            
            try (PreparedStatement stmt = connection.prepareStatement(countQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    int totalRecords = rs.getInt("total_records");
                    System.out.println("device_tokens 表总记录数: " + totalRecords);
                    
                    if (totalRecords == 0) {
                        System.out.println("✅ 表中没有记录，无需修复");
                        return;
                    }
                }
            }
            
            // 2. 直接修复所有记录的日期时间字段
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // 修复 created_at 字段 - 使用更安全的WHERE条件
            String fixCreatedAtQuery = "UPDATE device_tokens SET created_at = ? WHERE created_at IS NULL OR created_at < '1900-01-01'";
            
            try (PreparedStatement stmt = connection.prepareStatement(fixCreatedAtQuery)) {
                stmt.setString(1, currentTime);
                int updatedCreatedAt = stmt.executeUpdate();
                System.out.println("✅ 修复了 " + updatedCreatedAt + " 条记录的 created_at 字段");
            }
            
            // 修复 updated_at 字段
            String fixUpdatedAtQuery = "UPDATE device_tokens SET updated_at = ? WHERE updated_at IS NULL OR updated_at < '1900-01-01'";
            
            try (PreparedStatement stmt = connection.prepareStatement(fixUpdatedAtQuery)) {
                stmt.setString(1, currentTime);
                int updatedUpdatedAt = stmt.executeUpdate();
                System.out.println("✅ 修复了 " + updatedUpdatedAt + " 条记录的 updated_at 字段");
            }
            
            // 4. 修改表结构，设置默认值
            System.out.println("修改表结构，设置默认值...");
            
            try (Statement stmt = connection.createStatement()) {
                // 修改 created_at 字段
                String alterCreatedAtSql = "ALTER TABLE device_tokens MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT '2000-01-01 00:00:01'";
                stmt.execute(alterCreatedAtSql);
                System.out.println("✅ 修改 created_at 字段为 NOT NULL DEFAULT '2000-01-01 00:00:01'");
                
                // 修改 updated_at 字段
                String alterUpdatedAtSql = "ALTER TABLE device_tokens MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP";
                stmt.execute(alterUpdatedAtSql);
                System.out.println("✅ 修改 updated_at 字段为 NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");
            }
            
            System.out.println("🎉 数据库修复完成！");
            
        } catch (Exception e) {
            System.err.println("❌ 数据库修复失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
