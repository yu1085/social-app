package com.example.socialmeet.repository;

import com.example.socialmeet.entity.GrowthRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 成长值记录Repository
 */
@Repository
public interface GrowthRecordRepository extends JpaRepository<GrowthRecord, Long> {
    
    /**
     * 根据用户ID查找成长记录
     */
    Page<GrowthRecord> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和动作类型查找
     */
    Page<GrowthRecord> findByUserIdAndActionTypeOrderByCreatedAtDesc(Long userId, String actionType, Pageable pageable);
    
    /**
     * 根据时间范围查找
     */
    @Query("SELECT gr FROM GrowthRecord gr WHERE gr.userId = :userId AND gr.createdAt BETWEEN :startTime AND :endTime ORDER BY gr.createdAt DESC")
    Page<GrowthRecord> findByUserIdAndTimeRange(@Param("userId") Long userId, 
                                               @Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime, 
                                               Pageable pageable);
    
    /**
     * 统计用户总成长值
     */
    @Query("SELECT SUM(gr.points) FROM GrowthRecord gr WHERE gr.userId = :userId")
    Integer getTotalPointsByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户今日成长值
     */
    @Query("SELECT SUM(gr.points) FROM GrowthRecord gr WHERE gr.userId = :userId AND gr.createdAt >= :today")
    Integer getTodayPointsByUserId(@Param("userId") Long userId, @Param("today") LocalDateTime today);
    
    /**
     * 统计用户本周成长值
     */
    @Query("SELECT SUM(gr.points) FROM GrowthRecord gr WHERE gr.userId = :userId AND gr.createdAt >= :weekStart")
    Integer getWeeklyPointsByUserId(@Param("userId") Long userId, @Param("weekStart") LocalDateTime weekStart);
    
    /**
     * 统计用户本月成长值
     */
    @Query("SELECT SUM(gr.points) FROM GrowthRecord gr WHERE gr.userId = :userId AND gr.createdAt >= :monthStart")
    Integer getMonthlyPointsByUserId(@Param("userId") Long userId, @Param("monthStart") LocalDateTime monthStart);
    
    /**
     * 根据关联ID和类型查找
     */
    List<GrowthRecord> findByRelatedIdAndRelatedType(Long relatedId, String relatedType);
    
    /**
     * 统计各动作类型的成长值
     */
    @Query("SELECT gr.actionType, SUM(gr.points) FROM GrowthRecord gr WHERE gr.userId = :userId GROUP BY gr.actionType")
    List<Object[]> getPointsByActionType(@Param("userId") Long userId);
}
