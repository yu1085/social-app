package com.socialmeet.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 简单频率限制器
 */
@Component
@Slf4j
public class RateLimiter {
    
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    
    /**
     * 检查是否超过频率限制
     * @param key 限制键（如用户ID）
     * @param maxRequests 最大请求次数
     * @param windowSeconds 时间窗口（秒）
     * @return true表示超过限制，false表示未超过
     */
    public boolean isRateLimited(String key, int maxRequests, int windowSeconds) {
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - (windowSeconds * 1000L);
        
        RateLimitInfo rateLimitInfo = rateLimitMap.computeIfAbsent(key, k -> new RateLimitInfo());
        
        // 清理过期数据
        rateLimitInfo.cleanExpiredRequests(windowStart);
        
        // 检查当前请求数
        int currentRequests = rateLimitInfo.getRequestCount();
        
        if (currentRequests >= maxRequests) {
            log.warn("频率限制触发 - key: {}, current: {}, max: {}", key, currentRequests, maxRequests);
            return true;
        }
        
        // 记录当前请求
        rateLimitInfo.addRequest(currentTime);
        return false;
    }
    
    /**
     * 检查用户支付频率限制
     */
    public boolean isPaymentRateLimited(Long userId) {
        String key = "payment_" + userId;
        return isRateLimited(key, 10, 60); // 1分钟内最多10次支付请求
    }
    
    /**
     * 检查订单创建频率限制
     */
    public boolean isOrderCreateRateLimited(Long userId) {
        String key = "order_create_" + userId;
        return isRateLimited(key, 5, 60); // 1分钟内最多5次订单创建请求
    }
    
    /**
     * 清理过期的限制信息
     */
    public void cleanExpiredData() {
        long currentTime = System.currentTimeMillis();
        long expireTime = currentTime - (3600 * 1000L); // 1小时过期
        
        rateLimitMap.entrySet().removeIf(entry -> {
            RateLimitInfo info = entry.getValue();
            info.cleanExpiredRequests(expireTime);
            return info.getRequestCount() == 0;
        });
    }
    
    /**
     * 频率限制信息
     */
    private static class RateLimitInfo {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private final AtomicLong lastCleanTime = new AtomicLong(System.currentTimeMillis());
        
        public void addRequest(long timestamp) {
            requestCount.incrementAndGet();
        }
        
        public int getRequestCount() {
            return requestCount.get();
        }
        
        public void cleanExpiredRequests(long windowStart) {
            // 简单实现：每分钟清理一次
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCleanTime.get() > 60000) { // 60秒
                if (lastCleanTime.compareAndSet(lastCleanTime.get(), currentTime)) {
                    requestCount.set(0);
                }
            }
        }
    }
}
