package com.socialmeet.backend.service;

import com.socialmeet.backend.dto.MessageDTO;
import com.socialmeet.backend.dto.SignalingMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 信令服务
 * 负责WebSocket信令消息和聊天消息的发送和管理
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SignalingService {

    private final SimpMessagingTemplate messagingTemplate;
    private final OnlineUserService onlineUserService;

    /**
     * 发送信令消息给指定用户
     */
    public void sendSignalingMessage(Long userId, SignalingMessage message) {
        try {
            String destination = "/queue/signaling/" + userId;
            messagingTemplate.convertAndSend(destination, message);
            log.info("✅ 信令消息已发送 - userId: {}, type: {}, sessionId: {}", 
                    userId, message.getType(), message.getSessionId());
        } catch (Exception e) {
            log.error("❌ 发送信令消息失败 - userId: {}", userId, e);
        }
    }

    /**
     * 发送通话发起信令
     */
    public void sendCallInitiate(Long callerId, Long receiverId, String sessionId, String callType) {
        SignalingMessage message = new SignalingMessage();
        message.setType("CALL_INITIATE");
        message.setSessionId(sessionId);
        message.setCallerId(callerId);
        message.setReceiverId(receiverId);
        message.setCallType(callType);
        message.setStatus("INITIATED");
        message.setMessage("向您发起" + ("VIDEO".equals(callType) ? "视频" : "语音") + "通话");
        
        sendSignalingMessage(receiverId, message);
    }

    /**
     * 发送通话接听信令
     */
    public void sendCallAccept(Long callerId, String sessionId) {
        SignalingMessage message = new SignalingMessage();
        message.setType("CALL_ACCEPT");
        message.setSessionId(sessionId);
        message.setCallerId(callerId);
        message.setStatus("ACCEPTED");
        message.setMessage("对方已接听");
        
        sendSignalingMessage(callerId, message);
    }

    /**
     * 发送通话拒绝信令
     */
    public void sendCallReject(Long callerId, String sessionId) {
        SignalingMessage message = new SignalingMessage();
        message.setType("CALL_REJECT");
        message.setSessionId(sessionId);
        message.setCallerId(callerId);
        message.setStatus("REJECTED");
        message.setMessage("对方已拒绝");
        
        sendSignalingMessage(callerId, message);
    }

    /**
     * 发送通话结束信令
     */
    public void sendCallEnd(Long userId, String sessionId) {
        SignalingMessage message = new SignalingMessage();
        message.setType("CALL_END");
        message.setSessionId(sessionId);
        message.setStatus("ENDED");
        message.setMessage("通话已结束");

        sendSignalingMessage(userId, message);
    }

    /**
     * 通过WebSocket发送聊天消息
     * @return true: WebSocket发送成功, false: 用户不在线
     */
    public boolean sendChatMessage(Long receiverId, MessageDTO messageDTO) {
        // 检查用户是否在线
        if (!onlineUserService.isUserOnline(receiverId)) {
            log.info("用户不在线，无法通过WebSocket发送消息 - receiverId: {}", receiverId);
            return false;
        }

        try {
            // 构建WebSocket消息
            Map<String, Object> wsMessage = new HashMap<>();
            wsMessage.put("type", "CHAT_MESSAGE");
            wsMessage.put("messageId", messageDTO.getId());
            wsMessage.put("senderId", messageDTO.getSenderId());
            wsMessage.put("senderName", messageDTO.getSenderName());
            wsMessage.put("senderAvatar", messageDTO.getSenderAvatar());
            wsMessage.put("receiverId", messageDTO.getReceiverId());
            wsMessage.put("content", messageDTO.getContent());
            wsMessage.put("messageType", messageDTO.getMessageType());
            wsMessage.put("timestamp", messageDTO.getCreatedAt());

            // 发送到用户的消息队列
            String destination = "/queue/messages/" + receiverId;
            messagingTemplate.convertAndSend(destination, wsMessage);

            log.info("✅ WebSocket聊天消息已发送 - receiverId: {}, messageId: {}, sender: {}",
                    receiverId, messageDTO.getId(), messageDTO.getSenderName());
            return true;

        } catch (Exception e) {
            log.error("❌ WebSocket发送聊天消息失败 - receiverId: {}, messageId: {}",
                    receiverId, messageDTO.getId(), e);
            return false;
        }
    }
}
