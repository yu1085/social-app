package com.socialmeet.backend.config;

import com.socialmeet.backend.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 * WebSocket事件监听器
 * 监听WebSocket连接、断开、订阅事件
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final OnlineUserService onlineUserService;

    /**
     * WebSocket连接建立事件
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("WebSocket连接建立 - sessionId: {}", sessionId);
    }

    /**
     * WebSocket订阅事件
     */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();

        // 从destination中提取userId
        // 格式: /queue/messages/{userId} 或 /queue/signaling/{userId}
        if (destination != null && (destination.startsWith("/queue/messages/") ||
                                   destination.startsWith("/queue/signaling/"))) {
            try {
                String[] parts = destination.split("/");
                Long userId = Long.parseLong(parts[parts.length - 1]);

                // 用户订阅了消息队列，标记为在线
                onlineUserService.userOnline(userId, sessionId);

                log.info("用户订阅消息队列 - userId: {}, sessionId: {}, destination: {}",
                        userId, sessionId, destination);
            } catch (Exception e) {
                log.warn("解析destination失败 - destination: {}", destination);
            }
        }
    }

    /**
     * WebSocket断开连接事件
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        // 用户下线
        onlineUserService.userOffline(sessionId);

        log.info("WebSocket连接断开 - sessionId: {}", sessionId);
    }
}
