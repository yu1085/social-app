package com.example.socialmeet.repository;

import com.example.socialmeet.entity.PushNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 推送通知数据访问层
 */
@Repository
public interface PushNotificationRepository extends JpaRepository<PushNotification, Long> {
    
    /**
     * 根据用户ID查找推送通知
     */
    List<PushNotification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根据用户ID分页查找推送通知
     */
    Page<PushNotification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和分类查找推送通知
     */
    List<PushNotification> findByUserIdAndCategoryOrderByCreatedAtDesc(Long userId, String category);
    
    /**
     * 根据用户ID和状态查找推送通知
     */
    List<PushNotification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
    
    /**
     * 根据分类查找推送通知
     */
    List<PushNotification> findByCategoryOrderByCreatedAtDesc(String category);
    
    /**
     * 根据状态查找推送通知
     */
    List<PushNotification> findByStatusOrderByCreatedAtDesc(String status);
    
    /**
     * 查找指定时间范围内的推送通知
     */
    @Query("SELECT pn FROM PushNotification pn WHERE pn.userId = :userId AND pn.createdAt BETWEEN :startTime AND :endTime ORDER BY pn.createdAt DESC")
    List<PushNotification> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId, 
                                                          @Param("startTime") LocalDateTime startTime, 
                                                          @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计用户未读推送数量
     */
    @Query("SELECT COUNT(pn) FROM PushNotification pn WHERE pn.userId = :userId AND pn.status = 'SENT'")
    long countUnreadByUserId(@Param("userId") Long userId);
    
    /**
     * 统计指定分类的推送数量
     */
    long countByUserIdAndCategory(Long userId, String category);
    
    /**
     * 统计指定状态的推送数量
     */
    long countByUserIdAndStatus(Long userId, String status);
    
    /**
     * 删除指定时间之前的推送通知
     */
    void deleteByCreatedAtBefore(LocalDateTime dateTime);
    
    /**
     * 根据用户ID删除所有推送通知
     */
    void deleteByUserId(Long userId);
}
