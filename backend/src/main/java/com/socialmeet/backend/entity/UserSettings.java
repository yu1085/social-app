package com.socialmeet.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户设置实体类
 */
@Entity
@Table(name = "user_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "voice_call_enabled", nullable = false)
    private Boolean voiceCallEnabled = true;

    @Column(name = "video_call_enabled", nullable = false)
    private Boolean videoCallEnabled = true;

    @Column(name = "message_charge_enabled", nullable = false)
    private Boolean messageChargeEnabled = false;

    @Column(name = "voice_call_price", precision = 10, scale = 2)
    private BigDecimal voiceCallPrice = BigDecimal.valueOf(0.0);

    @Column(name = "video_call_price", precision = 10, scale = 2)
    private BigDecimal videoCallPrice = BigDecimal.valueOf(0.0);

    @Column(name = "message_price", precision = 10, scale = 2)
    private BigDecimal messagePrice = BigDecimal.valueOf(0.0);

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 关联用户
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
