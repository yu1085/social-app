package com.socialmeet.backend.controller;

import com.socialmeet.backend.dto.ApiResponse;
import com.socialmeet.backend.entity.CallSession;
import com.socialmeet.backend.security.JwtUtil;
import com.socialmeet.backend.service.CallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 通话控制器
 * 处理视频/语音通话相关的请求
 */
@RestController
@RequestMapping("/call")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CallController {

    private final CallService callService;
    private final JwtUtil jwtUtil;

    /**
     * 获取用户通话价格信息
     */
    @GetMapping("/rate-info")
    public ApiResponse<Map<String, Object>> getUserCallPrices(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            Map<String, Object> prices = callService.getUserCallPrices(userId);

            return ApiResponse.success(prices);

        } catch (Exception e) {
            log.error("获取通话价格失败", e);
            return ApiResponse.error("获取通话价格失败: " + e.getMessage());
        }
    }

    /**
     * 发起通话
     */
    @PostMapping("/initiate")
    public ApiResponse<Map<String, Object>> initiateCall(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> request) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long callerId = jwtUtil.getUserIdFromToken(token);
            Long receiverId = Long.valueOf(request.get("receiverId").toString());
            String callType = request.get("callType").toString();

            log.info("发起通话请求 - callerId: {}, receiverId: {}, callType: {}", callerId, receiverId, callType);

            CallSession callSession = callService.initiateCall(callerId, receiverId, callType);

            // 构建响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("callSessionId", callSession.getCallSessionId());
            data.put("callerId", callSession.getCallerId());
            data.put("receiverId", callSession.getReceiverId());
            data.put("callType", callSession.getCallType().toString());
            data.put("status", callSession.getStatus().toString());

            return ApiResponse.success("通话发起成功", data);

        } catch (Exception e) {
            log.error("发起通话失败", e);
            return ApiResponse.error("发起通话失败: " + e.getMessage());
        }
    }

    /**
     * 接受通话
     */
    @PostMapping("/accept")
    public ApiResponse<Map<String, Object>> acceptCall(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            String callSessionId = request.get("callSessionId");

            CallSession callSession = callService.acceptCall(callSessionId, userId);

            Map<String, Object> data = new HashMap<>();
            data.put("callSessionId", callSession.getCallSessionId());
            data.put("status", callSession.getStatus().toString());

            return ApiResponse.success("通话已接受", data);

        } catch (Exception e) {
            log.error("接受通话失败", e);
            return ApiResponse.error("接受通话失败: " + e.getMessage());
        }
    }

    /**
     * 拒绝通话
     */
    @PostMapping("/reject")
    public ApiResponse<Map<String, Object>> rejectCall(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            String callSessionId = request.get("callSessionId");

            CallSession callSession = callService.rejectCall(callSessionId, userId);

            Map<String, Object> data = new HashMap<>();
            data.put("callSessionId", callSession.getCallSessionId());
            data.put("status", callSession.getStatus().toString());

            return ApiResponse.success("通话已拒绝", data);

        } catch (Exception e) {
            log.error("拒绝通话失败", e);
            return ApiResponse.error("拒绝通话失败: " + e.getMessage());
        }
    }

    /**
     * 结束通话
     */
    @PostMapping("/end")
    public ApiResponse<Map<String, Object>> endCall(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            String callSessionId = request.get("callSessionId");

            CallSession callSession = callService.endCall(callSessionId, userId);

            Map<String, Object> data = new HashMap<>();
            data.put("callSessionId", callSession.getCallSessionId());
            data.put("status", callSession.getStatus().toString());
            data.put("durationSeconds", callSession.getDurationSeconds());
            data.put("totalCost", callSession.getTotalCost());

            return ApiResponse.success("通话已结束", data);

        } catch (Exception e) {
            log.error("结束通话失败", e);
            return ApiResponse.error("结束通话失败: " + e.getMessage());
        }
    }
}
