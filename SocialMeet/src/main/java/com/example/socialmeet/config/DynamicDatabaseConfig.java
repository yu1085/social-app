package com.example.socialmeet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 动态模块数据库配置
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
@EnableTransactionManagement
public class DynamicDatabaseConfig {
    
    // entityManagerFactory bean已由Spring Boot自动配置，无需重复定义
    
    // 事务管理器已由Spring Boot自动配置，无需重复定义
}
