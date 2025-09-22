package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.service.CallService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/call")
@CrossOrigin(originPatterns = "*")
public class CallController {
    
    @Autowired
    private CallService callService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 发起通话
     */
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<Object>> initiateCall(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long callerId = jwtUtil.getUserIdFromToken(jwt);
            Long receiverId = Long.valueOf(request.get("receiverId").toString());
            
            System.out.println("=== 发起通话请求 ===");
            System.out.println("发起方ID: " + callerId);
            System.out.println("接收方ID: " + receiverId);
            
            Object result = callService.initiateCall(callerId, receiverId);
            
            if (result != null) {
                System.out.println("通话发起成功");
                return ResponseEntity.ok(ApiResponse.success(result));
            } else {
                System.out.println("通话发起失败");
                return ResponseEntity.badRequest().body(ApiResponse.error("通话发起失败"));
            }
        } catch (Exception e) {
            System.out.println("通话发起异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("发起通话失败: " + e.getMessage()));
        }
    }
    
    /**
     * 接受通话
     */
    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<Object>> acceptCall(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            String callSessionId = request.get("callSessionId").toString();
            
            System.out.println("=== 接受通话请求 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("通话会话ID: " + callSessionId);
            
            Object result = callService.acceptCall(callSessionId, userId);
            
            if (result != null) {
                System.out.println("通话接受成功");
                return ResponseEntity.ok(ApiResponse.success(result));
            } else {
                System.out.println("通话接受失败");
                return ResponseEntity.badRequest().body(ApiResponse.error("接受通话失败"));
            }
        } catch (Exception e) {
            System.out.println("接受通话异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("接受通话失败: " + e.getMessage()));
        }
    }
    
    /**
     * 拒绝通话
     */
    @PostMapping("/reject")
    public ResponseEntity<ApiResponse<Object>> rejectCall(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            String callSessionId = request.get("callSessionId").toString();
            
            System.out.println("=== 拒绝通话请求 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("通话会话ID: " + callSessionId);
            
            Object result = callService.rejectCall(callSessionId, userId);
            
            if (result != null) {
                System.out.println("通话拒绝成功");
                return ResponseEntity.ok(ApiResponse.success(result));
            } else {
                System.out.println("通话拒绝失败");
                return ResponseEntity.badRequest().body(ApiResponse.error("拒绝通话失败"));
            }
        } catch (Exception e) {
            System.out.println("拒绝通话异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("拒绝通话失败: " + e.getMessage()));
        }
    }
    
    /**
     * 结束通话
     */
    @PostMapping("/end")
    public ResponseEntity<ApiResponse<Object>> endCall(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            String callSessionId = request.get("callSessionId").toString();
            String reason = request.getOrDefault("reason", "NORMAL").toString();
            
            System.out.println("=== 结束通话请求 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("通话会话ID: " + callSessionId);
            System.out.println("结束原因: " + reason);
            
            Object result = callService.endCall(callSessionId, userId, reason);
            
            if (result != null) {
                System.out.println("通话结束成功");
                return ResponseEntity.ok(ApiResponse.success(result));
            } else {
                System.out.println("通话结束失败");
                return ResponseEntity.badRequest().body(ApiResponse.error("结束通话失败"));
            }
        } catch (Exception e) {
            System.out.println("结束通话异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("结束通话失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取通话状态
     */
    @GetMapping("/status/{callSessionId}")
    public ResponseEntity<ApiResponse<Object>> getCallStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable String callSessionId) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            System.out.println("=== 获取通话状态 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("通话会话ID: " + callSessionId);
            
            Object result = callService.getCallStatus(callSessionId, userId);
            
            if (result != null) {
                return ResponseEntity.ok(ApiResponse.success(result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("获取通话状态失败"));
            }
        } catch (Exception e) {
            System.out.println("获取通话状态异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("获取通话状态失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取通话历史
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Object>> getCallHistory(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            System.out.println("=== 获取通话历史 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("页码: " + page + ", 大小: " + size);
            
            Object result = callService.getCallHistory(userId, page, size);
            
            if (result != null) {
                return ResponseEntity.ok(ApiResponse.success(result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("获取通话历史失败"));
            }
        } catch (Exception e) {
            System.out.println("获取通话历史异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("获取通话历史失败: " + e.getMessage()));
        }
    }
    
    /**
     * 实时计费 - 每分钟扣费
     */
    @PostMapping("/billing/minute")
    public ResponseEntity<ApiResponse<Object>> processMinuteBilling(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            String callSessionId = request.get("callSessionId").toString();
            
            System.out.println("=== 实时计费请求 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("通话会话ID: " + callSessionId);
            
            Object result = callService.processMinuteBilling(callSessionId, userId);
            
            if (result != null) {
                return ResponseEntity.ok(ApiResponse.success(result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("实时计费失败"));
            }
        } catch (Exception e) {
            System.out.println("实时计费异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("实时计费失败: " + e.getMessage()));
        }
    }
    
    /**
     * 检查余额是否足够继续通话
     */
    @PostMapping("/check-balance")
    public ResponseEntity<ApiResponse<Object>> checkBalanceForCall(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            String callSessionId = request.get("callSessionId").toString();
            
            System.out.println("=== 检查余额请求 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("通话会话ID: " + callSessionId);
            
            Object result = callService.checkBalanceForCall(callSessionId, userId);
            
            if (result != null) {
                return ResponseEntity.ok(ApiResponse.success(result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("检查余额失败"));
            }
        } catch (Exception e) {
            System.out.println("检查余额异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("检查余额失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取通话费率信息
     */
    @GetMapping("/rate-info")
    public ResponseEntity<ApiResponse<Object>> getCallRateInfo(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            System.out.println("=== 获取通话费率信息 ===");
            System.out.println("用户ID: " + userId);
            
            Object result = callService.getCallRateInfo(userId);
            
            if (result != null) {
                return ResponseEntity.ok(ApiResponse.success(result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("获取费率信息失败"));
            }
        } catch (Exception e) {
            System.out.println("获取费率信息异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("获取费率信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 检查来电
     */
    @GetMapping("/incoming")
    public ResponseEntity<ApiResponse<Object>> checkIncomingCalls(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            System.out.println("=== 检查来电 ===");
            System.out.println("用户ID: " + userId);
            
            Object result = callService.checkIncomingCalls(userId);
            
            if (result != null) {
                return ResponseEntity.ok(ApiResponse.success(result));
            } else {
                return ResponseEntity.ok(ApiResponse.success(null)); // 没有来电
            }
        } catch (Exception e) {
            System.out.println("检查来电异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("检查来电失败: " + e.getMessage()));
        }
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("通话服务正常"));
    }
}
