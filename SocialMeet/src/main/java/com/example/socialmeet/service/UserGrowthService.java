package com.example.socialmeet.service;

import com.example.socialmeet.entity.GrowthRecord;
import com.example.socialmeet.entity.UserGrowth;
import com.example.socialmeet.repository.GrowthRecordRepository;
import com.example.socialmeet.repository.UserGrowthRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户成长值服务
 */
@Service
@Slf4j
@Transactional
public class UserGrowthService {
    
    @Autowired
    private UserGrowthRepository userGrowthRepository;
    
    @Autowired
    private GrowthRecordRepository growthRecordRepository;
    
    /**
     * 获取用户成长信息
     */
    public UserGrowth getUserGrowth(Long userId) {
        Optional<UserGrowth> growthOpt = userGrowthRepository.findByUserId(userId);
        if (growthOpt.isPresent()) {
            return growthOpt.get();
        } else {
            UserGrowth growth = new UserGrowth(userId);
            return userGrowthRepository.save(growth);
        }
    }
    
    /**
     * 添加成长值
     */
    public boolean addGrowthPoints(Long userId, String actionType, Integer points, String description, Long relatedId, String relatedType) {
        try {
            UserGrowth growth = getUserGrowth(userId);
            
            // 添加成长值
            growth.addPoints(points);
            
            // 检查是否升级
            while (growth.canLevelUp()) {
                growth.levelUp();
                log.info("用户 {} 升级到 {} 级", userId, growth.getCurrentLevel());
            }
            
            userGrowthRepository.save(growth);
            
            // 记录成长值变化
            GrowthRecord record = new GrowthRecord(userId, actionType, points, description, relatedId, relatedType);
            growthRecordRepository.save(record);
            
            log.info("用户 {} 获得 {} 成长值，当前等级: {}", userId, points, growth.getCurrentLevel());
            return true;
        } catch (Exception e) {
            log.error("添加成长值失败: userId={}, actionType={}, points={}", userId, actionType, points, e);
            return false;
        }
    }
    
    /**
     * 获取用户成长记录
     */
    public Page<GrowthRecord> getUserGrowthRecords(Long userId, Pageable pageable) {
        return growthRecordRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    /**
     * 获取用户成长统计
     */
    public Map<String, Object> getUserGrowthStats(Long userId) {
        UserGrowth growth = getUserGrowth(userId);
        
        // 今日成长值
        Integer todayPoints = growthRecordRepository.getTodayPointsByUserId(userId, LocalDateTime.now().toLocalDate().atStartOfDay());
        
        // 本周成长值
        LocalDateTime weekStart = LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1);
        Integer weeklyPoints = growthRecordRepository.getWeeklyPointsByUserId(userId, weekStart);
        
        // 本月成长值
        LocalDateTime monthStart = LocalDateTime.now().toLocalDate().withDayOfMonth(1).atStartOfDay();
        Integer monthlyPoints = growthRecordRepository.getMonthlyPointsByUserId(userId, monthStart);
        
        // 各动作类型成长值
        List<Object[]> pointsByAction = growthRecordRepository.getPointsByActionType(userId);
        
        return Map.of(
            "totalPoints", growth.getTotalPoints(),
            "currentLevel", growth.getCurrentLevel(),
            "currentLevelPoints", growth.getCurrentLevelPoints(),
            "nextLevelPoints", growth.getNextLevelPoints(),
            "progressPercentage", growth.getProgressPercentage(),
            "todayPoints", todayPoints != null ? todayPoints : 0,
            "weeklyPoints", weeklyPoints != null ? weeklyPoints : 0,
            "monthlyPoints", monthlyPoints != null ? monthlyPoints : 0,
            "pointsByAction", pointsByAction
        );
    }
    
    /**
     * 每日重置成长值
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailyPoints() {
        log.info("开始每日成长值重置");
        List<UserGrowth> users = userGrowthRepository.findUsersNeedingDailyReset(LocalDateTime.now().toLocalDate().atStartOfDay());
        
        for (UserGrowth growth : users) {
            growth.resetDailyPoints();
            userGrowthRepository.save(growth);
        }
        
        log.info("每日成长值重置完成，处理用户数: {}", users.size());
    }
    
    /**
     * 每周重置成长值
     */
    @Scheduled(cron = "0 0 0 * * MON")
    public void resetWeeklyPoints() {
        log.info("开始每周成长值重置");
        LocalDateTime weekStart = LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1);
        List<UserGrowth> users = userGrowthRepository.findUsersNeedingWeeklyReset(weekStart);
        
        for (UserGrowth growth : users) {
            growth.resetWeeklyPoints();
            userGrowthRepository.save(growth);
        }
        
        log.info("每周成长值重置完成，处理用户数: {}", users.size());
    }
    
    /**
     * 每月重置成长值
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetMonthlyPoints() {
        log.info("开始每月成长值重置");
        LocalDateTime monthStart = LocalDateTime.now().toLocalDate().withDayOfMonth(1).atStartOfDay();
        List<UserGrowth> users = userGrowthRepository.findUsersNeedingMonthlyReset(monthStart);
        
        for (UserGrowth growth : users) {
            growth.resetMonthlyPoints();
            userGrowthRepository.save(growth);
        }
        
        log.info("每月成长值重置完成，处理用户数: {}", users.size());
    }
    
    /**
     * 获取等级分布统计
     */
    public List<Object[]> getLevelDistribution() {
        return userGrowthRepository.countUsersByLevel();
    }
}
