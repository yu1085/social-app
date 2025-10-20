package com.socialmeet.backend.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求ID生成器
 */
public class RequestIdGenerator {
    
    private static final AtomicLong COUNTER = new AtomicLong(1);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    /**
     * 生成请求ID
     * 格式: PAY_yyyyMMddHHmmss_序号_UUID前8位
     */
    public static String generateRequestId() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        long sequence = COUNTER.getAndIncrement();
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        
        return String.format("PAY_%s_%04d_%s", timestamp, sequence, uuid);
    }
    
    /**
     * 生成订单ID
     * 格式: ORDER_yyyyMMddHHmmss_序号_UUID前8位
     */
    public static String generateOrderId() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        long sequence = COUNTER.getAndIncrement();
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        
        return String.format("ORDER_%s_%04d_%s", timestamp, sequence, uuid);
    }
    
    /**
     * 生成交易ID
     * 格式: TXN_yyyyMMddHHmmss_序号_UUID前8位
     */
    public static String generateTransactionId() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        long sequence = COUNTER.getAndIncrement();
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        
        return String.format("TXN_%s_%04d_%s", timestamp, sequence, uuid);
    }
}
