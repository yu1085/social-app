package com.example.socialmeet.repository;

import com.example.socialmeet.entity.User;
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
import java.util.Optional;

/**
 * 优化后的用户Repository
 */
@Repository
public interface OptimizedUserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     */
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
    
    /**
     * 根据手机号查找用户
     */
    @Query("SELECT u FROM User u WHERE u.phone = :phone")
    Optional<User> findByPhone(@Param("phone") String phone);
    
    /**
     * 获取在线用户 - 使用复合索引
     */
    @Query("SELECT u FROM User u WHERE u.isOnline = true AND u.status = 'ONLINE' ORDER BY u.lastSeen DESC")
    Page<User> findOnlineUsers(Pageable pageable);
    
    /**
     * 根据性别获取在线用户
     */
    @Query("SELECT u FROM User u WHERE u.gender = :gender AND u.isOnline = true ORDER BY u.lastSeen DESC")
    Page<User> findOnlineUsersByGender(@Param("gender") String gender, Pageable pageable);
    
    /**
     * 根据位置获取用户
     */
    @Query("SELECT u FROM User u WHERE u.location LIKE %:location% AND u.isOnline = true ORDER BY u.lastSeen DESC")
    Page<User> findUsersByLocation(@Param("location") String location, Pageable pageable);
    
    /**
     * 根据性别和位置获取用户
     */
    @Query("SELECT u FROM User u WHERE u.gender = :gender AND u.location LIKE %:location% AND u.isOnline = true ORDER BY u.lastSeen DESC")
    Page<User> findUsersByGenderAndLocation(@Param("gender") String gender, @Param("location") String location, Pageable pageable);
    
    /**
     * 搜索用户 - 使用全文索引
     */
    @Query("SELECT u FROM User u WHERE " +
           "(u.nickname LIKE %:keyword% OR u.bio LIKE %:keyword%) " +
           "AND (:gender IS NULL OR u.gender = :gender) " +
           "AND u.isOnline = true " +
           "ORDER BY u.lastSeen DESC")
    Page<User> searchUsers(@Param("keyword") String keyword, @Param("gender") String gender, Pageable pageable);
    
    /**
     * 获取新用户
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :since ORDER BY u.createdAt DESC")
    Page<User> findNewUsers(@Param("since") LocalDateTime since, Pageable pageable);
    
    /**
     * 获取热门用户 - 按关注数排序
     */
    @Query("SELECT u FROM User u WHERE u.isOnline = true ORDER BY u.followerCount DESC, u.likeCount DESC")
    Page<User> findHotUsers(Pageable pageable);
    
    /**
     * 获取用户关注列表
     */
    @Query("SELECT u FROM User u " +
           "JOIN FollowRelationship f ON u.id = f.followingId " +
           "WHERE f.followerId = :userId " +
           "ORDER BY f.createdAt DESC")
    Page<User> findFollowingUsers(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 获取用户粉丝列表
     */
    @Query("SELECT u FROM User u " +
           "JOIN FollowRelationship f ON u.id = f.followerId " +
           "WHERE f.followingId = :userId " +
           "ORDER BY f.createdAt DESC")
    Page<User> findFollowerUsers(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 更新用户在线状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isOnline = :isOnline, u.lastSeen = :lastSeen WHERE u.id = :userId")
    int updateUserOnlineStatus(@Param("userId") Long userId, @Param("isOnline") Boolean isOnline, @Param("lastSeen") LocalDateTime lastSeen);
    
    /**
     * 更新用户状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status, u.updatedAt = :updatedAt WHERE u.id = :userId")
    int updateUserStatus(@Param("userId") Long userId, @Param("status") String status, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 更新用户最后在线时间
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastSeen = :lastSeen WHERE u.id = :userId")
    int updateUserLastSeen(@Param("userId") Long userId, @Param("lastSeen") LocalDateTime lastSeen);
    
    /**
     * 增加用户关注数
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.followerCount = u.followerCount + 1 WHERE u.id = :userId")
    int incrementFollowerCount(@Param("userId") Long userId);
    
    /**
     * 减少用户关注数
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.followerCount = GREATEST(u.followerCount - 1, 0) WHERE u.id = :userId")
    int decrementFollowerCount(@Param("userId") Long userId);
    
    /**
     * 增加用户点赞数
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.likeCount = u.likeCount + 1 WHERE u.id = :userId")
    int incrementLikeCount(@Param("userId") Long userId);
    
    /**
     * 减少用户点赞数
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.likeCount = GREATEST(u.likeCount - 1, 0) WHERE u.id = :userId")
    int decrementLikeCount(@Param("userId") Long userId);
    
    /**
     * 获取用户统计信息
     */
    @Query("SELECT " +
           "COUNT(u) as totalUsers, " +
           "COUNT(CASE WHEN u.isOnline = true THEN 1 END) as onlineUsers, " +
           "COUNT(CASE WHEN u.gender = '男' THEN 1 END) as maleUsers, " +
           "COUNT(CASE WHEN u.gender = '女' THEN 1 END) as femaleUsers " +
           "FROM User u")
    Object[] getUserStatistics();
    
    /**
     * 获取用户详细信息
     */
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findUserById(@Param("userId") Long userId);
    
    /**
     * 检查用户是否存在
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.id = :userId")
    boolean existsById(@Param("userId") Long userId);
    
    /**
     * 检查用户名是否存在
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);
    
    /**
     * 检查手机号是否存在
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phone = :phone")
    boolean existsByPhone(@Param("phone") String phone);
    
    /**
     * 获取用户关注数
     */
    @Query("SELECT COUNT(f) FROM FollowRelationship f WHERE f.followerId = :userId")
    Long countUserFollowing(@Param("userId") Long userId);
    
    /**
     * 获取用户粉丝数
     */
    @Query("SELECT COUNT(f) FROM FollowRelationship f WHERE f.followingId = :userId")
    Long countUserFollowers(@Param("userId") Long userId);
    
    /**
     * 检查用户是否关注了另一个用户
     */
    @Query("SELECT COUNT(f) > 0 FROM FollowRelationship f WHERE f.followerId = :followerId AND f.followingId = :followingId")
    boolean isUserFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    
    /**
     * 根据昵称模糊查询用户
     */
    @Query("SELECT u FROM User u WHERE u.nickname LIKE %:nickname%")
    List<User> findByNicknameContaining(@Param("nickname") String nickname);
    
    /**
     * 获取用户推荐列表
     */
    @Query("SELECT u FROM User u WHERE " +
           "u.id != :userId AND " +
           "u.gender != :userGender AND " +
           "u.isOnline = true AND " +
           "u.id NOT IN (SELECT f.followingId FROM FollowRelationship f WHERE f.followerId = :userId) " +
           "ORDER BY u.followerCount DESC, u.likeCount DESC")
    Page<User> findRecommendedUsers(@Param("userId") Long userId, @Param("userGender") String userGender, Pageable pageable);
    
    /**
     * 获取用户附近的用户
     */
    @Query("SELECT u FROM User u WHERE " +
           "u.location = :location AND " +
           "u.id != :userId AND " +
           "u.isOnline = true " +
           "ORDER BY u.lastSeen DESC")
    Page<User> findNearbyUsers(@Param("userId") Long userId, @Param("location") String location, Pageable pageable);
    
    /**
     * 批量更新用户状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isOnline = false, u.status = 'OFFLINE' WHERE u.lastSeen < :offlineTime")
    int updateOfflineUsers(@Param("offlineTime") LocalDateTime offlineTime);
    
    /**
     * 获取用户活跃度统计
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN u.lastSeen >= :today THEN 1 END) as todayActive, " +
           "COUNT(CASE WHEN u.lastSeen >= :weekAgo THEN 1 END) as weekActive, " +
           "COUNT(CASE WHEN u.lastSeen >= :monthAgo THEN 1 END) as monthActive " +
           "FROM User u")
    Object[] getUserActivityStats(@Param("today") LocalDateTime today, 
                                 @Param("weekAgo") LocalDateTime weekAgo, 
                                 @Param("monthAgo") LocalDateTime monthAgo);
    
    
    /**
     * 获取用户完整信息（包含统计）
     */
    @Query("SELECT u, " +
           "COUNT(DISTINCT f1.followingId) as followingCount, " +
           "COUNT(DISTINCT f2.followerId) as followerCount, " +
           "COUNT(DISTINCT d.id) as dynamicCount " +
           "FROM User u " +
           "LEFT JOIN FollowRelationship f1 ON u.id = f1.followerId " +
           "LEFT JOIN FollowRelationship f2 ON u.id = f2.followingId " +
           "LEFT JOIN Dynamic d ON u.id = d.userId AND d.isDeleted = false " +
           "WHERE u.id = :userId " +
           "GROUP BY u.id")
    Object[] findUserWithStats(@Param("userId") Long userId);
    
    /**
     * 根据性别和在线状态查询用户，按最后在线时间排序
     */
    @Query("SELECT u FROM User u WHERE u.gender = :gender AND u.isOnline = :isOnline ORDER BY u.lastSeen DESC")
    Page<User> findByGenderAndIsOnlineOrderByLastSeenDesc(@Param("gender") String gender, @Param("isOnline") boolean isOnline, Pageable pageable);
    
    /**
     * 根据性别和在线状态和状态查询用户，按最后在线时间排序
     */
    @Query("SELECT u FROM User u WHERE u.gender = :gender AND u.isOnline = :isOnline AND u.status = :status ORDER BY u.lastSeen DESC")
    Page<User> findByGenderAndIsOnlineAndStatusOrderByLastSeenDesc(@Param("gender") String gender, @Param("isOnline") boolean isOnline, @Param("status") String status, Pageable pageable);
    
    /**
     * 根据性别查询用户，按创建时间排序
     */
    @Query("SELECT u FROM User u WHERE u.gender = :gender ORDER BY u.createdAt DESC")
    Page<User> findByGenderOrderByCreatedAtDesc(@Param("gender") String gender, Pageable pageable);
    
    /**
     * 根据性别查询用户，按关注者数量排序
     */
    @Query("SELECT u FROM User u WHERE u.gender = :gender ORDER BY u.followerCount DESC")
    Page<User> findByGenderOrderByFollowerCountDesc(@Param("gender") String gender, Pageable pageable);
    
    /**
     * 根据性别查询用户，按最后在线时间排序
     */
    @Query("SELECT u FROM User u WHERE u.gender = :gender ORDER BY u.lastSeen DESC")
    Page<User> findByGenderOrderByLastSeenDesc(@Param("gender") String gender, Pageable pageable);
    
    /**
     * 根据昵称和性别搜索用户
     */
    @Query("SELECT u FROM User u WHERE u.nickname LIKE %:nickname% AND u.gender = :gender")
    Page<User> findByNicknameContainingAndGender(@Param("nickname") String nickname, @Param("gender") String gender, Pageable pageable);
    
    /**
     * 根据位置和性别搜索用户
     */
    @Query("SELECT u FROM User u WHERE u.location LIKE %:location% AND u.gender = :gender")
    Page<User> findByLocationContainingAndGender(@Param("location") String location, @Param("gender") String gender, Pageable pageable);
    
    /**
     * 查询活跃用户
     */
    @Query("SELECT u FROM User u WHERE u.isOnline = true OR u.lastSeen > :since ORDER BY u.lastSeen DESC")
    Page<User> findActiveUsers(@Param("since") LocalDateTime since, Pageable pageable);
    
    /**
     * 统计在线用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isOnline = :isOnline AND u.status = :status")
    long countByIsOnlineAndStatus(@Param("isOnline") boolean isOnline, @Param("status") String status);
}
