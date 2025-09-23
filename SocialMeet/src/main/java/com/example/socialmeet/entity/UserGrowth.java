package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 用户成长值实体类
 */
@Entity
@Table(name = "user_growth")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class UserGrowth {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0; // 总成长值
    
    @Column(name = "current_level", nullable = false)
    private Integer currentLevel = 1; // 当前等级
    
    @Column(name = "current_level_points", nullable = false)
    private Integer currentLevelPoints = 0; // 当前等级已获得成长值
    
    @Column(name = "next_level_points", nullable = false)
    private Integer nextLevelPoints = 100; // 下一等级所需成长值
    
    @Column(name = "daily_points", nullable = false)
    private Integer dailyPoints = 0; // 今日获得成长值
    
    @Column(name = "weekly_points", nullable = false)
    private Integer weeklyPoints = 0; // 本周获得成长值
    
    @Column(name = "monthly_points", nullable = false)
    private Integer monthlyPoints = 0; // 本月获得成长值
    
    @Column(name = "last_daily_reset")
    private LocalDateTime lastDailyReset; // 上次每日重置时间
    
    @Column(name = "last_weekly_reset")
    private LocalDateTime lastWeeklyReset; // 上次每周重置时间
    
    @Column(name = "last_monthly_reset")
    private LocalDateTime lastMonthlyReset; // 上次每月重置时间
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public UserGrowth() {}
    
    public UserGrowth(Long userId) {
        this.userId = userId;
        this.lastDailyReset = LocalDateTime.now();
        this.lastWeeklyReset = LocalDateTime.now();
        this.lastMonthlyReset = LocalDateTime.now();
    }
    
    // 业务方法
    public void addPoints(Integer points) {
        this.totalPoints += points;
        this.currentLevelPoints += points;
        this.dailyPoints += points;
        this.weeklyPoints += points;
        this.monthlyPoints += points;
    }
    
    public boolean canLevelUp() {
        return this.currentLevelPoints >= this.nextLevelPoints;
    }
    
    public void levelUp() {
        if (canLevelUp()) {
            this.currentLevel++;
            this.currentLevelPoints = this.currentLevelPoints - this.nextLevelPoints;
            this.nextLevelPoints = calculateNextLevelPoints(this.currentLevel);
        }
    }
    
    private Integer calculateNextLevelPoints(Integer level) {
        // 等级越高，所需成长值越多
        return 100 + (level - 1) * 50;
    }
    
    public Integer getProgressPercentage() {
        if (this.nextLevelPoints == 0) {
            return 100;
        }
        return (this.currentLevelPoints * 100) / this.nextLevelPoints;
    }
    
    public void resetDailyPoints() {
        this.dailyPoints = 0;
        this.lastDailyReset = LocalDateTime.now();
    }
    
    public void resetWeeklyPoints() {
        this.weeklyPoints = 0;
        this.lastWeeklyReset = LocalDateTime.now();
    }
    
    public void resetMonthlyPoints() {
        this.monthlyPoints = 0;
        this.lastMonthlyReset = LocalDateTime.now();
    }
}
