package com.example.socialmeet.dto;

import com.example.socialmeet.entity.CallRecordEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通话记录DTO
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class CallRecordDTO {
    
    private Long id;
    private Long callerId;
    private Long receiverId;
    private CallRecordEntity.CallType callType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private CallRecordEntity.CallStatus callStatus;
    private Boolean isMissed;
    private Boolean isAnswered;
    private Boolean isRejected;
    private Integer callPrice;
    private Integer totalCost;
    private Double qualityScore;
    private String networkQuality;
    private String callerDeviceType;
    private String receiverDeviceType;
    private String callerLocation;
    private String receiverLocation;
    private String callNotes;
    private Boolean isRecorded;
    private String recordingUrl;
    private Integer recordingDuration;
    private Boolean isDeletedCaller;
    private Boolean isDeletedReceiver;
    private LocalDateTime createdAt;
    
    // 扩展字段
    private String callerNickname;
    private String callerAvatar;
    private String receiverNickname;
    private String receiverAvatar;
    private String durationText;
    private String costText;
}
