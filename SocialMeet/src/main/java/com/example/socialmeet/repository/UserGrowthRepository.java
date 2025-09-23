package com.example.socialmeet.repository;

import com.example.socialmeet.entity.UserGrowth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户成长值Repository
 */
@Repository
public interface UserGrowthRepository extends JpaRepository<UserGrowth, Long> {
    
    /**
     * 根据用户ID查找成长信息
     */
    Optional<UserGrowth> findByUserId(Long userId);
    
    /**
     * 根据等级查找用户
     */
    List<UserGrowth> findByCurrentLevelOrderByTotalPointsDesc(Integer level);
    
    /**
     * 查找需要重置每日成长值的用户
     */
    @Query("SELECT ug FROM UserGrowth ug WHERE ug.lastDailyReset < :today")
    List<UserGrowth> findUsersNeedingDailyReset(@Param("today") LocalDateTime today);
    
    /**
     * 查找需要重置每周成长值的用户
     */
    @Query("SELECT ug FROM UserGrowth ug WHERE ug.lastWeeklyReset < :weekStart")
    List<UserGrowth> findUsersNeedingWeeklyReset(@Param("weekStart") LocalDateTime weekStart);
    
    /**
     * 查找需要重置每月成长值的用户
     */
    @Query("SELECT ug FROM UserGrowth ug WHERE ug.lastMonthlyReset < :monthStart")
    List<UserGrowth> findUsersNeedingMonthlyReset(@Param("monthStart") LocalDateTime monthStart);
    
    /**
     * 统计各等级用户数量
     */
    @Query("SELECT ug.currentLevel, COUNT(ug) FROM UserGrowth ug GROUP BY ug.currentLevel ORDER BY ug.currentLevel")
    List<Object[]> countUsersByLevel();
}
