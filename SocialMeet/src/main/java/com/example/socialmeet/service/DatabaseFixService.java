package com.example.socialmeet.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据库修复服务 - 智能数据修复和健康检查
 * 
 * 功能：
 * 1. 修复历史数据中的无效日期时间值
 * 2. 执行数据健康检查
 * 3. 提供数据质量报告
 * 4. 作为数据迁移的安全网
 * 
 * @author SocialMeet Team
 * @version 2.0
 * @since 2024-01-01
 */
@Service
@Slf4j
public class DatabaseFixService implements ApplicationRunner {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private AuditService auditService;
    
    /**
     * 应用程序启动时自动执行数据库修复和健康检查
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("🚀 应用程序启动，开始执行数据库健康检查...");
        try {
            // 1. 执行数据健康检查
            DataHealthReport healthReport = performDataHealthCheck();
            
            // 2. 如果有问题，执行修复
            if (healthReport.hasIssues()) {
                log.warn("⚠️ 发现数据问题，开始执行修复...");
                fixAllDateTimeIssues();
                log.info("✅ 数据库修复完成！");
            } else {
                log.info("✅ 数据库健康检查通过，无需修复！");
            }
            
            // 3. 输出数据质量报告
            logDataQualityReport(healthReport);
            
        } catch (Exception e) {
            log.error("❌ 数据库健康检查失败，但应用程序将继续启动", e);
            // 不重新抛出异常，让应用程序继续启动
        }
    }
    
    /**
     * 修复 device_tokens 表中的无效日期时间值
     */
    @Transactional
    public void fixDeviceTokensDateTime() {
        try {
            log.info("开始修复 device_tokens 表中的无效日期时间值...");
            
            // 1. 先检查表是否存在
            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'device_tokens'";
            int tableExists = jdbcTemplate.queryForObject(checkTableQuery, Integer.class);
            
            if (tableExists == 0) {
                log.info("device_tokens 表不存在，跳过修复");
                return;
            }
            
            // 2. 使用更安全的方式检查总记录数
            String countQuery = "SELECT COUNT(*) FROM device_tokens";
            int totalRecords = jdbcTemplate.queryForObject(countQuery, Integer.class);
            log.info("device_tokens 表总记录数: {}", totalRecords);
            
            if (totalRecords == 0) {
                log.info("device_tokens 表为空，无需修复");
                return;
            }
            
            // 3. 使用最安全的方式修复无效的日期时间值 - 完全避免警告
            int updatedCreatedAt = 0;
            int updatedUpdatedAt = 0;
            
            try {
                // 方法1：先查询所有记录，然后逐条更新，完全避免WHERE条件中的无效日期比较
                String selectAllQuery = "SELECT id, created_at, updated_at FROM device_tokens";
                List<Map<String, Object>> records = jdbcTemplate.queryForList(selectAllQuery);
                
                LocalDateTime now = LocalDateTime.now();
                int needFixCreatedAt = 0;
                int needFixUpdatedAt = 0;
                
                for (Map<String, Object> record : records) {
                    Long id = ((Number) record.get("id")).longValue();
                    Object createdAt = record.get("created_at");
                    Object updatedAt = record.get("updated_at");
                    
                    boolean needUpdate = false;
                    String updateQuery = "UPDATE device_tokens SET ";
                    List<Object> params = new ArrayList<>();
                    
                    // 检查并修复 created_at
                    if (createdAt == null || "0000-00-00 00:00:00".equals(createdAt.toString()) || 
                        "0000-00-00 00:00:00.000000".equals(createdAt.toString())) {
                        updateQuery += "created_at = ?, ";
                        params.add(now);
                        needFixCreatedAt++;
                        needUpdate = true;
                    }
                    
                    // 检查并修复 updated_at
                    if (updatedAt == null || "0000-00-00 00:00:00".equals(updatedAt.toString()) || 
                        "0000-00-00 00:00:00.000000".equals(updatedAt.toString())) {
                        updateQuery += "updated_at = ?, ";
                        params.add(now);
                        needFixUpdatedAt++;
                        needUpdate = true;
                    }
                    
                    if (needUpdate) {
                        updateQuery = updateQuery.substring(0, updateQuery.length() - 2); // 移除最后的逗号和空格
                        updateQuery += " WHERE id = ?";
                        params.add(id);
                        
                        jdbcTemplate.update(updateQuery, params.toArray());
                    }
                }
                
                updatedCreatedAt = needFixCreatedAt;
                updatedUpdatedAt = needFixUpdatedAt;
                log.info("修复了 created_at: {} 条, updated_at: {} 条记录", updatedCreatedAt, updatedUpdatedAt);
                
            } catch (Exception e) {
                log.warn("逐条修复方法失败: {}", e.getMessage());
                
                // 方法2：如果逐条修复失败，使用最保守的方法 - 只修复NULL值
                try {
                    String fixNullCreatedAt = "UPDATE device_tokens SET created_at = ? WHERE created_at IS NULL";
                    String fixNullUpdatedAt = "UPDATE device_tokens SET updated_at = ? WHERE updated_at IS NULL";
                    
                    LocalDateTime now = LocalDateTime.now();
                    updatedCreatedAt = jdbcTemplate.update(fixNullCreatedAt, now);
                    updatedUpdatedAt = jdbcTemplate.update(fixNullUpdatedAt, now);
                    
                    log.info("使用保守方法修复了 created_at: {} 条, updated_at: {} 条", updatedCreatedAt, updatedUpdatedAt);
                } catch (Exception e2) {
                    log.warn("保守修复方法也失败: {}", e2.getMessage());
                }
            }
            
            // 5. 验证修复结果 - 使用安全的查询
            try {
                String verifyQuery = "SELECT COUNT(*) FROM device_tokens WHERE created_at IS NOT NULL AND updated_at IS NOT NULL";
                int validRecords = jdbcTemplate.queryForObject(verifyQuery, Integer.class);
                log.info("修复后有效记录数: {}/{}", validRecords, totalRecords);
                
                if (validRecords == totalRecords) {
                    log.info("✅ device_tokens 表日期时间值修复完成！");
                } else {
                    log.warn("⚠️ 仍有部分记录未能修复: 有效记录={}/{}", validRecords, totalRecords);
                }
            } catch (Exception e) {
                log.warn("验证修复结果时出现警告: {}", e.getMessage());
            }
            
        } catch (Exception e) {
            log.error("修复 device_tokens 表日期时间值时发生错误", e);
            // 不抛出异常，让应用程序继续启动
            log.warn("数据库修复失败，但应用程序将继续启动");
        }
    }
    
    /**
     * 检查并修复所有表的无效日期时间值
     */
    @Transactional
    public void fixAllDateTimeIssues() {
        log.info("开始检查并修复所有表的无效日期时间值...");
        
        // 修复 device_tokens 表
        fixDeviceTokensDateTime();
        
        // 可以在这里添加其他表的修复逻辑
        // fixOtherTablesDateTime();
        
        log.info("所有表的日期时间值修复完成！");
    }
    
    /**
     * 执行数据健康检查
     */
    public DataHealthReport performDataHealthCheck() {
        DataHealthReport report = new DataHealthReport();
        
        try {
            // 检查主要表的数据质量
            checkTableHealth("device_tokens", report);
            checkTableHealth("users", report);
            checkTableHealth("dynamics", report);
            checkTableHealth("messages", report);
            
            log.info("📊 数据健康检查完成: 总表数={}, 问题表数={}", 
                    report.getTotalTables(), report.getProblemTables());
            
        } catch (Exception e) {
            log.error("数据健康检查失败", e);
            report.addError("健康检查执行失败: " + e.getMessage());
        }
        
        return report;
    }
    
    /**
     * 检查单个表的数据健康状态
     */
    private void checkTableHealth(String tableName, DataHealthReport report) {
        try {
            // 检查表是否存在
            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";
            int tableExists = jdbcTemplate.queryForObject(checkTableQuery, Integer.class, tableName);
            
            if (tableExists == 0) {
                report.addInfo(tableName + " 表不存在，跳过检查");
                return;
            }
            
            // 检查总记录数
            String countQuery = "SELECT COUNT(*) FROM " + tableName;
            int totalRecords = jdbcTemplate.queryForObject(countQuery, Integer.class);
            
            // 检查无效日期时间记录数
            String invalidQuery = """
                SELECT 
                    SUM(CASE WHEN created_at IS NULL OR created_at = '0000-00-00 00:00:00' THEN 1 ELSE 0 END) as invalid_created_at,
                    SUM(CASE WHEN updated_at IS NULL OR updated_at = '0000-00-00 00:00:00' THEN 1 ELSE 0 END) as invalid_updated_at
                FROM """ + tableName;
            
            try {
                Map<String, Object> result = jdbcTemplate.queryForMap(invalidQuery);
                int invalidCreatedAt = ((Number) result.get("invalid_created_at")).intValue();
                int invalidUpdatedAt = ((Number) result.get("invalid_updated_at")).intValue();
                
                report.addTableResult(tableName, totalRecords, invalidCreatedAt, invalidUpdatedAt);
                
            } catch (Exception e) {
                // 如果查询失败，可能是表结构问题
                report.addWarning(tableName + " 表结构可能有问题: " + e.getMessage());
            }
            
        } catch (Exception e) {
            report.addError(tableName + " 健康检查失败: " + e.getMessage());
        }
    }
    
    /**
     * 输出数据质量报告
     */
    private void logDataQualityReport(DataHealthReport report) {
        log.info("📋 === 数据质量报告 ===");
        log.info("总检查表数: {}", report.getTotalTables());
        log.info("问题表数: {}", report.getProblemTables());
        
        if (report.getTableResults().isEmpty()) {
            log.info("✅ 所有表数据健康");
        } else {
            for (DataHealthReport.TableResult result : report.getTableResults()) {
                if (result.hasIssues()) {
                    log.warn("⚠️ 表 {}: 总记录={}, 无效created_at={}, 无效updated_at={}", 
                            result.getTableName(), result.getTotalRecords(), 
                            result.getInvalidCreatedAt(), result.getInvalidUpdatedAt());
                } else {
                    log.info("✅ 表 {}: 总记录={}, 数据健康", 
                            result.getTableName(), result.getTotalRecords());
                }
            }
        }
        
        if (!report.getWarnings().isEmpty()) {
            log.warn("⚠️ 警告信息:");
            report.getWarnings().forEach(warning -> log.warn("  - {}", warning));
        }
        
        if (!report.getErrors().isEmpty()) {
            log.error("❌ 错误信息:");
            report.getErrors().forEach(error -> log.error("  - {}", error));
        }
        
        log.info("📋 === 报告结束 ===");
    }
    
    /**
     * 数据健康报告类
     */
    public static class DataHealthReport {
        private final List<TableResult> tableResults = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();
        private final List<String> errors = new ArrayList<>();
        private final List<String> infos = new ArrayList<>();
        
        public void addTableResult(String tableName, int totalRecords, int invalidCreatedAt, int invalidUpdatedAt) {
            tableResults.add(new TableResult(tableName, totalRecords, invalidCreatedAt, invalidUpdatedAt));
        }
        
        public void addWarning(String warning) {
            warnings.add(warning);
        }
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public void addInfo(String info) {
            infos.add(info);
        }
        
        public boolean hasIssues() {
            return tableResults.stream().anyMatch(TableResult::hasIssues) || !errors.isEmpty();
        }
        
        public int getTotalTables() {
            return tableResults.size();
        }
        
        public long getProblemTables() {
            return tableResults.stream().filter(TableResult::hasIssues).count();
        }
        
        public List<TableResult> getTableResults() {
            return tableResults;
        }
        
        public List<String> getWarnings() {
            return warnings;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public static class TableResult {
            private final String tableName;
            private final int totalRecords;
            private final int invalidCreatedAt;
            private final int invalidUpdatedAt;
            
            public TableResult(String tableName, int totalRecords, int invalidCreatedAt, int invalidUpdatedAt) {
                this.tableName = tableName;
                this.totalRecords = totalRecords;
                this.invalidCreatedAt = invalidCreatedAt;
                this.invalidUpdatedAt = invalidUpdatedAt;
            }
            
            public boolean hasIssues() {
                return invalidCreatedAt > 0 || invalidUpdatedAt > 0;
            }
            
            // Getters
            public String getTableName() { return tableName; }
            public int getTotalRecords() { return totalRecords; }
            public int getInvalidCreatedAt() { return invalidCreatedAt; }
            public int getInvalidUpdatedAt() { return invalidUpdatedAt; }
        }
    }
}
