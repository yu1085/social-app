package com.example.socialmeet.service;

import com.example.socialmeet.entity.GiftEffect;
import com.example.socialmeet.repository.GiftEffectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 礼物特效服务
 */
@Service
@Slf4j
@Transactional
public class GiftEffectService {
    
    @Autowired
    private GiftEffectRepository giftEffectRepository;
    
    /**
     * 获取礼物的所有特效
     */
    public List<GiftEffect> getGiftEffects(Long giftId) {
        return giftEffectRepository.findByGiftIdAndIsActiveTrueOrderByPriorityDesc(giftId);
    }
    
    /**
     * 获取满足条件的特效
     */
    public List<GiftEffect> getActiveEffectsByGiftIdAndQuantity(Long giftId, Integer quantity) {
        return giftEffectRepository.findActiveEffectsByGiftIdAndQuantity(giftId, quantity);
    }
    
    /**
     * 获取特效配置
     */
    public Map<String, Object> getEffectConfig(Long giftId, Integer quantity) {
        List<GiftEffect> effects = getActiveEffectsByGiftIdAndQuantity(giftId, quantity);
        
        return Map.of(
            "giftId", giftId,
            "quantity", quantity,
            "effects", effects.stream().map(effect -> Map.of(
                "id", effect.getId(),
                "type", effect.getEffectType(),
                "name", effect.getEffectName(),
                "url", effect.getEffectUrl(),
                "duration", effect.getDuration(),
                "priority", effect.getPriority(),
                "isLoop", effect.getIsLoop(),
                "loopCount", effect.getLoopCount(),
                "config", effect.getEffectConfig()
            )).collect(Collectors.toList())
        );
    }
    
    /**
     * 创建礼物特效
     */
    public GiftEffect createGiftEffect(Long giftId, String effectType, String effectName, 
                                     String effectUrl, Integer duration, Integer priority) {
        GiftEffect effect = new GiftEffect(giftId, effectType, effectName, effectUrl, duration);
        effect.setPriority(priority);
        return giftEffectRepository.save(effect);
    }
}
