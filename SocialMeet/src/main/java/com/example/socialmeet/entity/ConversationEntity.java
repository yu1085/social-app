package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 会话实体类 - 管理用户之间的对话会话
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "conversations")
@Data
@EntityListeners(AuditingEntityListener.class)
public class ConversationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user1_id", nullable = false)
    private Long user1Id;
    
    @Column(name = "user2_id", nullable = false)
    private Long user2Id;
    
    @Column(name = "last_message_id")
    private Long lastMessageId;
    
    @Column(name = "last_message_content", columnDefinition = "TEXT")
    private String lastMessageContent;
    
    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;
    
    @Column(name = "unread_count_user1", nullable = false)
    private Integer unreadCountUser1 = 0;
    
    @Column(name = "unread_count_user2", nullable = false)
    private Integer unreadCountUser2 = 0;
    
    @Column(name = "is_pinned_user1", nullable = false)
    private Boolean isPinnedUser1 = false;
    
    @Column(name = "is_pinned_user2", nullable = false)
    private Boolean isPinnedUser2 = false;
    
    @Column(name = "is_muted_user1", nullable = false)
    private Boolean isMutedUser1 = false;
    
    @Column(name = "is_muted_user2", nullable = false)
    private Boolean isMutedUser2 = false;
    
    @Column(name = "is_deleted_user1", nullable = false)
    private Boolean isDeletedUser1 = false;
    
    @Column(name = "is_deleted_user2", nullable = false)
    private Boolean isDeletedUser2 = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "conversation_type", nullable = false)
    private ConversationType conversationType = ConversationType.PRIVATE;
    
    @Column(name = "conversation_name", length = 100)
    private String conversationName; // 群聊名称
    
    @Column(name = "conversation_avatar", length = 500)
    private String conversationAvatar; // 群聊头像
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 关联用户信息（不存储，通过查询获取）
    @Transient
    private User user1;
    
    @Transient
    private User user2;
    
    @Transient
    private MessageEntity lastMessage;
    
    // 会话类型枚举
    public enum ConversationType {
        PRIVATE("私聊"),
        GROUP("群聊"),
        SYSTEM("系统消息");
        
        private final String description;
        
        ConversationType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 获取指定用户的未读消息数
     */
    public Integer getUnreadCountForUser(Long userId) {
        if (userId.equals(user1Id)) {
            return unreadCountUser1;
        } else if (userId.equals(user2Id)) {
            return unreadCountUser2;
        }
        return 0;
    }
    
    /**
     * 设置指定用户的未读消息数
     */
    public void setUnreadCountForUser(Long userId, Integer count) {
        if (userId.equals(user1Id)) {
            this.unreadCountUser1 = count;
        } else if (userId.equals(user2Id)) {
            this.unreadCountUser2 = count;
        }
    }
    
    /**
     * 增加指定用户的未读消息数
     */
    public void incrementUnreadCountForUser(Long userId) {
        if (userId.equals(user1Id)) {
            this.unreadCountUser1++;
        } else if (userId.equals(user2Id)) {
            this.unreadCountUser2++;
        }
    }
    
    /**
     * 重置指定用户的未读消息数
     */
    public void resetUnreadCountForUser(Long userId) {
        setUnreadCountForUser(userId, 0);
    }
    
    /**
     * 检查指定用户是否置顶了会话
     */
    public Boolean isPinnedForUser(Long userId) {
        if (userId.equals(user1Id)) {
            return isPinnedUser1;
        } else if (userId.equals(user2Id)) {
            return isPinnedUser2;
        }
        return false;
    }
    
    /**
     * 设置指定用户的置顶状态
     */
    public void setPinnedForUser(Long userId, Boolean pinned) {
        if (userId.equals(user1Id)) {
            this.isPinnedUser1 = pinned;
        } else if (userId.equals(user2Id)) {
            this.isPinnedUser2 = pinned;
        }
    }
    
    /**
     * 检查指定用户是否静音了会话
     */
    public Boolean isMutedForUser(Long userId) {
        if (userId.equals(user1Id)) {
            return isMutedUser1;
        } else if (userId.equals(user2Id)) {
            return isMutedUser2;
        }
        return false;
    }
    
    /**
     * 设置指定用户的静音状态
     */
    public void setMutedForUser(Long userId, Boolean muted) {
        if (userId.equals(user1Id)) {
            this.isMutedUser1 = muted;
        } else if (userId.equals(user2Id)) {
            this.isMutedUser2 = muted;
        }
    }
    
    /**
     * 检查指定用户是否删除了会话
     */
    public Boolean isDeletedForUser(Long userId) {
        if (userId.equals(user1Id)) {
            return isDeletedUser1;
        } else if (userId.equals(user2Id)) {
            return isDeletedUser2;
        }
        return false;
    }
    
    /**
     * 设置指定用户的删除状态
     */
    public void setDeletedForUser(Long userId, Boolean deleted) {
        if (userId.equals(user1Id)) {
            this.isDeletedUser1 = deleted;
        } else if (userId.equals(user2Id)) {
            this.isDeletedUser2 = deleted;
        }
    }
}
