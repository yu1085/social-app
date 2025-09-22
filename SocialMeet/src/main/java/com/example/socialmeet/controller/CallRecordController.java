package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.CallRecordDTO;
import com.example.socialmeet.dto.StartCallRequest;
import com.example.socialmeet.dto.EndCallRequest;
import com.example.socialmeet.service.CallRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通话记录控制器
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/calls")
@CrossOrigin(originPatterns = "*")
public class CallRecordController {
    
    @Autowired
    private CallRecordService callRecordService;
    
    /**
     * 开始通话
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<CallRecordDTO>> startCall(
            @Valid @RequestBody StartCallRequest request,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<CallRecordDTO> response = callRecordService.startCall(request, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 接听通话
     */
    @PostMapping("/{callRecordId}/answer")
    public ResponseEntity<ApiResponse<CallRecordDTO>> answerCall(
            @PathVariable Long callRecordId,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<CallRecordDTO> response = callRecordService.answerCall(callRecordId, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 拒绝通话
     */
    @PostMapping("/{callRecordId}/reject")
    public ResponseEntity<ApiResponse<CallRecordDTO>> rejectCall(
            @PathVariable Long callRecordId,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<CallRecordDTO> response = callRecordService.rejectCall(callRecordId, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 结束通话
     */
    @PostMapping("/end")
    public ResponseEntity<ApiResponse<CallRecordDTO>> endCall(
            @Valid @RequestBody EndCallRequest request,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<CallRecordDTO> response = callRecordService.endCall(request, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 标记为未接听
     */
    @PostMapping("/{callRecordId}/missed")
    public ResponseEntity<ApiResponse<CallRecordDTO>> markAsMissed(
            @PathVariable Long callRecordId,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<CallRecordDTO> response = callRecordService.markAsMissed(callRecordId, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取通话记录列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CallRecordDTO>>> getCallRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<Page<CallRecordDTO>> response = callRecordService.getCallRecords(page, size, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取两个用户之间的通话记录
     */
    @GetMapping("/user/{otherUserId}")
    public ResponseEntity<ApiResponse<Page<CallRecordDTO>>> getCallRecordsBetweenUsers(
            @PathVariable Long otherUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<Page<CallRecordDTO>> response = callRecordService.getCallRecordsBetweenUsers(otherUserId, page, size, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取未接来电
     */
    @GetMapping("/missed")
    public ResponseEntity<ApiResponse<List<CallRecordDTO>>> getMissedCalls(
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<List<CallRecordDTO>> response = callRecordService.getMissedCalls(token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取通话统计
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Object>> getCallStatistics(
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<Object> response = callRecordService.getCallStatistics(token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 删除通话记录
     */
    @DeleteMapping("/{callRecordId}")
    public ResponseEntity<ApiResponse<String>> deleteCallRecord(
            @PathVariable Long callRecordId,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<String> response = callRecordService.deleteCallRecord(callRecordId, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
