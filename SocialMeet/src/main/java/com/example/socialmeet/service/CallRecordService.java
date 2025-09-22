package com.example.socialmeet.service;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.CallRecordDTO;
import com.example.socialmeet.dto.StartCallRequest;
import com.example.socialmeet.dto.EndCallRequest;
import com.example.socialmeet.entity.CallRecordEntity;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.CallRecordRepository;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 通话记录服务层
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
public class CallRecordService {
    
    @Autowired
    private CallRecordRepository callRecordRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 开始通话
     */
    @Transactional
    public ApiResponse<CallRecordDTO> startCall(StartCallRequest request, String token) {
        try {
            Long callerId = jwtUtil.getUserIdFromToken(token);
            if (callerId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            // 验证接收者是否存在
            Optional<User> receiverOpt = userRepository.findById(request.getReceiverId());
            if (!receiverOpt.isPresent()) {
                return ApiResponse.error("接收者不存在");
            }
            
            User receiver = receiverOpt.get();
            
            // 检查接收者是否在线
            if (!receiver.getIsOnline()) {
                return ApiResponse.error("对方不在线，无法发起通话");
            }
            
            // 检查通话权限
            if (request.getCallType() == CallRecordEntity.CallType.VIDEO && !receiver.getVideoCallEnabled()) {
                return ApiResponse.error("对方未开启视频通话功能");
            }
            if (request.getCallType() == CallRecordEntity.CallType.VOICE && !receiver.getVoiceCallEnabled()) {
                return ApiResponse.error("对方未开启语音通话功能");
            }
            
            // 创建通话记录
            CallRecordEntity callRecord = new CallRecordEntity();
            callRecord.setCallerId(callerId);
            callRecord.setReceiverId(request.getReceiverId());
            callRecord.setCallType(request.getCallType());
            callRecord.setStartTime(LocalDateTime.now());
            callRecord.setCallStatus(CallRecordEntity.CallStatus.INITIATED);
            callRecord.setCallPrice(receiver.getCallPrice());
            callRecord.setCallerDeviceType(request.getCallerDeviceType());
            callRecord.setCallerLocation(request.getCallerLocation());
            callRecord.setReceiverLocation(receiver.getLocation());
            
            callRecord = callRecordRepository.save(callRecord);
            
            return ApiResponse.success(convertToDTO(callRecord));
            
        } catch (Exception e) {
            return ApiResponse.error("发起通话失败: " + e.getMessage());
        }
    }
    
    /**
     * 接听通话
     */
    @Transactional
    public ApiResponse<CallRecordDTO> answerCall(Long callRecordId, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Optional<CallRecordEntity> callRecordOpt = callRecordRepository.findById(callRecordId);
            if (!callRecordOpt.isPresent()) {
                return ApiResponse.error("通话记录不存在");
            }
            
            CallRecordEntity callRecord = callRecordOpt.get();
            if (!callRecord.getReceiverId().equals(userId)) {
                return ApiResponse.error("无权限接听此通话");
            }
            
            if (callRecord.getCallStatus() != CallRecordEntity.CallStatus.RINGING) {
                return ApiResponse.error("通话状态不正确");
            }
            
            callRecord.markAsAnswered();
            callRecord = callRecordRepository.save(callRecord);
            
            return ApiResponse.success(convertToDTO(callRecord));
            
        } catch (Exception e) {
            return ApiResponse.error("接听通话失败: " + e.getMessage());
        }
    }
    
    /**
     * 拒绝通话
     */
    @Transactional
    public ApiResponse<CallRecordDTO> rejectCall(Long callRecordId, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Optional<CallRecordEntity> callRecordOpt = callRecordRepository.findById(callRecordId);
            if (!callRecordOpt.isPresent()) {
                return ApiResponse.error("通话记录不存在");
            }
            
            CallRecordEntity callRecord = callRecordOpt.get();
            if (!callRecord.getReceiverId().equals(userId)) {
                return ApiResponse.error("无权限拒绝此通话");
            }
            
            callRecord.markAsRejected();
            callRecord = callRecordRepository.save(callRecord);
            
            return ApiResponse.success(convertToDTO(callRecord));
            
        } catch (Exception e) {
            return ApiResponse.error("拒绝通话失败: " + e.getMessage());
        }
    }
    
    /**
     * 结束通话
     */
    @Transactional
    public ApiResponse<CallRecordDTO> endCall(EndCallRequest request, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Optional<CallRecordEntity> callRecordOpt = callRecordRepository.findById(request.getCallRecordId());
            if (!callRecordOpt.isPresent()) {
                return ApiResponse.error("通话记录不存在");
            }
            
            CallRecordEntity callRecord = callRecordOpt.get();
            if (!callRecord.getCallerId().equals(userId) && !callRecord.getReceiverId().equals(userId)) {
                return ApiResponse.error("无权限结束此通话");
            }
            
            if (callRecord.getCallStatus() != CallRecordEntity.CallStatus.ANSWERED) {
                return ApiResponse.error("通话状态不正确");
            }
            
            callRecord.endCall();
            callRecord.setQualityScore(request.getQualityScore());
            callRecord.setNetworkQuality(request.getNetworkQuality());
            callRecord.setCallNotes(request.getCallNotes());
            callRecord.setIsRecorded(request.getIsRecorded());
            callRecord.setRecordingUrl(request.getRecordingUrl());
            callRecord.setRecordingDuration(request.getRecordingDuration());
            
            callRecord = callRecordRepository.save(callRecord);
            
            return ApiResponse.success(convertToDTO(callRecord));
            
        } catch (Exception e) {
            return ApiResponse.error("结束通话失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记为未接听
     */
    @Transactional
    public ApiResponse<CallRecordDTO> markAsMissed(Long callRecordId, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Optional<CallRecordEntity> callRecordOpt = callRecordRepository.findById(callRecordId);
            if (!callRecordOpt.isPresent()) {
                return ApiResponse.error("通话记录不存在");
            }
            
            CallRecordEntity callRecord = callRecordOpt.get();
            if (!callRecord.getReceiverId().equals(userId)) {
                return ApiResponse.error("无权限操作此通话");
            }
            
            callRecord.markAsMissed();
            callRecord = callRecordRepository.save(callRecord);
            
            return ApiResponse.success(convertToDTO(callRecord));
            
        } catch (Exception e) {
            return ApiResponse.error("标记未接听失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取通话记录列表
     */
    public ApiResponse<Page<CallRecordDTO>> getCallRecords(int page, int size, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<CallRecordEntity> callRecords = callRecordRepository.findByUserId(userId, pageable);
            
            Page<CallRecordDTO> callRecordDTOs = callRecords.map(this::convertToDTO);
            
            return ApiResponse.success(callRecordDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取通话记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取两个用户之间的通话记录
     */
    public ApiResponse<Page<CallRecordDTO>> getCallRecordsBetweenUsers(Long otherUserId, int page, int size, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<CallRecordEntity> callRecords = callRecordRepository.findCallRecordsBetweenUsers(userId, otherUserId, pageable);
            
            Page<CallRecordDTO> callRecordDTOs = callRecords.map(this::convertToDTO);
            
            return ApiResponse.success(callRecordDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取通话记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取未接来电
     */
    public ApiResponse<List<CallRecordDTO>> getMissedCalls(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            List<CallRecordEntity> missedCalls = callRecordRepository.findMissedCallsByUserId(userId);
            List<CallRecordDTO> missedCallDTOs = missedCalls.stream()
                    .map(this::convertToDTO)
                    .collect(java.util.stream.Collectors.toList());
            
            return ApiResponse.success(missedCallDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取未接来电失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取通话统计
     */
    public ApiResponse<Object> getCallStatistics(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Object[] statistics = callRecordRepository.getCallStatistics(userId);
            
            return ApiResponse.success(statistics);
            
        } catch (Exception e) {
            return ApiResponse.error("获取通话统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除通话记录
     */
    @Transactional
    public ApiResponse<String> deleteCallRecord(Long callRecordId, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Optional<CallRecordEntity> callRecordOpt = callRecordRepository.findById(callRecordId);
            if (!callRecordOpt.isPresent()) {
                return ApiResponse.error("通话记录不存在");
            }
            
            CallRecordEntity callRecord = callRecordOpt.get();
            if (!callRecord.getCallerId().equals(userId) && !callRecord.getReceiverId().equals(userId)) {
                return ApiResponse.error("无权限删除此通话记录");
            }
            
            // 软删除
            if (callRecord.getCallerId().equals(userId)) {
                callRecord.setIsDeletedCaller(true);
            } else {
                callRecord.setIsDeletedReceiver(true);
            }
            
            callRecordRepository.save(callRecord);
            
            return ApiResponse.success("通话记录已删除");
            
        } catch (Exception e) {
            return ApiResponse.error("删除通话记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 转换为DTO
     */
    private CallRecordDTO convertToDTO(CallRecordEntity callRecord) {
        CallRecordDTO dto = new CallRecordDTO();
        dto.setId(callRecord.getId());
        dto.setCallerId(callRecord.getCallerId());
        dto.setReceiverId(callRecord.getReceiverId());
        dto.setCallType(callRecord.getCallType());
        dto.setStartTime(callRecord.getStartTime());
        dto.setEndTime(callRecord.getEndTime());
        dto.setDuration(callRecord.getDuration());
        dto.setCallStatus(callRecord.getCallStatus());
        dto.setIsMissed(callRecord.getIsMissed());
        dto.setIsAnswered(callRecord.getIsAnswered());
        dto.setIsRejected(callRecord.getIsRejected());
        dto.setCallPrice(callRecord.getCallPrice());
        dto.setTotalCost(callRecord.getTotalCost());
        dto.setQualityScore(callRecord.getQualityScore());
        dto.setNetworkQuality(callRecord.getNetworkQuality());
        dto.setCallerDeviceType(callRecord.getCallerDeviceType());
        dto.setReceiverDeviceType(callRecord.getReceiverDeviceType());
        dto.setCallerLocation(callRecord.getCallerLocation());
        dto.setReceiverLocation(callRecord.getReceiverLocation());
        dto.setCallNotes(callRecord.getCallNotes());
        dto.setIsRecorded(callRecord.getIsRecorded());
        dto.setRecordingUrl(callRecord.getRecordingUrl());
        dto.setRecordingDuration(callRecord.getRecordingDuration());
        dto.setIsDeletedCaller(callRecord.getIsDeletedCaller());
        dto.setIsDeletedReceiver(callRecord.getIsDeletedReceiver());
        dto.setCreatedAt(callRecord.getCreatedAt());
        
        return dto;
    }
}
