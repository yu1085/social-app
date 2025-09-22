package com.example.socialmeet.repository;

import com.example.socialmeet.dto.DynamicFilterRequest;
import com.example.socialmeet.entity.Dynamic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 增强版动态数据访问层
 * 提供复杂的查询功能
 */
@Repository
public interface EnhancedDynamicRepository extends JpaRepository<Dynamic, Long> {
    
    /**
     * 根据用户ID和删除状态查询动态
     */
    Page<Dynamic> findByUserIdAndIsDeletedOrderByPublishTimeDesc(Long userId, Boolean isDeleted, Pageable pageable);
    
    /**
     * 根据状态和删除状态查询动态
     */
    Page<Dynamic> findByStatusAndIsDeletedOrderByPublishTimeDesc(String status, Boolean isDeleted, Pageable pageable);
    
    /**
     * 根据状态和删除状态按点赞数排序查询动态
     */
    Page<Dynamic> findByStatusAndIsDeletedOrderByLikeCountDescPublishTimeDesc(String status, Boolean isDeleted, Pageable pageable);
    
    /**
     * 根据位置查询动态
     */
    @Query("SELECT d FROM Dynamic d WHERE d.location LIKE %:location% AND d.status = 'PUBLISHED' AND d.isDeleted = false ORDER BY d.publishTime DESC")
    Page<Dynamic> findByLocationContaining(@Param("location") String location, Pageable pageable);
    
    /**
     * 根据内容关键词搜索动态
     */
    @Query("SELECT d FROM Dynamic d WHERE d.content LIKE %:keyword% AND d.status = 'PUBLISHED' AND d.isDeleted = false ORDER BY d.publishTime DESC")
    Page<Dynamic> searchDynamics(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 统计用户动态数
     */
    long countByUserIdAndIsDeleted(Long userId, Boolean isDeleted);
    
    /**
     * 检查用户是否点赞了动态
     */
    @Query("SELECT COUNT(dl) > 0 FROM DynamicLike dl WHERE dl.dynamicId = :dynamicId AND dl.userId = :userId")
    boolean isUserLikedDynamic(@Param("dynamicId") Long dynamicId, @Param("userId") Long userId);
    
    /**
     * 添加点赞
     */
    @Query(value = "INSERT INTO dynamic_likes (dynamic_id, user_id, created_at) VALUES (:dynamicId, :userId, NOW())", nativeQuery = true)
    void addLike(@Param("dynamicId") Long dynamicId, @Param("userId") Long userId);
    
    /**
     * 移除点赞
     */
    @Query(value = "DELETE FROM dynamic_likes WHERE dynamic_id = :dynamicId AND user_id = :userId", nativeQuery = true)
    void removeLike(@Param("dynamicId") Long dynamicId, @Param("userId") Long userId);
    
    /**
     * 添加评论
     */
    @Query(value = "INSERT INTO dynamic_comments (dynamic_id, user_id, content, created_at) VALUES (:dynamicId, :userId, :content, NOW())", nativeQuery = true)
    void addComment(@Param("dynamicId") Long dynamicId, @Param("userId") Long userId, @Param("content") String content);
    
    /**
     * 获取动态评论列表
     */
    @Query(value = "SELECT dc.*, u.nickname, u.avatar FROM dynamic_comments dc " +
                   "LEFT JOIN users u ON dc.user_id = u.id " +
                   "WHERE dc.dynamic_id = :dynamicId AND dc.is_deleted = false " +
                   "ORDER BY dc.created_at DESC", nativeQuery = true)
    Page<Object> getDynamicComments(@Param("dynamicId") Long dynamicId, Pageable pageable);
    
    /**
     * 添加举报记录
     */
    @Query(value = "INSERT INTO dynamic_reports (dynamic_id, user_id, reason, created_at) VALUES (:dynamicId, :userId, :reason, NOW())", nativeQuery = true)
    void addReport(@Param("dynamicId") Long dynamicId, @Param("userId") Long userId, @Param("reason") String reason);
    
    /**
     * 获取热门话题
     */
    @Query(value = "SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(d.content, '#', numbers.n), '#', -1) as topic, COUNT(*) as count " +
                   "FROM (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) numbers " +
                   "INNER JOIN dynamics d ON CHAR_LENGTH(d.content) - CHAR_LENGTH(REPLACE(d.content, '#', '')) >= numbers.n - 1 " +
                   "WHERE d.status = 'PUBLISHED' AND d.is_deleted = false " +
                   "AND d.publish_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                   "GROUP BY topic " +
                   "ORDER BY count DESC " +
                   "LIMIT :limit", nativeQuery = true)
    List<String> getTrendingTopics(@Param("limit") int limit);
    
    /**
     * 根据复杂筛选条件查询动态
     */
    @Query("SELECT d FROM Dynamic d " +
           "LEFT JOIN User u ON d.userId = u.id " +
           "WHERE d.status = 'PUBLISHED' AND d.isDeleted = false " +
           "AND (:location IS NULL OR d.location LIKE %:location%) " +
           "AND (:gender IS NULL OR u.gender = :gender) " +
           "AND (:minAge IS NULL OR u.age >= :minAge) " +
           "AND (:maxAge IS NULL OR u.age <= :maxAge) " +
           "AND (:keyword IS NULL OR d.content LIKE %:keyword%) " +
           "AND (:hasImages IS NULL OR (:hasImages = true AND d.images IS NOT NULL AND d.images != '') OR (:hasImages = false AND (d.images IS NULL OR d.images = ''))) " +
           "AND (:hasLocation IS NULL OR (:hasLocation = true AND d.location IS NOT NULL AND d.location != '') OR (:hasLocation = false AND (d.location IS NULL OR d.location = ''))) " +
           "AND (:minLikes IS NULL OR d.likeCount >= :minLikes) " +
           "AND (:maxLikes IS NULL OR d.likeCount <= :maxLikes) " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'likeCount' THEN d.likeCount END DESC, " +
           "CASE WHEN :sortBy = 'commentCount' THEN d.commentCount END DESC, " +
           "CASE WHEN :sortBy = 'viewCount' THEN d.viewCount END DESC, " +
           "d.publishTime DESC")
    Page<Dynamic> findDynamicsWithFilters(
            @Param("location") String location,
            @Param("gender") String gender,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("keyword") String keyword,
            @Param("hasImages") Boolean hasImages,
            @Param("hasLocation") Boolean hasLocation,
            @Param("minLikes") Integer minLikes,
            @Param("maxLikes") Integer maxLikes,
            @Param("sortBy") String sortBy,
            Pageable pageable);
    
    /**
     * 根据筛选请求查询动态
     */
    default Page<Dynamic> findDynamicsWithFilters(DynamicFilterRequest filterRequest, Pageable pageable) {
        return findDynamicsWithFilters(
            filterRequest.getLocation(),
            filterRequest.getGender(),
            filterRequest.getMinAge(),
            filterRequest.getMaxAge(),
            filterRequest.getKeyword(),
            filterRequest.getHasImages(),
            filterRequest.getHasLocation(),
            filterRequest.getMinLikes(),
            filterRequest.getMaxLikes(),
            filterRequest.getSortBy(),
            pageable
        );
    }
    
    /**
     * 获取用户关注的动态
     */
    @Query("SELECT d FROM Dynamic d " +
           "INNER JOIN FollowRelationship f ON d.userId = f.followingId " +
           "WHERE f.followerId = :userId AND d.status = 'PUBLISHED' AND d.isDeleted = false " +
           "ORDER BY d.publishTime DESC")
    Page<Dynamic> findFollowingDynamics(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 获取附近的动态
     */
    @Query("SELECT d FROM Dynamic d " +
           "WHERE d.location LIKE %:location% AND d.status = 'PUBLISHED' AND d.isDeleted = false " +
           "ORDER BY d.publishTime DESC")
    Page<Dynamic> findNearbyDynamics(@Param("location") String location, Pageable pageable);
    
    /**
     * 获取热门动态（按点赞数排序）
     */
    @Query("SELECT d FROM Dynamic d " +
           "WHERE d.status = 'PUBLISHED' AND d.isDeleted = false " +
           "AND d.publishTime >= :startTime " +
           "ORDER BY d.likeCount DESC, d.publishTime DESC")
    Page<Dynamic> findHotDynamics(@Param("startTime") java.time.LocalDateTime startTime, Pageable pageable);
    
    /**
     * 获取最新动态
     */
    @Query("SELECT d FROM Dynamic d " +
           "WHERE d.status = 'PUBLISHED' AND d.isDeleted = false " +
           "ORDER BY d.publishTime DESC")
    Page<Dynamic> findLatestDynamics(Pageable pageable);
    
    /**
     * 统计动态总数
     */
    @Query("SELECT COUNT(d) FROM Dynamic d WHERE d.status = 'PUBLISHED' AND d.isDeleted = false")
    long countPublishedDynamics();
    
    /**
     * 统计用户动态总数
     */
    @Query("SELECT COUNT(d) FROM Dynamic d WHERE d.userId = :userId AND d.isDeleted = false")
    long countUserDynamics(@Param("userId") Long userId);
    
    /**
     * 获取动态统计信息
     */
    @Query("SELECT " +
           "COUNT(d) as totalDynamics, " +
           "AVG(d.likeCount) as avgLikes, " +
           "AVG(d.commentCount) as avgComments, " +
           "AVG(d.viewCount) as avgViews " +
           "FROM Dynamic d " +
           "WHERE d.status = 'PUBLISHED' AND d.isDeleted = false")
    Object getDynamicStats();
}
