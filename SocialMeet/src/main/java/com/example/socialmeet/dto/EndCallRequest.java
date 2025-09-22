package com.example.socialmeet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 结束通话请求DTO
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class EndCallRequest {
    
    @NotNull(message = "通话记录ID不能为空")
    private Long callRecordId;
    
    private Double qualityScore;
    private String networkQuality;
    private String callNotes;
    private Boolean isRecorded = false;
    private String recordingUrl;
    private Integer recordingDuration;
}
