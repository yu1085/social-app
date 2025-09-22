package com.example.socialmeet.service;

import com.example.socialmeet.entity.CallSettings;
import com.example.socialmeet.repository.CallSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 通话设置服务
 */
@Service
public class CallSettingsService {
    
    @Autowired
    private CallSettingsRepository callSettingsRepository;
    
    /**
     * 获取用户的通话设置
     */
    public CallSettings getUserCallSettings(Long userId) {
        Optional<CallSettings> settings = callSettingsRepository.findByUserId(userId);
        if (settings.isPresent()) {
            return settings.get();
        } else {
            // 如果用户没有通话设置，返回null（不自动创建）
            // 只有新用户注册时才会创建默认设置
            System.out.println("=== 用户 " + userId + " 没有通话设置记录 ===");
            return null;
        }
    }
    
    /**
     * 更新用户的通话设置
     */
    public CallSettings updateUserCallSettings(Long userId, Map<String, Object> updateData) {
        CallSettings settings = getUserCallSettings(userId);
        
        // 如果用户没有通话设置，先创建一个
        if (settings == null) {
            settings = new CallSettings(userId);
            System.out.println("=== 为用户 " + userId + " 创建新的通话设置 ===");
        }
        
        // 更新视频接听设置
        if (updateData.containsKey("videoCallEnabled")) {
            settings.setVideoCallEnabled((Boolean) updateData.get("videoCallEnabled"));
        }
        if (updateData.containsKey("videoCallPrice")) {
            settings.setVideoCallPrice(((Number) updateData.get("videoCallPrice")).doubleValue());
        }
        
        // 更新语音接听设置
        if (updateData.containsKey("voiceCallEnabled")) {
            settings.setVoiceCallEnabled((Boolean) updateData.get("voiceCallEnabled"));
        }
        if (updateData.containsKey("voiceCallPrice")) {
            settings.setVoiceCallPrice(((Number) updateData.get("voiceCallPrice")).doubleValue());
        }
        
        // 更新私信收费设置
        if (updateData.containsKey("messageChargeEnabled")) {
            settings.setMessageChargeEnabled((Boolean) updateData.get("messageChargeEnabled"));
        }
        if (updateData.containsKey("messagePrice")) {
            settings.setMessagePrice(((Number) updateData.get("messagePrice")).doubleValue());
        }
        
        // 更新免费接听时长
        if (updateData.containsKey("freeCallDuration")) {
            settings.setFreeCallDuration(((Number) updateData.get("freeCallDuration")).intValue());
        }
        
        // 更新自动接听设置
        if (updateData.containsKey("autoAnswerEnabled")) {
            settings.setAutoAnswerEnabled((Boolean) updateData.get("autoAnswerEnabled"));
        }
        
        settings.setUpdatedAt(LocalDateTime.now());
        return callSettingsRepository.save(settings);
    }
    
    /**
     * 获取用户的通话价格信息（用于用户卡片显示）
     */
    public Map<String, Object> getUserCallPrices(Long userId) {
        CallSettings settings = getUserCallSettings(userId);
        
        Map<String, Object> prices = new HashMap<>();
        
        if (settings != null) {
            // 用户有通话设置，使用用户设置的价格
            prices.put("videoCallPrice", settings.getVideoCallPrice());
            prices.put("voiceCallPrice", settings.getVoiceCallPrice());
            prices.put("messagePrice", settings.getMessagePrice());
            prices.put("videoCallEnabled", settings.getVideoCallEnabled());
            prices.put("voiceCallEnabled", settings.getVoiceCallEnabled());
            prices.put("messageChargeEnabled", settings.getMessageChargeEnabled());
        } else {
            // 用户没有通话设置，使用系统默认价格（老用户兼容）
            prices.put("videoCallPrice", 500.0); // 老用户默认500/分钟
            prices.put("voiceCallPrice", 500.0);
            prices.put("messagePrice", 0.0);
            prices.put("videoCallEnabled", true);
            prices.put("voiceCallEnabled", true);
            prices.put("messageChargeEnabled", false);
        }
        
        return prices;
    }
}
