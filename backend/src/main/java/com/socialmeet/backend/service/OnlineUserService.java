package com.socialmeet.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在线用户管理服务
 * 跟踪WebSocket连接的在线用户
 */
@Service
@Slf4j
public class OnlineUserService {

    // 在线用户映射: userId -> sessionId
    private final Map<Long, String> onlineUsers = new ConcurrentHashMap<>();

    // WebSocket会话映射: sessionId -> userId
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    /**
     * 用户上线（WebSocket连接建立）
     */
    public void userOnline(Long userId, String sessionId) {
        // 如果用户已经在线，先移除旧会话
        String oldSessionId = onlineUsers.get(userId);
        if (oldSessionId != null) {
            sessions.remove(oldSessionId);
            log.info("用户已在线，移除旧会话 - userId: {}, oldSessionId: {}", userId, oldSessionId);
        }

        // 添加新会话
        onlineUsers.put(userId, sessionId);
        sessions.put(sessionId, userId);

        log.info("✅ 用户上线 - userId: {}, sessionId: {}, 在线用户数: {}",
                userId, sessionId, onlineUsers.size());
    }

    /**
     * 用户下线（WebSocket连接断开）
     */
    public void userOffline(String sessionId) {
        Long userId = sessions.remove(sessionId);
        if (userId != null) {
            onlineUsers.remove(userId);
            log.info("✅ 用户下线 - userId: {}, sessionId: {}, 在线用户数: {}",
                    userId, sessionId, onlineUsers.size());
        }
    }

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        return onlineUsers.containsKey(userId);
    }

    /**
     * 获取用户的WebSocket会话ID
     */
    public String getUserSessionId(Long userId) {
        return onlineUsers.get(userId);
    }

    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return onlineUsers.size();
    }

    /**
     * 获取所有在线用户ID
     */
    public Map<Long, String> getAllOnlineUsers() {
        return new ConcurrentHashMap<>(onlineUsers);
    }
}
