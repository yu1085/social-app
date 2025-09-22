package com.example.socialmeet.repository;

import com.example.socialmeet.entity.Dynamic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * 动态数据访问层
 */
@Repository
public interface DynamicRepository extends JpaRepository<Dynamic, Long> {
    
    /**
     * 根据用户ID查找动态
     */
    Page<Dynamic> findByUserIdAndIsDeletedFalseOrderByPublishTimeDesc(Long userId, Pageable pageable);
    
    /**
     * 查找所有未删除的动态，按发布时间倒序
     */
    Page<Dynamic> findByIsDeletedFalseOrderByPublishTimeDesc(Pageable pageable);
    
    /**
     * 查找指定时间范围内的动态
     */
    @Query("SELECT d FROM Dynamic d WHERE d.isDeleted = false AND d.publishTime >= :startTime ORDER BY d.publishTime DESC")
    Page<Dynamic> findRecentDynamics(@Param("startTime") LocalDateTime startTime, Pageable pageable);
    
    /**
     * 查找热门动态（按点赞数排序）
     */
    @Query("SELECT d FROM Dynamic d WHERE d.isDeleted = false ORDER BY d.likeCount DESC, d.publishTime DESC")
    Page<Dynamic> findHotDynamics(Pageable pageable);
    
    /**
     * 查找用户关注的动态
     */
    @Query("SELECT d FROM Dynamic d WHERE d.isDeleted = false AND d.userId IN " +
           "(SELECT f.followingId FROM FollowRelationship f WHERE f.followerId = :userId) " +
           "ORDER BY d.publishTime DESC")
    Page<Dynamic> findFollowingDynamics(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 查找用户点赞的动态
     */
    @Query("SELECT d FROM Dynamic d WHERE d.isDeleted = false AND d.id IN " +
           "(SELECT dl.dynamicId FROM DynamicLike dl WHERE dl.userId = :userId) " +
           "ORDER BY d.publishTime DESC")
    Page<Dynamic> findLikedDynamics(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 根据内容搜索动态
     */
    @Query("SELECT d FROM Dynamic d WHERE d.isDeleted = false AND d.content LIKE %:keyword% ORDER BY d.publishTime DESC")
    Page<Dynamic> searchDynamics(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据状态和删除状态查找动态，按发布时间倒序
     */
    Page<Dynamic> findByStatusAndIsDeletedOrderByPublishTimeDesc(String status, Boolean isDeleted, Pageable pageable);
    
    /**
     * 根据状态和删除状态查找动态，按点赞数倒序
     */
    Page<Dynamic> findByStatusAndIsDeletedOrderByLikeCountDescPublishTimeDesc(String status, Boolean isDeleted, Pageable pageable);
    
    /**
     * 根据内容包含关键词、状态和删除状态查找动态
     */
    Page<Dynamic> findByContentContainingAndStatusAndIsDeleted(String keyword, String status, Boolean isDeleted, Pageable pageable);
    
    /**
     * 根据用户ID、状态和删除状态查找动态
     */
    Page<Dynamic> findByUserIdAndStatusAndIsDeleted(Long userId, String status, Boolean isDeleted, Pageable pageable);
    
    /**
     * 统计用户动态数量
     */
    long countByUserIdAndIsDeletedFalse(Long userId);
    
    /**
     * 统计用户动态数量（包含删除状态）
     */
    long countByUserIdAndIsDeleted(Long userId, Boolean isDeleted);
    
    /**
     * 统计用户总点赞数
     */
    @Query("SELECT COALESCE(SUM(d.likeCount), 0) FROM Dynamic d WHERE d.userId = :userId AND d.isDeleted = false")
    long countTotalLikesByUserId(@Param("userId") Long userId);
}
