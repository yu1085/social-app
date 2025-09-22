package com.example.socialmeet.repository;

import com.example.socialmeet.entity.DynamicNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 动态通知数据访问层
 */
@Repository
public interface DynamicNotificationRepository extends JpaRepository<DynamicNotification, Long> {
    
    /**
     * 根据用户ID查询通知列表
     */
    Page<DynamicNotification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和已读状态查询通知
     */
    List<DynamicNotification> findByUserIdAndIsRead(Long userId, Boolean isRead);
    
    /**
     * 统计用户未读通知数量
     */
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
    
    /**
     * 根据用户ID和类型查询通知
     */
    List<DynamicNotification> findByUserIdAndType(Long userId, String type);
    
    /**
     * 根据动态ID查询相关通知
     */
    List<DynamicNotification> findByDynamicId(Long dynamicId);
    
    /**
     * 删除过期的通知（30天前）
     */
    @Query("DELETE FROM DynamicNotification n WHERE n.createdAt < :expireTime")
    void deleteExpiredNotifications(@Param("expireTime") java.time.LocalDateTime expireTime);
    
    /**
     * 获取用户最新通知
     */
    @Query("SELECT n FROM DynamicNotification n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    List<DynamicNotification> findLatestNotifications(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 获取用户未读通知
     */
    @Query("SELECT n FROM DynamicNotification n WHERE n.userId = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<DynamicNotification> findUnreadNotifications(@Param("userId") Long userId);
    
    /**
     * 根据类型统计通知数量
     */
    @Query("SELECT n.type, COUNT(n) FROM DynamicNotification n WHERE n.userId = :userId GROUP BY n.type")
    List<Object[]> countNotificationsByType(@Param("userId") Long userId);
}
