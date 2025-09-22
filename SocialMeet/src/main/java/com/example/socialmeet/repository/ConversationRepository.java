package com.example.socialmeet.repository;

import com.example.socialmeet.entity.ConversationEntity;
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
 * 会话数据访问层
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {
    
    /**
     * 根据用户ID查询会话列表
     */
    @Query("SELECT c FROM ConversationEntity c WHERE " +
           "(c.user1Id = :userId OR c.user2Id = :userId) " +
           "AND c.isActive = true " +
           "AND ((c.user1Id = :userId AND c.isDeletedUser1 = false) OR " +
           "(c.user2Id = :userId AND c.isDeletedUser2 = false)) " +
           "ORDER BY c.lastMessageTime DESC")
    Page<ConversationEntity> findConversationsByUserId(@Param("userId") Long userId, 
                                                      Pageable pageable);
    
    /**
     * 查询两个用户之间的会话
     */
    @Query("SELECT c FROM ConversationEntity c WHERE " +
           "((c.user1Id = :userId1 AND c.user2Id = :userId2) OR " +
           "(c.user1Id = :userId2 AND c.user2Id = :userId1)) " +
           "AND c.isActive = true")
    Optional<ConversationEntity> findConversationBetweenUsers(@Param("userId1") Long userId1, 
                                                             @Param("userId2") Long userId2);
    
    /**
     * 查询置顶的会话
     */
    @Query("SELECT c FROM ConversationEntity c WHERE " +
           "((c.user1Id = :userId AND c.isPinnedUser1 = true) OR " +
           "(c.user2Id = :userId AND c.isPinnedUser2 = true)) " +
           "AND c.isActive = true " +
           "AND ((c.user1Id = :userId AND c.isDeletedUser1 = false) OR " +
           "(c.user2Id = :userId AND c.isDeletedUser2 = false)) " +
           "ORDER BY c.lastMessageTime DESC")
    List<ConversationEntity> findPinnedConversationsByUserId(@Param("userId") Long userId);
    
    /**
     * 查询有未读消息的会话
     */
    @Query("SELECT c FROM ConversationEntity c WHERE " +
           "((c.user1Id = :userId AND c.unreadCountUser1 > 0) OR " +
           "(c.user2Id = :userId AND c.unreadCountUser2 > 0)) " +
           "AND c.isActive = true " +
           "AND ((c.user1Id = :userId AND c.isDeletedUser1 = false) OR " +
           "(c.user2Id = :userId AND c.isDeletedUser2 = false)) " +
           "ORDER BY c.lastMessageTime DESC")
    List<ConversationEntity> findUnreadConversationsByUserId(@Param("userId") Long userId);
    
    /**
     * 查询静音的会话
     */
    @Query("SELECT c FROM ConversationEntity c WHERE " +
           "((c.user1Id = :userId AND c.isMutedUser1 = true) OR " +
           "(c.user2Id = :userId AND c.isMutedUser2 = true)) " +
           "AND c.isActive = true " +
           "AND ((c.user1Id = :userId AND c.isDeletedUser1 = false) OR " +
           "(c.user2Id = :userId AND c.isDeletedUser2 = false)) " +
           "ORDER BY c.lastMessageTime DESC")
    List<ConversationEntity> findMutedConversationsByUserId(@Param("userId") Long userId);
    
    /**
     * 根据会话类型查询
     */
    @Query("SELECT c FROM ConversationEntity c WHERE " +
           "(c.user1Id = :userId OR c.user2Id = :userId) " +
           "AND c.conversationType = :conversationType " +
           "AND c.isActive = true " +
           "AND ((c.user1Id = :userId AND c.isDeletedUser1 = false) OR " +
           "(c.user2Id = :userId AND c.isDeletedUser2 = false)) " +
           "ORDER BY c.lastMessageTime DESC")
    Page<ConversationEntity> findConversationsByType(@Param("userId") Long userId, 
                                                     @Param("conversationType") ConversationEntity.ConversationType conversationType, 
                                                     Pageable pageable);
    
    /**
     * 搜索会话
     */
    @Query("SELECT c FROM ConversationEntity c WHERE " +
           "(c.user1Id = :userId OR c.user2Id = :userId) " +
           "AND (c.conversationName LIKE %:keyword% OR c.lastMessageContent LIKE %:keyword%) " +
           "AND c.isActive = true " +
           "AND ((c.user1Id = :userId AND c.isDeletedUser1 = false) OR " +
           "(c.user2Id = :userId AND c.isDeletedUser2 = false)) " +
           "ORDER BY c.lastMessageTime DESC")
    Page<ConversationEntity> searchConversations(@Param("userId") Long userId, 
                                                @Param("keyword") String keyword, 
                                                Pageable pageable);
    
    /**
     * 更新会话的最后消息
     */
    @Modifying
    @Transactional
    @Query("UPDATE ConversationEntity c SET " +
           "c.lastMessageId = :messageId, " +
           "c.lastMessageContent = :content, " +
           "c.lastMessageTime = :messageTime " +
           "WHERE c.id = :conversationId")
    int updateLastMessage(@Param("conversationId") Long conversationId,
                         @Param("messageId") Long messageId,
                         @Param("content") String content,
                         @Param("messageTime") LocalDateTime messageTime);
    
    /**
     * 增加未读消息数
     */
    @Modifying
    @Transactional
    @Query("UPDATE ConversationEntity c SET " +
           "c.unreadCountUser1 = CASE WHEN c.user1Id = :userId THEN c.unreadCountUser1 + 1 ELSE c.unreadCountUser1 END, " +
           "c.unreadCountUser2 = CASE WHEN c.user2Id = :userId THEN c.unreadCountUser2 + 1 ELSE c.unreadCountUser2 END " +
           "WHERE c.id = :conversationId")
    int incrementUnreadCount(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
    
    /**
     * 重置未读消息数
     */
    @Modifying
    @Transactional
    @Query("UPDATE ConversationEntity c SET " +
           "c.unreadCountUser1 = CASE WHEN c.user1Id = :userId THEN 0 ELSE c.unreadCountUser1 END, " +
           "c.unreadCountUser2 = CASE WHEN c.user2Id = :userId THEN 0 ELSE c.unreadCountUser2 END " +
           "WHERE c.id = :conversationId")
    int resetUnreadCount(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
    
    /**
     * 设置置顶状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE ConversationEntity c SET " +
           "c.isPinnedUser1 = CASE WHEN c.user1Id = :userId THEN :pinned ELSE c.isPinnedUser1 END, " +
           "c.isPinnedUser2 = CASE WHEN c.user2Id = :userId THEN :pinned ELSE c.isPinnedUser2 END " +
           "WHERE c.id = :conversationId")
    int setPinnedStatus(@Param("conversationId") Long conversationId, 
                       @Param("userId") Long userId, 
                       @Param("pinned") Boolean pinned);
    
    /**
     * 设置静音状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE ConversationEntity c SET " +
           "c.isMutedUser1 = CASE WHEN c.user1Id = :userId THEN :muted ELSE c.isMutedUser1 END, " +
           "c.isMutedUser2 = CASE WHEN c.user2Id = :userId THEN :muted ELSE c.isMutedUser2 END " +
           "WHERE c.id = :conversationId")
    int setMutedStatus(@Param("conversationId") Long conversationId, 
                      @Param("userId") Long userId, 
                      @Param("muted") Boolean muted);
    
    /**
     * 删除会话（软删除）
     */
    @Modifying
    @Transactional
    @Query("UPDATE ConversationEntity c SET " +
           "c.isDeletedUser1 = CASE WHEN c.user1Id = :userId THEN true ELSE c.isDeletedUser1 END, " +
           "c.isDeletedUser2 = CASE WHEN c.user2Id = :userId THEN true ELSE c.isDeletedUser2 END " +
           "WHERE c.id = :conversationId")
    int deleteConversationForUser(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
    
    /**
     * 查询用户总未读消息数
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN c.user1Id = :userId THEN c.unreadCountUser1 ELSE c.unreadCountUser2 END), 0) " +
           "FROM ConversationEntity c WHERE " +
           "(c.user1Id = :userId OR c.user2Id = :userId) " +
           "AND c.isActive = true " +
           "AND ((c.user1Id = :userId AND c.isDeletedUser1 = false) OR " +
           "(c.user2Id = :userId AND c.isDeletedUser2 = false))")
    Long getTotalUnreadCountByUserId(@Param("userId") Long userId);
    
    /**
     * 查询会话统计信息
     */
    @Query("SELECT " +
           "COUNT(c) as totalConversations, " +
           "COUNT(CASE WHEN (c.user1Id = :userId AND c.unreadCountUser1 > 0) OR " +
           "(c.user2Id = :userId AND c.unreadCountUser2 > 0) THEN 1 END) as unreadConversations, " +
           "COUNT(CASE WHEN (c.user1Id = :userId AND c.isPinnedUser1 = true) OR " +
           "(c.user2Id = :userId AND c.isPinnedUser2 = true) THEN 1 END) as pinnedConversations " +
           "FROM ConversationEntity c WHERE " +
           "(c.user1Id = :userId OR c.user2Id = :userId) " +
           "AND c.isActive = true " +
           "AND ((c.user1Id = :userId AND c.isDeletedUser1 = false) OR " +
           "(c.user2Id = :userId AND c.isDeletedUser2 = false))")
    Object[] getConversationStatistics(@Param("userId") Long userId);
}
