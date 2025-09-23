package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 礼物特效实体类
 */
@Entity
@Table(name = "gift_effects")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class GiftEffect {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "gift_id", nullable = false)
    private Long giftId;
    
    @Column(name = "effect_type", length = 50, nullable = false)
    private String effectType; // ANIMATION, SOUND, FULLSCREEN, PARTICLE
    
    @Column(name = "effect_name", length = 100, nullable = false)
    private String effectName;
    
    @Column(name = "effect_url", length = 500)
    private String effectUrl; // 特效资源URL
    
    @Column(name = "duration", nullable = false)
    private Integer duration; // 特效持续时间(毫秒)
    
    @Column(name = "priority", nullable = false)
    private Integer priority = 0; // 优先级，数字越大优先级越高
    
    @Column(name = "is_loop")
    private Boolean isLoop = false; // 是否循环播放
    
    @Column(name = "loop_count")
    private Integer loopCount = 1; // 循环次数
    
    @Column(name = "trigger_condition", length = 200)
    private String triggerCondition; // 触发条件 (如：数量>=10)
    
    @Column(name = "effect_config", columnDefinition = "TEXT")
    private String effectConfig; // 特效配置JSON
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 特效类型枚举
    public enum EffectType {
        ANIMATION("动画特效", "礼物动画效果"),
        SOUND("音效", "礼物音效"),
        FULLSCREEN("全屏特效", "全屏显示的特效"),
        PARTICLE("粒子特效", "粒子系统特效"),
        LIGHTNING("闪电特效", "闪电类特效"),
        FIREWORKS("烟花特效", "烟花类特效"),
        HEART("爱心特效", "爱心类特效"),
        RAINBOW("彩虹特效", "彩虹类特效");
        
        private final String displayName;
        private final String description;
        
        EffectType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 构造函数
    public GiftEffect() {}
    
    public GiftEffect(Long giftId, String effectType, String effectName, 
                     String effectUrl, Integer duration) {
        this.giftId = giftId;
        this.effectType = effectType;
        this.effectName = effectName;
        this.effectUrl = effectUrl;
        this.duration = duration;
    }
}
