package com.socialmeet.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 防重放攻击工具
 */
@Component
@Slf4j
public class ReplayAttackPrevention {
    
    private final ConcurrentHashMap<String, Long> requestCache = new ConcurrentHashMap<>();
    private final long CACHE_EXPIRE_TIME = TimeUnit.MINUTES.toMillis(5); // 5分钟过期
    
    /**
     * 验证请求是否重复
     * @param userId 用户ID
     * @param requestData 请求数据
     * @param timestamp 时间戳
     * @param signature 签名
     * @return true表示请求有效，false表示重复请求
     */
    public boolean validateRequest(Long userId, String requestData, Long timestamp, String signature) {
        try {
            // 如果没有提供timestamp和signature，跳过防重放攻击验证（开发模式）
            if (timestamp == null || signature == null) {
                log.debug("跳过防重放攻击验证 - userId: {} (开发模式)", userId);
                return true;
            }

            // 1. 检查时间戳（防止重放攻击）
            long currentTime = System.currentTimeMillis();
            long timeDiff = Math.abs(currentTime - timestamp);
            if (timeDiff > 300000) { // 5分钟
                log.warn("请求时间戳过期 - userId: {}, timeDiff: {}", userId, timeDiff);
                return false;
            }

            // 2. 生成请求指纹
            String requestFingerprint = generateRequestFingerprint(userId, requestData, timestamp);

            // 3. 检查是否重复请求
            if (requestCache.containsKey(requestFingerprint)) {
                log.warn("检测到重复请求 - userId: {}, fingerprint: {}", userId, requestFingerprint);
                return false;
            }

            // 4. 验证签名（简单实现，实际项目中应使用更复杂的签名算法）
            if (!verifySignature(requestData, timestamp, signature)) {
                log.warn("请求签名验证失败 - userId: {}", userId);
                return false;
            }

            // 5. 记录请求指纹
            requestCache.put(requestFingerprint, currentTime);

            // 6. 清理过期缓存
            cleanExpiredCache();

            return true;

        } catch (Exception e) {
            log.error("防重放攻击验证失败 - userId: {}", userId, e);
            return false;
        }
    }
    
    /**
     * 生成请求指纹
     */
    private String generateRequestFingerprint(Long userId, String requestData, Long timestamp) {
        String data = userId + ":" + requestData + ":" + timestamp;
        return md5(data);
    }
    
    /**
     * 验证签名
     */
    private boolean verifySignature(String requestData, Long timestamp, String signature) {
        // 简单实现，实际项目中应使用更复杂的签名算法
        String expectedSignature = md5(requestData + ":" + timestamp + ":secret_key");
        return expectedSignature.equals(signature);
    }
    
    /**
     * MD5加密
     */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("MD5加密失败", e);
            return "";
        }
    }
    
    /**
     * 清理过期缓存
     */
    private void cleanExpiredCache() {
        long currentTime = System.currentTimeMillis();
        requestCache.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > CACHE_EXPIRE_TIME);
    }
    
    /**
     * 清理所有缓存
     */
    public void clearCache() {
        requestCache.clear();
        log.info("防重放攻击缓存已清理");
    }
}
