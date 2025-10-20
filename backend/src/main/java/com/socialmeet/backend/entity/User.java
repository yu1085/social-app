package com.socialmeet.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表: users
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(length = 255)
    private String password;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String nickname;

    @Column(length = 100)
    private String email;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    private LocalDate birthday;

    @Column(length = 20)
    private String constellation;

    @Column(length = 100)
    private String location;

    private Integer height;

    private Integer weight;

    @Column(name = "income_level", length = 50)
    private String incomeLevel;

    @Column(length = 50)
    private String education;

    @Column(name = "marital_status", length = 20)
    private String maritalStatus;

    @Column(length = 200)
    private String signature;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "is_vip", nullable = false)
    private Boolean isVip = false;

    @Column(name = "vip_level", nullable = false)
    private Integer vipLevel = 0;

    @Column(name = "vip_expire_at")
    private LocalDateTime vipExpireAt;

    @Column(name = "wealth_level", nullable = false)
    private Integer wealthLevel = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "is_online", nullable = false)
    private Boolean isOnline = false;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @Column(name = "jpush_registration_id", length = 50)
    private String jpushRegistrationId;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "last_location_update")
    private LocalDateTime lastLocationUpdate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UserStatus status = UserStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 性别枚举
     */
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE,     // 活跃
        BANNED,     // 封禁
        DELETED     // 已删除
    }

    /**
     * 计算年龄
     */
    public Integer getAge() {
        if (birthday == null) {
            return null;
        }
        LocalDate now = LocalDate.now();
        int age = now.getYear() - birthday.getYear();
        if (now.getMonthValue() < birthday.getMonthValue() ||
            (now.getMonthValue() == birthday.getMonthValue() && now.getDayOfMonth() < birthday.getDayOfMonth())) {
            age--;
        }
        return age;
    }
}
