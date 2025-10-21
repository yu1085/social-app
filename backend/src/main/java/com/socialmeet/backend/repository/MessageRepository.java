package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息数据访问层
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * 获取两个用户之间的聊天记录
     */
    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findChatHistory(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    /**
     * 获取用户的所有未读消息
     */
    @Query("SELECT m FROM Message m WHERE m.receiverId = :userId AND m.isRead = false ORDER BY m.createdAt ASC")
    List<Message> findUnreadMessages(@Param("userId") Long userId);
    
    /**
     * 获取用户与特定用户的未读消息数量
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE " +
           "((m.senderId = :senderId AND m.receiverId = :receiverId) OR " +
           "(m.senderId = :receiverId AND m.receiverId = :senderId)) AND " +
           "m.receiverId = :currentUserId AND m.isRead = false")
    Long countUnreadMessages(@Param("senderId") Long senderId, 
                            @Param("receiverId") Long receiverId, 
                            @Param("currentUserId") Long currentUserId);
    
    /**
     * 获取用户最近的联系人列表（有消息往来的用户）
     */
    @Query("SELECT DISTINCT CASE " +
           "WHEN m.senderId = :userId THEN m.receiverId " +
           "ELSE m.senderId END " +
           "FROM Message m WHERE m.senderId = :userId OR m.receiverId = :userId " +
           "ORDER BY MAX(m.createdAt) DESC")
    List<Long> findRecentContacts(@Param("userId") Long userId);
    
    /**
     * 获取指定时间之后的消息
     */
    @Query("SELECT m FROM Message m WHERE " +
           "((m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1)) AND " +
           "m.createdAt > :since " +
           "ORDER BY m.createdAt ASC")
    List<Message> findMessagesSince(@Param("userId1") Long userId1,
                                   @Param("userId2") Long userId2,
                                   @Param("since") LocalDateTime since);

    /**
     * 获取用户所有相关的消息（发送或接收）
     */
    @Query("SELECT m FROM Message m WHERE m.senderId = :userId OR m.receiverId = :userId ORDER BY m.createdAt DESC")
    List<Message> findUserMessages(@Param("userId") Long userId);
}
