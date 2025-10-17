package com.socialmeet.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 验证码实体类
 * 对应数据库表: verification_codes
 */
@Entity
@Table(name = "verification_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 10)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CodeType type = CodeType.LOGIN;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 验证码类型枚举
     */
    public enum CodeType {
        LOGIN,              // 登录
        REGISTER,           // 注册
        RESET_PASSWORD,     // 重置密码
        CHANGE_PHONE        // 更换手机号
    }

    /**
     * 检查验证码是否过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    /**
     * 检查验证码是否有效（未使用且未过期）
     */
    public boolean isValid() {
        return !isUsed && !isExpired();
    }
}
