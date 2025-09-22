package com.example.socialmeet.websocket;

import com.example.socialmeet.dto.MessageDTO;
import com.example.socialmeet.dto.CallRecordDTO;
import com.example.socialmeet.entity.CallRecordEntity;
import com.example.socialmeet.service.MessageService;
import com.example.socialmeet.service.CallRecordService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * WebSocket控制器
 * 处理实时通信消息
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Controller
public class WebSocketController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private CallRecordService callRecordService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 处理消息发送
     */
    @MessageMapping("/message.send")
    public void sendMessage(@Payload Map<String, Object> payload, Principal principal) {
        try {
            // 解析消息数据
            Long senderId = Long.valueOf(payload.get("senderId").toString());
            Long receiverId = Long.valueOf(payload.get("receiverId").toString());
            String content = payload.get("content").toString();
            String messageType = payload.get("messageType").toString();
            
            // 创建消息DTO
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setSenderId(senderId);
            messageDTO.setReceiverId(receiverId);
            messageDTO.setContent(content);
            // messageDTO.setMessageType(messageType); // 需要根据实际MessageDTO结构调整
            // messageDTO.setSendTime(System.currentTimeMillis()); // 需要根据实际MessageDTO结构调整
            
            // 保存消息到数据库
            // messageService.sendMessage(messageDTO);
            
            // 发送给接收者
            messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/messages",
                messageDTO
            );
            
            // 发送给发送者确认
            messagingTemplate.convertAndSendToUser(
                senderId.toString(),
                "/queue/messages",
                messageDTO
            );
            
        } catch (Exception e) {
            // 发送错误消息给发送者
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                Map.of("error", "消息发送失败: " + e.getMessage())
            );
        }
    }
    
    /**
     * 处理通话邀请
     */
    @MessageMapping("/call.invite")
    public void sendCallInvite(@Payload Map<String, Object> payload, Principal principal) {
        try {
            // 解析通话数据
            Long callerId = Long.valueOf(payload.get("callerId").toString());
            Long receiverId = Long.valueOf(payload.get("receiverId").toString());
            String callType = payload.get("callType").toString();
            String sessionId = payload.get("sessionId").toString();
            
            // 创建通话记录DTO
            CallRecordDTO callRecordDTO = new CallRecordDTO();
            callRecordDTO.setCallerId(callerId);
            callRecordDTO.setReceiverId(receiverId);
            // 转换callType字符串为枚举
            try {
                callRecordDTO.setCallType(CallRecordEntity.CallType.valueOf(callType.toUpperCase()));
            } catch (IllegalArgumentException e) {
                callRecordDTO.setCallType(CallRecordEntity.CallType.VOICE);
            }
            callRecordDTO.setStartTime(LocalDateTime.now());
            callRecordDTO.setCallStatus(CallRecordEntity.CallStatus.INITIATED);
            
            // 保存通话记录到数据库
            // callRecordService.startCall(callRecordDTO);
            
            // 发送给接收者
            messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/calls",
                callRecordDTO
            );
            
        } catch (Exception e) {
            // 发送错误消息给发送者
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                Map.of("error", "通话邀请发送失败: " + e.getMessage())
            );
        }
    }
    
    /**
     * 处理在线状态更新
     */
    @MessageMapping("/status.update")
    public void updateStatus(@Payload Map<String, Object> payload, Principal principal) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Boolean isOnline = Boolean.valueOf(payload.get("isOnline").toString());
            String status = payload.get("status").toString();
            
            // 更新用户在线状态
            // userService.updateOnlineStatus(userId, isOnline, status);
            
            // 广播状态更新
            messagingTemplate.convertAndSend(
                "/topic/status",
                Map.of(
                    "userId", userId,
                    "isOnline", isOnline,
                    "status", status,
                    "timestamp", System.currentTimeMillis()
                )
            );
            
        } catch (Exception e) {
            // 发送错误消息
            messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                Map.of("error", "状态更新失败: " + e.getMessage())
            );
        }
    }
    
    /**
     * 处理心跳
     */
    @MessageMapping("/ping")
    public void handlePing(@Payload Map<String, Object> payload, Principal principal) {
        // 发送心跳响应
        messagingTemplate.convertAndSendToUser(
            principal.getName(),
            "/queue/pong",
            Map.of("timestamp", System.currentTimeMillis())
        );
    }
}
