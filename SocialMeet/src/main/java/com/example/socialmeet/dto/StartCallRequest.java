package com.example.socialmeet.dto;

import com.example.socialmeet.entity.CallRecordEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 开始通话请求DTO
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class StartCallRequest {
    
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;
    
    @NotNull(message = "通话类型不能为空")
    private CallRecordEntity.CallType callType = CallRecordEntity.CallType.VOICE;
    
    private String callerDeviceType;
    private String callerLocation;
}
