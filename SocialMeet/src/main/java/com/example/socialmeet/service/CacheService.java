package com.example.socialmeet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务
 */
@Service
public class CacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String CACHE_PREFIX = "socialmeet:";
    private static final String USER_CACHE_PREFIX = CACHE_PREFIX + "user:";
    private static final String DYNAMIC_CACHE_PREFIX = CACHE_PREFIX + "dynamic:";
    private static final String DYNAMIC_LIST_CACHE_PREFIX = CACHE_PREFIX + "dynamic_list:";
    private static final String USER_CARD_CACHE_PREFIX = CACHE_PREFIX + "user_card:";
    private static final String VERIFICATION_CODE_PREFIX = CACHE_PREFIX + "verification:";
    private static final String JWT_TOKEN_PREFIX = CACHE_PREFIX + "jwt:";
    
    /**
     * 用户缓存操作
     */
    public void cacheUser(String userId, Object user) {
        String key = USER_CACHE_PREFIX + userId;
        redisTemplate.opsForValue().set(key, user, Duration.ofHours(1));
    }
    
    @Cacheable(value = "users", key = "#userId")
    public Object getCachedUser(String userId) {
        String key = USER_CACHE_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }
    
    @CacheEvict(value = "users", key = "#userId")
    public void evictUserCache(String userId) {
        String key = USER_CACHE_PREFIX + userId;
        redisTemplate.delete(key);
    }
    
    /**
     * 动态缓存操作
     */
    public void cacheDynamic(String dynamicId, Object dynamic) {
        String key = DYNAMIC_CACHE_PREFIX + dynamicId;
        redisTemplate.opsForValue().set(key, dynamic, Duration.ofMinutes(15));
    }
    
    @Cacheable(value = "dynamics", key = "#dynamicId")
    public Object getCachedDynamic(String dynamicId) {
        String key = DYNAMIC_CACHE_PREFIX + dynamicId;
        return redisTemplate.opsForValue().get(key);
    }
    
    @CacheEvict(value = "dynamics", key = "#dynamicId")
    public void evictDynamicCache(String dynamicId) {
        String key = DYNAMIC_CACHE_PREFIX + dynamicId;
        redisTemplate.delete(key);
    }
    
    /**
     * 动态列表缓存操作
     */
    public void cacheDynamicList(String type, int page, int size, Object dynamics) {
        String key = DYNAMIC_LIST_CACHE_PREFIX + type + ":" + page + ":" + size;
        redisTemplate.opsForValue().set(key, dynamics, Duration.ofMinutes(5));
    }
    
    @Cacheable(value = "dynamicLists", key = "#type + '_' + #page + '_' + #size")
    public Object getCachedDynamicList(String type, int page, int size) {
        String key = DYNAMIC_LIST_CACHE_PREFIX + type + ":" + page + ":" + size;
        return redisTemplate.opsForValue().get(key);
    }
    
    @CacheEvict(value = "dynamicLists", allEntries = true)
    public void evictDynamicListCache() {
        String pattern = DYNAMIC_LIST_CACHE_PREFIX + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
    
    /**
     * 用户卡片缓存操作
     */
    public void cacheUserCards(String gender, String category, int page, int size, Object userCards) {
        String key = USER_CARD_CACHE_PREFIX + gender + ":" + category + ":" + page + ":" + size;
        redisTemplate.opsForValue().set(key, userCards, Duration.ofMinutes(10));
    }
    
    @Cacheable(value = "userCards", key = "#gender + '_' + #category + '_' + #page + '_' + #size")
    public Object getCachedUserCards(String gender, String category, int page, int size) {
        String key = USER_CARD_CACHE_PREFIX + gender + ":" + category + ":" + page + ":" + size;
        return redisTemplate.opsForValue().get(key);
    }
    
    @CacheEvict(value = "userCards", allEntries = true)
    public void evictUserCardCache() {
        String pattern = USER_CARD_CACHE_PREFIX + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
    
    /**
     * 验证码缓存操作
     */
    public void cacheVerificationCode(String phone, String code) {
        String key = VERIFICATION_CODE_PREFIX + phone;
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(5));
    }
    
    public String getCachedVerificationCode(String phone) {
        String key = VERIFICATION_CODE_PREFIX + phone;
        Object code = redisTemplate.opsForValue().get(key);
        return code != null ? code.toString() : null;
    }
    
    public void evictVerificationCode(String phone) {
        String key = VERIFICATION_CODE_PREFIX + phone;
        redisTemplate.delete(key);
    }
    
    /**
     * JWT Token缓存操作
     */
    public void cacheJwtToken(String token, String userId) {
        String key = JWT_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, userId, Duration.ofHours(24));
    }
    
    public String getCachedJwtToken(String token) {
        String key = JWT_TOKEN_PREFIX + token;
        Object userId = redisTemplate.opsForValue().get(key);
        return userId != null ? userId.toString() : null;
    }
    
    public void evictJwtToken(String token) {
        String key = JWT_TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }
    
    /**
     * 批量缓存操作
     */
    public void cacheBatch(String prefix, List<String> keys, List<Object> values, Duration ttl) {
        for (int i = 0; i < keys.size(); i++) {
            String key = prefix + keys.get(i);
            redisTemplate.opsForValue().set(key, values.get(i), ttl);
        }
    }
    
    /**
     * 批量获取缓存
     */
    public List<Object> getBatchCache(String prefix, List<String> keys) {
        return keys.stream()
                .map(key -> redisTemplate.opsForValue().get(prefix + key))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 批量删除缓存
     */
    public void evictBatchCache(String prefix, List<String> keys) {
        List<String> fullKeys = keys.stream()
                .map(key -> prefix + key)
                .collect(java.util.stream.Collectors.toList());
        redisTemplate.delete(fullKeys);
    }
    
    /**
     * 缓存预热
     */
    public void warmUpCache() {
        // 预热热门数据
        // 这里可以实现缓存预热逻辑
    }
    
    /**
     * 清理过期缓存
     */
    public void cleanExpiredCache() {
        // Redis会自动清理过期键，这里可以添加额外的清理逻辑
    }
    
    /**
     * 获取缓存统计信息
     */
    public CacheStats getCacheStats() {
        String pattern = CACHE_PREFIX + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        
        int totalKeys = keys != null ? keys.size() : 0;
        int userCacheKeys = 0;
        int dynamicCacheKeys = 0;
        int dynamicListCacheKeys = 0;
        int userCardCacheKeys = 0;
        
        if (keys != null) {
            for (String key : keys) {
                if (key.startsWith(USER_CACHE_PREFIX)) userCacheKeys++;
                else if (key.startsWith(DYNAMIC_CACHE_PREFIX)) dynamicCacheKeys++;
                else if (key.startsWith(DYNAMIC_LIST_CACHE_PREFIX)) dynamicListCacheKeys++;
                else if (key.startsWith(USER_CARD_CACHE_PREFIX)) userCardCacheKeys++;
            }
        }
        
        return new CacheStats(
            totalKeys,
            userCacheKeys,
            dynamicCacheKeys,
            dynamicListCacheKeys,
            userCardCacheKeys
        );
    }
    
    /**
     * 缓存统计数据类
     */
    public static class CacheStats {
        private final int totalKeys;
        private final int userCacheKeys;
        private final int dynamicCacheKeys;
        private final int dynamicListCacheKeys;
        private final int userCardCacheKeys;
        
        public CacheStats(int totalKeys, int userCacheKeys, int dynamicCacheKeys, 
                         int dynamicListCacheKeys, int userCardCacheKeys) {
            this.totalKeys = totalKeys;
            this.userCacheKeys = userCacheKeys;
            this.dynamicCacheKeys = dynamicCacheKeys;
            this.dynamicListCacheKeys = dynamicListCacheKeys;
            this.userCardCacheKeys = userCardCacheKeys;
        }
        
        // Getters
        public int getTotalKeys() { return totalKeys; }
        public int getUserCacheKeys() { return userCacheKeys; }
        public int getDynamicCacheKeys() { return dynamicCacheKeys; }
        public int getDynamicListCacheKeys() { return dynamicListCacheKeys; }
        public int getUserCardCacheKeys() { return userCardCacheKeys; }
    }
}
