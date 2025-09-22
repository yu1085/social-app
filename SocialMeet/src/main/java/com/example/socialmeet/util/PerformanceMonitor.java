package com.example.socialmeet.util;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控工具
 */
@Component
public class PerformanceMonitor extends OncePerRequestFilter {
    
    private static final String START_TIME_ATTRIBUTE = "startTime";
    private static final String REQUEST_COUNT_ATTRIBUTE = "requestCount";
    
    private final ConcurrentHashMap<String, AtomicLong> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> totalResponseTimes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> maxResponseTimes = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            String requestPath = getRequestPath(request);
            recordMetrics(requestPath, responseTime);
            
            // 记录慢请求
            if (responseTime > 1000) { // 超过1秒
                logSlowRequest(request, responseTime);
            }
        }
    }
    
    /**
     * 记录性能指标
     */
    private void recordMetrics(String requestPath, long responseTime) {
        // 请求计数
        requestCounts.computeIfAbsent(requestPath, k -> new AtomicLong(0)).incrementAndGet();
        
        // 总响应时间
        totalResponseTimes.computeIfAbsent(requestPath, k -> new AtomicLong(0)).addAndGet(responseTime);
        
        // 最大响应时间
        maxResponseTimes.computeIfAbsent(requestPath, k -> new AtomicLong(0))
                .updateAndGet(current -> Math.max(current, responseTime));
    }
    
    /**
     * 记录慢请求
     */
    private void logSlowRequest(HttpServletRequest request, long responseTime) {
        System.out.println(String.format("慢请求警告: %s %s - 响应时间: %dms", 
                request.getMethod(), request.getRequestURI(), responseTime));
    }
    
    /**
     * 获取请求路径
     */
    private String getRequestPath(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        return method + " " + uri;
    }
    
    /**
     * 获取性能统计
     */
    public PerformanceStats getPerformanceStats() {
        ConcurrentHashMap<String, RequestStats> stats = new ConcurrentHashMap<>();
        
        for (String path : requestCounts.keySet()) {
            long count = requestCounts.get(path).get();
            long totalTime = totalResponseTimes.get(path).get();
            long maxTime = maxResponseTimes.get(path).get();
            
            stats.put(path, new RequestStats(count, totalTime, maxTime));
        }
        
        return new PerformanceStats(stats);
    }
    
    /**
     * 重置统计
     */
    public void resetStats() {
        requestCounts.clear();
        totalResponseTimes.clear();
        maxResponseTimes.clear();
    }
    
    /**
     * 记录API调用
     */
    public void recordApiCall(String apiName, long responseTime) {
        recordMetrics(apiName, responseTime);
    }
    
    /**
     * 记录API错误
     */
    public void recordApiError(String apiName, String errorMessage) {
        // 记录错误统计
        String errorKey = apiName + "_error";
        requestCounts.computeIfAbsent(errorKey, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * 记录API错误（简化版本）
     */
    public void recordError(String apiName, String errorMessage) {
        recordApiError(apiName, errorMessage);
    }
    
    
    /**
     * 请求统计数据类
     */
    public static class RequestStats {
        private final long requestCount;
        private final long totalResponseTime;
        private final long maxResponseTime;
        
        public RequestStats(long requestCount, long totalResponseTime, long maxResponseTime) {
            this.requestCount = requestCount;
            this.totalResponseTime = totalResponseTime;
            this.maxResponseTime = maxResponseTime;
        }
        
        public long getRequestCount() { return requestCount; }
        public long getTotalResponseTime() { return totalResponseTime; }
        public long getMaxResponseTime() { return maxResponseTime; }
        public double getAverageResponseTime() { 
            return requestCount > 0 ? (double) totalResponseTime / requestCount : 0; 
        }
    }
    
    /**
     * 性能统计数据类
     */
    public static class PerformanceStats {
        private final ConcurrentHashMap<String, RequestStats> requestStats;
        
        public PerformanceStats(ConcurrentHashMap<String, RequestStats> requestStats) {
            this.requestStats = requestStats;
        }
        
        public ConcurrentHashMap<String, RequestStats> getRequestStats() { return requestStats; }
        
        public long getTotalRequests() {
            return requestStats.values().stream()
                    .mapToLong(RequestStats::getRequestCount)
                    .sum();
        }
        
        public double getOverallAverageResponseTime() {
            long totalRequests = getTotalRequests();
            if (totalRequests == 0) return 0;
            
            long totalTime = requestStats.values().stream()
                    .mapToLong(RequestStats::getTotalResponseTime)
                    .sum();
            
            return (double) totalTime / totalRequests;
        }
    }
}
