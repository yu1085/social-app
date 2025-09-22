package com.example.socialmeet.repository;

import com.example.socialmeet.entity.Dynamic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优化后的动态Repository
 */
@Repository
public interface OptimizedDynamicRepository extends JpaRepository<Dynamic, Long> {
    
    /**
     * 获取最新动态 - 使用复合索引
     */
    @Query("SELECT d FROM Dynamic d WHERE d.status = 'PUBLISHED' AND d.isDeleted = false ORDER BY d.publishTime DESC")
    Page<Dynamic> findLatestDynamics(Pageable pageable);
    
    /**
     * 获取热门动态 - 使用复合索引
     */
    @Query("SELECT d FROM Dynamic d WHERE d.status = 'PUBLISHED' AND d.isDeleted = false ORDER BY d.likeCount DESC, d.publishTime DESC")
    Page<Dynamic> findHotDynamics(Pageable pageable);
    
    /**
     * 获取用户动态 - 使用复合索引
     */
    @Query("SELECT d FROM Dynamic d WHERE d.userId = :userId AND d.status = 'PUBLISHED' AND d.isDeleted = false ORDER BY d.publishTime DESC")
    Page<Dynamic> findUserDynamics(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 获取附近动态 - 使用位置索引
     */
    @Query("SELECT d FROM Dynamic d WHERE d.location LIKE %:location% AND d.status = 'PUBLISHED' AND d.isDeleted = false ORDER BY d.publishTime DESC")
    Page<Dynamic> findNearbyDynamics(@Param("location") String location, Pageable pageable);
    
    /**
     * 获取动态统计信息
     */
    @Query("SELECT COUNT(d) FROM Dynamic d WHERE d.userId = :userId AND d.isDeleted = false")
    Long countUserDynamics(@Param("userId") Long userId);
    
    /**
     * 获取用户总点赞数
     */
    @Query("SELECT COALESCE(SUM(d.likeCount), 0) FROM Dynamic d WHERE d.userId = :userId AND d.isDeleted = false")
    Long getUserTotalLikes(@Param("userId") Long userId);
    
    /**
     * 批量更新动态状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE Dynamic d SET d.status = :status, d.updatedAt = :updatedAt WHERE d.id IN :ids")
    int updateDynamicStatus(@Param("ids") List<Long> ids, @Param("status") String status, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 软删除动态
     */
    @Modifying
    @Transactional
    @Query("UPDATE Dynamic d SET d.isDeleted = true, d.status = 'DELETED', d.updatedAt = :updatedAt WHERE d.id = :id")
    int softDeleteDynamic(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 增加浏览次数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Dynamic d SET d.viewCount = d.viewCount + 1 WHERE d.id = :id")
    int incrementViewCount(@Param("id") Long id);
    
    /**
     * 增加点赞数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Dynamic d SET d.likeCount = d.likeCount + 1 WHERE d.id = :id")
    int incrementLikeCount(@Param("id") Long id);
    
    /**
     * 减少点赞数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Dynamic d SET d.likeCount = GREATEST(d.likeCount - 1, 0) WHERE d.id = :id")
    int decrementLikeCount(@Param("id") Long id);
    
    /**
     * 增加评论数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Dynamic d SET d.commentCount = d.commentCount + 1 WHERE d.id = :id")
    int incrementCommentCount(@Param("id") Long id);
    
    /**
     * 减少评论数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Dynamic d SET d.commentCount = GREATEST(d.commentCount - 1, 0) WHERE d.id = :id")
    int decrementCommentCount(@Param("id") Long id);
    
    /**
     * 获取动态详情 - 使用缓存
     */
    @Query("SELECT d FROM Dynamic d WHERE d.id = :id AND d.isDeleted = false")
    Dynamic findByIdAndNotDeleted(@Param("id") Long id);
    
    /**
     * 获取用户最近的动态
     */
    @Query("SELECT d FROM Dynamic d WHERE d.userId = :userId AND d.status = 'PUBLISHED' AND d.isDeleted = false ORDER BY d.publishTime DESC")
    List<Dynamic> findRecentUserDynamics(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 搜索动态内容
     */
    @Query("SELECT d FROM Dynamic d WHERE d.content LIKE %:keyword% AND d.status = 'PUBLISHED' AND d.isDeleted = false ORDER BY d.publishTime DESC")
    Page<Dynamic> searchDynamicsByContent(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 获取指定时间范围内的动态
     */
    @Query("SELECT d FROM Dynamic d WHERE d.publishTime BETWEEN :startTime AND :endTime AND d.status = 'PUBLISHED' AND d.isDeleted = false ORDER BY d.publishTime DESC")
    Page<Dynamic> findDynamicsByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);
    
    /**
     * 获取动态的点赞用户列表
     */
    @Query("SELECT dl.userId FROM DynamicLike dl WHERE dl.dynamicId = :dynamicId ORDER BY dl.createdAt DESC")
    List<Long> findDynamicLikeUsers(@Param("dynamicId") Long dynamicId, Pageable pageable);
    
    /**
     * 检查用户是否点赞了动态
     */
    @Query("SELECT COUNT(dl) > 0 FROM DynamicLike dl WHERE dl.dynamicId = :dynamicId AND dl.userId = :userId")
    boolean isUserLikedDynamic(@Param("dynamicId") Long dynamicId, @Param("userId") Long userId);
    
    /**
     * 获取动态的评论数量
     */
    @Query("SELECT COUNT(dc) FROM DynamicComment dc WHERE dc.dynamicId = :dynamicId AND dc.isDeleted = false")
    Long countDynamicComments(@Param("dynamicId") Long dynamicId);
    
    /**
     * 获取用户动态的统计信息
     */
    @Query("SELECT " +
           "COUNT(d) as dynamicCount, " +
           "COALESCE(SUM(d.likeCount), 0) as totalLikes, " +
           "COALESCE(SUM(d.viewCount), 0) as totalViews, " +
           "COALESCE(SUM(d.commentCount), 0) as totalComments " +
           "FROM Dynamic d WHERE d.userId = :userId AND d.isDeleted = false")
    Object[] getUserDynamicStats(@Param("userId") Long userId);
    
    /**
     * 获取热门标签
     */
    @Query("SELECT d.content FROM Dynamic d WHERE d.status = 'PUBLISHED' AND d.isDeleted = false AND d.content LIKE %:tag% ORDER BY d.likeCount DESC")
    List<String> findPopularTags(@Param("tag") String tag, Pageable pageable);
    
    /**
     * 清理过期数据
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Dynamic d WHERE d.isDeleted = true AND d.updatedAt < :expiredTime")
    int cleanExpiredDeletedDynamics(@Param("expiredTime") LocalDateTime expiredTime);
    
    /**
     * 获取动态的完整信息（包含用户信息）
     */
    @Query("SELECT d, u.nickname, u.avatarUrl, u.gender FROM Dynamic d " +
           "JOIN User u ON d.userId = u.id " +
           "WHERE d.id = :id AND d.isDeleted = false")
    Object[] findDynamicWithUserInfo(@Param("id") Long id);
}
