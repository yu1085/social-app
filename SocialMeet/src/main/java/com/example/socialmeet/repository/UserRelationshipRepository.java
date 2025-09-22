package com.example.socialmeet.repository;

import com.example.socialmeet.entity.UserRelationshipEntity;
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
 * 用户关系数据访问层
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface UserRelationshipRepository extends JpaRepository<UserRelationshipEntity, Long> {
    
    /**
     * 查询两个用户之间的关系
     */
    @Query("SELECT r FROM UserRelationshipEntity r WHERE " +
           "((r.user1Id = :userId1 AND r.user2Id = :userId2) OR " +
           "(r.user1Id = :userId2 AND r.user2Id = :userId1)) " +
           "AND r.relationshipType = :relationshipType " +
           "AND r.status = 'ACTIVE'")
    Optional<UserRelationshipEntity> findRelationshipBetweenUsers(@Param("userId1") Long userId1, 
                                                                @Param("userId2") Long userId2, 
                                                                @Param("relationshipType") UserRelationshipEntity.RelationshipType relationshipType);
    
    /**
     * 查询用户的所有关系
     */
    @Query("SELECT r FROM UserRelationshipEntity r WHERE " +
           "(r.user1Id = :userId OR r.user2Id = :userId) " +
           "AND r.status = 'ACTIVE' " +
           "ORDER BY r.lastInteractionTime DESC")
    Page<UserRelationshipEntity> findRelationshipsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 根据关系类型查询
     */
    @Query("SELECT r FROM UserRelationshipEntity r WHERE " +
           "(r.user1Id = :userId OR r.user2Id = :userId) " +
           "AND r.relationshipType = :relationshipType " +
           "AND r.status = 'ACTIVE' " +
           "ORDER BY r.lastInteractionTime DESC")
    Page<UserRelationshipEntity> findByRelationshipType(@Param("userId") Long userId, 
                                                       @Param("relationshipType") UserRelationshipEntity.RelationshipType relationshipType, 
                                                       Pageable pageable);
    
    /**
     * 查询知友列表
     */
    @Query("SELECT r FROM UserRelationshipEntity r WHERE " +
           "(r.user1Id = :userId OR r.user2Id = :userId) " +
           "AND r.relationshipType = 'FRIEND' " +
           "AND r.status = 'ACTIVE' " +
           "ORDER BY r.intimacyScore DESC, r.lastChatTime DESC")
    List<UserRelationshipEntity> findFriendsByUserId(@Param("userId") Long userId);
    
    /**
     * 查询喜欢列表
     */
    @Query("SELECT r FROM UserRelationshipEntity r WHERE " +
           "r.user1Id = :userId " +
           "AND r.relationshipType = 'LIKE' " +
           "AND r.status = 'ACTIVE' " +
           "ORDER BY r.lastInteractionTime DESC")
    List<UserRelationshipEntity> findLikesByUserId(@Param("userId") Long userId);
    
    /**
     * 查询亲密关系列表
     */
    @Query("SELECT r FROM UserRelationshipEntity r WHERE " +
           "(r.user1Id = :userId OR r.user2Id = :userId) " +
           "AND r.relationshipType = 'INTIMATE' " +
           "AND r.status = 'ACTIVE' " +
           "ORDER BY r.intimacyScore DESC")
    List<UserRelationshipEntity> findIntimateRelationshipsByUserId(@Param("userId") Long userId);
    
    /**
     * 查询高亲密度关系
     */
    @Query("SELECT r FROM UserRelationshipEntity r WHERE " +
           "(r.user1Id = :userId OR r.user2Id = :userId) " +
           "AND r.intimacyScore >= :minIntimacyScore " +
           "AND r.status = 'ACTIVE' " +
           "ORDER BY r.intimacyScore DESC")
    List<UserRelationshipEntity> findHighIntimacyRelationships(@Param("userId") Long userId, 
                                                              @Param("minIntimacyScore") Integer minIntimacyScore);
    
    /**
     * 查询最近聊天的关系
     */
    @Query("SELECT r FROM UserRelationshipEntity r WHERE " +
           "(r.user1Id = :userId OR r.user2Id = :userId) " +
           "AND r.lastChatTime IS NOT NULL " +
           "AND r.status = 'ACTIVE' " +
           "ORDER BY r.lastChatTime DESC")
    Page<UserRelationshipEntity> findRecentChatRelationships(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 查询相互关注的关系
     */
    @Query("SELECT r FROM UserRelationshipEntity r WHERE " +
           "(r.user1Id = :userId OR r.user2Id = :userId) " +
           "AND r.isMutual = true " +
           "AND r.status = 'ACTIVE' " +
           "ORDER BY r.lastInteractionTime DESC")
    List<UserRelationshipEntity> findMutualRelationships(@Param("userId") Long userId);
    
    /**
     * 检查两个用户是否有指定类型的关系
     */
    @Query("SELECT COUNT(r) > 0 FROM UserRelationshipEntity r WHERE " +
           "((r.user1Id = :userId1 AND r.user2Id = :userId2) OR " +
           "(r.user1Id = :userId2 AND r.user2Id = :userId1)) " +
           "AND r.relationshipType = :relationshipType " +
           "AND r.status = 'ACTIVE'")
    boolean hasRelationship(@Param("userId1") Long userId1, 
                           @Param("userId2") Long userId2, 
                           @Param("relationshipType") UserRelationshipEntity.RelationshipType relationshipType);
    
    /**
     * 更新亲密度评分
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserRelationshipEntity r SET r.intimacyScore = :intimacyScore " +
           "WHERE r.id = :relationshipId")
    int updateIntimacyScore(@Param("relationshipId") Long relationshipId, 
                           @Param("intimacyScore") Integer intimacyScore);
    
    /**
     * 增加聊天频次
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserRelationshipEntity r SET " +
           "r.chatFrequency = r.chatFrequency + 1, " +
           "r.lastChatTime = :lastChatTime " +
           "WHERE r.id = :relationshipId")
    int incrementChatFrequency(@Param("relationshipId") Long relationshipId, 
                              @Param("lastChatTime") LocalDateTime lastChatTime);
    
    /**
     * 增加互动次数
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserRelationshipEntity r SET " +
           "r.interactionCount = r.interactionCount + 1, " +
           "r.lastInteractionTime = :lastInteractionTime " +
           "WHERE r.id = :relationshipId")
    int incrementInteraction(@Param("relationshipId") Long relationshipId, 
                            @Param("lastInteractionTime") LocalDateTime lastInteractionTime);
    
    /**
     * 增加点赞次数
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserRelationshipEntity r SET r.likeCount = r.likeCount + 1 " +
           "WHERE r.id = :relationshipId")
    int incrementLikeCount(@Param("relationshipId") Long relationshipId);
    
    /**
     * 增加评论次数
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserRelationshipEntity r SET r.commentCount = r.commentCount + 1 " +
           "WHERE r.id = :relationshipId")
    int incrementCommentCount(@Param("relationshipId") Long relationshipId);
    
    /**
     * 增加通话记录
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserRelationshipEntity r SET " +
           "r.callCount = r.callCount + 1, " +
           "r.callDuration = r.callDuration + :duration " +
           "WHERE r.id = :relationshipId")
    int addCallRecord(@Param("relationshipId") Long relationshipId, 
                     @Param("duration") Integer duration);
    
    /**
     * 增加送礼记录
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserRelationshipEntity r SET " +
           "r.giftCount = r.giftCount + 1, " +
           "r.giftValue = r.giftValue + :value " +
           "WHERE r.id = :relationshipId")
    int addGiftRecord(@Param("relationshipId") Long relationshipId, 
                     @Param("value") Integer value);
    
    /**
     * 设置相互关注状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserRelationshipEntity r SET r.isMutual = :isMutual " +
           "WHERE r.id = :relationshipId")
    int setMutualStatus(@Param("relationshipId") Long relationshipId, 
                       @Param("isMutual") Boolean isMutual);
    
    /**
     * 删除关系（软删除）
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserRelationshipEntity r SET r.status = 'DELETED' " +
           "WHERE r.id = :relationshipId")
    int deleteRelationship(@Param("relationshipId") Long relationshipId);
    
    /**
     * 查询关系统计信息
     */
    @Query("SELECT " +
           "COUNT(r) as totalRelationships, " +
           "COUNT(CASE WHEN r.relationshipType = 'FRIEND' THEN 1 END) as friendCount, " +
           "COUNT(CASE WHEN r.relationshipType = 'LIKE' THEN 1 END) as likeCount, " +
           "COUNT(CASE WHEN r.relationshipType = 'INTIMATE' THEN 1 END) as intimateCount, " +
           "COUNT(CASE WHEN r.isMutual = true THEN 1 END) as mutualCount " +
           "FROM UserRelationshipEntity r WHERE " +
           "(r.user1Id = :userId OR r.user2Id = :userId) " +
           "AND r.status = 'ACTIVE'")
    Object[] getRelationshipStatistics(@Param("userId") Long userId);
}
