package com.example.socialmeet.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 限流器
 */
@Component
public class RateLimiter {
    
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    
    // 限流配置
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final int MAX_REQUESTS_PER_HOUR = 1000;
    private static final int MAX_REQUESTS_PER_DAY = 10000;
    
    /**
     * 检查是否允许请求
     */
    public boolean isAllowed(String key) {
        RateLimitInfo info = rateLimitMap.computeIfAbsent(key, k -> new RateLimitInfo());
        
        long currentTime = System.currentTimeMillis();
        
        // 清理过期数据
        cleanupExpiredData(info, currentTime);
        
        // 检查分钟级限流
        if (info.getMinuteRequests().get() >= MAX_REQUESTS_PER_MINUTE) {
            return false;
        }
        
        // 检查小时级限流
        if (info.getHourRequests().get() >= MAX_REQUESTS_PER_HOUR) {
            return false;
        }
        
        // 检查天级限流
        if (info.getDayRequests().get() >= MAX_REQUESTS_PER_DAY) {
            return false;
        }
        
        // 记录请求
        info.getMinuteRequests().incrementAndGet();
        info.getHourRequests().incrementAndGet();
        info.getDayRequests().incrementAndGet();
        
        return true;
    }
    
    /**
     * 清理过期数据
     */
    private void cleanupExpiredData(RateLimitInfo info, long currentTime) {
        // 清理分钟级数据
        if (currentTime - info.getMinuteStartTime() > 60000) { // 1分钟
            info.getMinuteRequests().set(0);
            info.setMinuteStartTime(currentTime);
        }
        
        // 清理小时级数据
        if (currentTime - info.getHourStartTime() > 3600000) { // 1小时
            info.getHourRequests().set(0);
            info.setHourStartTime(currentTime);
        }
        
        // 清理天级数据
        if (currentTime - info.getDayStartTime() > 86400000) { // 1天
            info.getDayRequests().set(0);
            info.setDayStartTime(currentTime);
        }
    }
    
    /**
     * 获取剩余请求次数
     */
    public int getRemainingRequests(String key) {
        RateLimitInfo info = rateLimitMap.get(key);
        if (info == null) {
            return MAX_REQUESTS_PER_MINUTE;
        }
        
        long currentTime = System.currentTimeMillis();
        cleanupExpiredData(info, currentTime);
        
        return Math.max(0, MAX_REQUESTS_PER_MINUTE - info.getMinuteRequests().get());
    }
    
    /**
     * 重置限流器
     */
    public void reset(String key) {
        rateLimitMap.remove(key);
    }
    
    /**
     * 清理所有过期数据
     */
    public void cleanup() {
        long currentTime = System.currentTimeMillis();
        rateLimitMap.entrySet().removeIf(entry -> {
            RateLimitInfo info = entry.getValue();
            return currentTime - info.getDayStartTime() > 86400000; // 超过1天
        });
    }
    
    /**
     * 限流信息
     */
    private static class RateLimitInfo {
        private final AtomicInteger minuteRequests = new AtomicInteger(0);
        private final AtomicInteger hourRequests = new AtomicInteger(0);
        private final AtomicInteger dayRequests = new AtomicInteger(0);
        private long minuteStartTime = System.currentTimeMillis();
        private long hourStartTime = System.currentTimeMillis();
        private long dayStartTime = System.currentTimeMillis();
        
        public AtomicInteger getMinuteRequests() { return minuteRequests; }
        public AtomicInteger getHourRequests() { return hourRequests; }
        public AtomicInteger getDayRequests() { return dayRequests; }
        public long getMinuteStartTime() { return minuteStartTime; }
        public long getHourStartTime() { return hourStartTime; }
        public long getDayStartTime() { return dayStartTime; }
        
        public void setMinuteStartTime(long minuteStartTime) { this.minuteStartTime = minuteStartTime; }
        public void setHourStartTime(long hourStartTime) { this.hourStartTime = hourStartTime; }
        public void setDayStartTime(long dayStartTime) { this.dayStartTime = dayStartTime; }
    }
}
