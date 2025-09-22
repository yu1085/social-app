package com.example.socialmeet.repository;

import com.example.socialmeet.entity.MessageEntity;
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
 * 消息数据访问层
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    
    /**
     * 根据发送者和接收者查询消息
     */
    @Query("SELECT m FROM MessageEntity m WHERE " +
           "((m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1)) " +
           "AND m.isDeleted = false " +
           "ORDER BY m.sendTime DESC")
    Page<MessageEntity> findMessagesBetweenUsers(@Param("userId1") Long userId1, 
                                                @Param("userId2") Long userId2, 
                                                Pageable pageable);
    
    /**
     * 根据会话ID查询消息
     */
    @Query("SELECT m FROM MessageEntity m WHERE m.conversationId = :conversationId " +
           "AND m.isDeleted = false ORDER BY m.sendTime DESC")
    Page<MessageEntity> findByConversationId(@Param("conversationId") Long conversationId, 
                                            Pageable pageable);
    
    /**
     * 查询用户未读消息数量
     */
    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE m.receiverId = :userId " +
           "AND m.isRead = false AND m.isDeleted = false")
    Long countUnreadMessagesByUserId(@Param("userId") Long userId);
    
    /**
     * 查询两个用户之间的未读消息数量
     */
    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE " +
           "m.senderId = :senderId AND m.receiverId = :receiverId " +
           "AND m.isRead = false AND m.isDeleted = false")
    Long countUnreadMessagesBetweenUsers(@Param("senderId") Long senderId, 
                                        @Param("receiverId") Long receiverId);
    
    /**
     * 查询用户最近的消息
     */
    @Query("SELECT m FROM MessageEntity m WHERE " +
           "(m.senderId = :userId OR m.receiverId = :userId) " +
           "AND m.isDeleted = false " +
           "ORDER BY m.sendTime DESC")
    List<MessageEntity> findRecentMessagesByUserId(@Param("userId") Long userId, 
                                                   Pageable pageable);
    
    /**
     * 根据消息类型查询
     */
    @Query("SELECT m FROM MessageEntity m WHERE m.messageType = :messageType " +
           "AND m.isDeleted = false ORDER BY m.sendTime DESC")
    Page<MessageEntity> findByMessageType(@Param("messageType") MessageEntity.MessageType messageType, 
                                         Pageable pageable);
    
    /**
     * 搜索消息内容
     */
    @Query("SELECT m FROM MessageEntity m WHERE " +
           "(m.senderId = :userId OR m.receiverId = :userId) " +
           "AND m.content LIKE %:keyword% " +
           "AND m.isDeleted = false " +
           "ORDER BY m.sendTime DESC")
    Page<MessageEntity> searchMessagesByContent(@Param("userId") Long userId, 
                                               @Param("keyword") String keyword, 
                                               Pageable pageable);
    
    /**
     * 查询指定时间范围内的消息
     */
    @Query("SELECT m FROM MessageEntity m WHERE " +
           "(m.senderId = :userId OR m.receiverId = :userId) " +
           "AND m.sendTime BETWEEN :startTime AND :endTime " +
           "AND m.isDeleted = false " +
           "ORDER BY m.sendTime DESC")
    List<MessageEntity> findMessagesByTimeRange(@Param("userId") Long userId,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);
    
    /**
     * 标记消息为已读
     */
    @Modifying
    @Transactional
    @Query("UPDATE MessageEntity m SET m.isRead = true, m.readTime = :readTime " +
           "WHERE m.receiverId = :userId AND m.isRead = false AND m.isDeleted = false")
    int markMessagesAsRead(@Param("userId") Long userId, @Param("readTime") LocalDateTime readTime);
    
    /**
     * 标记指定发送者的消息为已读
     */
    @Modifying
    @Transactional
    @Query("UPDATE MessageEntity m SET m.isRead = true, m.readTime = :readTime " +
           "WHERE m.senderId = :senderId AND m.receiverId = :userId " +
           "AND m.isRead = false AND m.isDeleted = false")
    int markMessagesAsReadFromSender(@Param("userId") Long userId, 
                                    @Param("senderId") Long senderId, 
                                    @Param("readTime") LocalDateTime readTime);
    
    /**
     * 撤回消息
     */
    @Modifying
    @Transactional
    @Query("UPDATE MessageEntity m SET m.isRecalled = true, m.recallTime = :recallTime " +
           "WHERE m.id = :messageId AND m.senderId = :userId")
    int recallMessage(@Param("messageId") Long messageId, 
                     @Param("userId") Long userId, 
                     @Param("recallTime") LocalDateTime recallTime);
    
    /**
     * 删除消息（软删除）
     */
    @Modifying
    @Transactional
    @Query("UPDATE MessageEntity m SET m.isDeleted = true " +
           "WHERE m.id = :messageId AND (m.senderId = :userId OR m.receiverId = :userId)")
    int deleteMessage(@Param("messageId") Long messageId, @Param("userId") Long userId);
    
    /**
     * 查询消息统计信息
     */
    @Query("SELECT " +
           "COUNT(m) as totalMessages, " +
           "COUNT(CASE WHEN m.isRead = false THEN 1 END) as unreadMessages, " +
           "COUNT(CASE WHEN m.messageType = 'TEXT' THEN 1 END) as textMessages, " +
           "COUNT(CASE WHEN m.messageType = 'IMAGE' THEN 1 END) as imageMessages, " +
           "COUNT(CASE WHEN m.messageType = 'VIDEO' THEN 1 END) as videoMessages " +
           "FROM MessageEntity m WHERE m.senderId = :userId OR m.receiverId = :userId")
    Object[] getMessageStatistics(@Param("userId") Long userId);
    
    /**
     * 查询最后一条消息
     */
    @Query("SELECT m FROM MessageEntity m WHERE " +
           "((m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1)) " +
           "AND m.isDeleted = false " +
           "ORDER BY m.sendTime DESC")
    List<MessageEntity> findLastMessageBetweenUsers(@Param("userId1") Long userId1, 
                                                   @Param("userId2") Long userId2, 
                                                   Pageable pageable);
    
    /**
     * 查询消息详情（包含发送者和接收者信息）
     */
    @Query("SELECT m FROM MessageEntity m WHERE m.id = :messageId")
    Optional<MessageEntity> findMessageWithDetails(@Param("messageId") Long messageId);
}