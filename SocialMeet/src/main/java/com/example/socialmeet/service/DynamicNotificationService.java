package com.example.socialmeet.service;

import com.example.socialmeet.entity.DynamicNotification;
import com.example.socialmeet.repository.DynamicNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态通知服务
 * 处理动态相关的通知功能
 */
@Service
public class DynamicNotificationService {
    
    @Autowired
    private DynamicNotificationRepository notificationRepository;
    
    /**
     * 发送点赞通知
     */
    @Transactional
    public void sendLikeNotification(Long dynamicId, Long fromUserId, Long toUserId) {
        DynamicNotification notification = new DynamicNotification();
        notification.setUserId(toUserId);
        notification.setDynamicId(dynamicId);
        notification.setType("LIKE");
        notification.setFromUserId(fromUserId);
        notification.setContent("有人点赞了你的动态");
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    /**
     * 发送评论通知
     */
    @Transactional
    public void sendCommentNotification(Long dynamicId, Long fromUserId, Long toUserId, String commentContent) {
        DynamicNotification notification = new DynamicNotification();
        notification.setUserId(toUserId);
        notification.setDynamicId(dynamicId);
        notification.setType("COMMENT");
        notification.setFromUserId(fromUserId);
        notification.setContent("有人评论了你的动态: " + truncateContent(commentContent, 50));
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    /**
     * 发送分享通知
     */
    @Transactional
    public void sendShareNotification(Long dynamicId, Long fromUserId, Long toUserId) {
        DynamicNotification notification = new DynamicNotification();
        notification.setUserId(toUserId);
        notification.setDynamicId(dynamicId);
        notification.setType("SHARE");
        notification.setFromUserId(fromUserId);
        notification.setContent("有人分享了你的动态");
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    /**
     * 发送关注通知
     */
    @Transactional
    public void sendFollowNotification(Long fromUserId, Long toUserId) {
        DynamicNotification notification = new DynamicNotification();
        notification.setUserId(toUserId);
        notification.setType("FOLLOW");
        notification.setFromUserId(fromUserId);
        notification.setContent("有人关注了你");
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    /**
     * 获取用户通知列表
     */
    public List<DynamicNotification> getUserNotifications(Long userId, int page, int size) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, 
            org.springframework.data.domain.PageRequest.of(page, size)).getContent();
    }
    
    /**
     * 获取用户未读通知数量
     */
    public long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }
    
    /**
     * 标记通知为已读
     */
    @Transactional
    public void markNotificationAsRead(Long notificationId, Long userId) {
        DynamicNotification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }
    
    /**
     * 标记所有通知为已读
     */
    @Transactional
    public void markAllNotificationsAsRead(Long userId) {
        List<DynamicNotification> notifications = notificationRepository.findByUserIdAndIsRead(userId, false);
        for (DynamicNotification notification : notifications) {
            notification.setIsRead(true);
        }
        notificationRepository.saveAll(notifications);
    }
    
    /**
     * 删除通知
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        DynamicNotification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null && notification.getUserId().equals(userId)) {
            notificationRepository.delete(notification);
        }
    }
    
    /**
     * 截断内容
     */
    private String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
