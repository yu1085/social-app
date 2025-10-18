package com.socialmeet.backend.dto;

import lombok.Data;

/**
 * 信令消息DTO
 * 用于WebSocket信令传输
 */
@Data
public class SignalingMessage {
    private String type; // CALL_INITIATE, CALL_ACCEPT, CALL_REJECT, CALL_END
    private String sessionId;
    private Long callerId;
    private Long receiverId;
    private String callType;
    private String status;
    private String message;
    private Long timestamp;
    
    public SignalingMessage() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public SignalingMessage(String type, String sessionId) {
        this();
        this.type = type;
        this.sessionId = sessionId;
    }
}
