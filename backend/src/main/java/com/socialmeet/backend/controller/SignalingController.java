package com.socialmeet.backend.controller;

import com.socialmeet.backend.dto.SignalingMessage;
import com.socialmeet.backend.security.JwtUtil;
import com.socialmeet.backend.service.SignalingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * WebSocket信令控制器
 * 处理来自客户端的信令消息
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class SignalingController {

    private final SignalingService signalingService;
    private final JwtUtil jwtUtil;

    /**
     * 处理信令消息
     */
    @MessageMapping("/signaling")
    public void handleSignalingMessage(@Payload SignalingMessage message, 
                                     SimpMessageHeaderAccessor headerAccessor) {
        try {
            // 从WebSocket会话中获取用户ID
            String userIdStr = headerAccessor.getSessionAttributes().get("userId").toString();
            Long userId = Long.valueOf(userIdStr);
            
            log.info("收到信令消息 - userId: {}, type: {}, sessionId: {}", 
                    userId, message.getType(), message.getSessionId());
            
            // 根据消息类型处理
            switch (message.getType()) {
                case "CALL_ACCEPT":
                    signalingService.sendCallAccept(message.getCallerId(), message.getSessionId());
                    break;
                case "CALL_REJECT":
                    signalingService.sendCallReject(message.getCallerId(), message.getSessionId());
                    break;
                case "CALL_END":
                    signalingService.sendCallEnd(message.getReceiverId(), message.getSessionId());
                    break;
                default:
                    log.warn("未知的信令消息类型: {}", message.getType());
            }
            
        } catch (Exception e) {
            log.error("处理信令消息失败", e);
        }
    }
}
