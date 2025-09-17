package com.example.socialmeet.service;

import com.example.socialmeet.entity.CallSession;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.entity.Wallet;
import com.example.socialmeet.repository.CallSessionRepository;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class CallService {
    
    @Autowired
    private CallSessionRepository callSessionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private WalletService walletService;
    
    // 内存中存储活跃的通话会话
    private final Map<String, CallSession> activeCalls = new ConcurrentHashMap<>();
    
    // 通话费率（元/分钟）
    private static final double CALL_RATE = 300.0;
    
    /**
     * 发起通话
     */
    public Object initiateCall(Long callerId, Long receiverId) {
        try {
            // 1. 检查接收方是否存在
            Optional<User> receiverOpt = userRepository.findById(receiverId);
            if (!receiverOpt.isPresent()) {
                return createErrorResponse("用户不存在", "USER_NOT_FOUND");
            }
            
            User receiver = receiverOpt.get();
            
            // 2. 检查接收方是否在线
            if (!receiver.getIsOnline()) {
                return createErrorResponse("用户离线，无法通话", "USER_OFFLINE");
            }
            
            // 3. 检查发起方余额
            Wallet callerWallet = walletRepository.findByUserId(callerId).orElse(null);
            if (callerWallet == null) {
                return createErrorResponse("发起方钱包不存在", "CALLER_WALLET_NOT_FOUND");
            }
            
            if (callerWallet.getBalance().doubleValue() < CALL_RATE / 60.0) {
                return createErrorResponse("余额不足，无法发起通话", "INSUFFICIENT_BALANCE");
            }
            
            // 4. 创建通话会话
            String callSessionId = generateCallSessionId();
            CallSession callSession = new CallSession();
            callSession.setId(callSessionId);
            callSession.setCallerId(callerId);
            callSession.setReceiverId(receiverId);
            callSession.setStatus("INITIATED");
            callSession.setRate(CALL_RATE);
            callSession.setCallerBalance(callerWallet.getBalance().doubleValue());
            callSession.setReceiverBalance(receiver.getIsOnline() ? 1000.0 : 0.0);
            callSession.setIsOnline(receiver.getIsOnline());
            callSession.setCreatedAt(LocalDateTime.now());
            callSession.setUpdatedAt(LocalDateTime.now());
            
            // 5. 保存到数据库
            callSession = callSessionRepository.save(callSession);
            
            // 6. 存储到内存
            activeCalls.put(callSessionId, callSession);
            
            System.out.println("通话会话创建成功: " + callSessionId);
            
            return createCallSessionResponse(callSession);
            
        } catch (Exception e) {
            System.out.println("发起通话异常: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("发起通话失败: " + e.getMessage(), "INITIATE_CALL_FAILED");
        }
    }
    
    /**
     * 接受通话
     */
    public Object acceptCall(String callSessionId, Long userId) {
        try {
            // 1. 查找通话会话
            CallSession callSession = activeCalls.get(callSessionId);
            if (callSession == null) {
                callSession = callSessionRepository.findById(callSessionId).orElse(null);
            }
            
            if (callSession == null) {
                return createErrorResponse("通话会话不存在", "CALL_SESSION_NOT_FOUND");
            }
            
            // 2. 检查用户权限
            if (!callSession.getReceiverId().equals(userId)) {
                return createErrorResponse("无权限接受此通话", "UNAUTHORIZED");
            }
            
            // 3. 检查通话状态
            if (!"INITIATED".equals(callSession.getStatus()) && !"RINGING".equals(callSession.getStatus())) {
                return createErrorResponse("通话状态不允许接受", "INVALID_CALL_STATUS");
            }
            
            // 4. 更新通话状态
            callSession.setStatus("ACTIVE");
            callSession.setStartTime(LocalDateTime.now());
            callSession.setUpdatedAt(LocalDateTime.now());
            
            // 5. 保存到数据库
            callSession = callSessionRepository.save(callSession);
            activeCalls.put(callSessionId, callSession);
            
            System.out.println("通话接受成功: " + callSessionId);
            
            return createCallSessionResponse(callSession);
            
        } catch (Exception e) {
            System.out.println("接受通话异常: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("接受通话失败: " + e.getMessage(), "ACCEPT_CALL_FAILED");
        }
    }
    
    /**
     * 拒绝通话
     */
    public Object rejectCall(String callSessionId, Long userId) {
        try {
            // 1. 查找通话会话
            CallSession callSession = activeCalls.get(callSessionId);
            if (callSession == null) {
                callSession = callSessionRepository.findById(callSessionId).orElse(null);
            }
            
            if (callSession == null) {
                return createErrorResponse("通话会话不存在", "CALL_SESSION_NOT_FOUND");
            }
            
            // 2. 检查用户权限
            if (!callSession.getReceiverId().equals(userId)) {
                return createErrorResponse("无权限拒绝此通话", "UNAUTHORIZED");
            }
            
            // 3. 更新通话状态
            callSession.setStatus("REJECTED");
            callSession.setEndTime(LocalDateTime.now());
            callSession.setUpdatedAt(LocalDateTime.now());
            
            // 4. 保存到数据库
            callSession = callSessionRepository.save(callSession);
            activeCalls.remove(callSessionId);
            
            System.out.println("通话拒绝成功: " + callSessionId);
            
            return createCallSessionResponse(callSession);
            
        } catch (Exception e) {
            System.out.println("拒绝通话异常: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("拒绝通话失败: " + e.getMessage(), "REJECT_CALL_FAILED");
        }
    }
    
    /**
     * 结束通话
     */
    public Object endCall(String callSessionId, Long userId, String reason) {
        try {
            // 1. 查找通话会话
            CallSession callSession = activeCalls.get(callSessionId);
            if (callSession == null) {
                callSession = callSessionRepository.findById(callSessionId).orElse(null);
            }
            
            if (callSession == null) {
                return createErrorResponse("通话会话不存在", "CALL_SESSION_NOT_FOUND");
            }
            
            // 2. 检查用户权限
            if (!callSession.getCallerId().equals(userId) && !callSession.getReceiverId().equals(userId)) {
                return createErrorResponse("无权限结束此通话", "UNAUTHORIZED");
            }
            
            // 3. 计算通话费用
            double totalCost = 0.0;
            if ("ACTIVE".equals(callSession.getStatus()) && callSession.getStartTime() != null) {
                long durationSeconds = java.time.Duration.between(callSession.getStartTime(), LocalDateTime.now()).getSeconds();
                callSession.setDuration(durationSeconds);
                totalCost = (durationSeconds / 60.0) * callSession.getRate();
                callSession.setTotalCost(totalCost);
            }
            
            // 4. 扣除费用
            if (totalCost > 0) {
                try {
                    walletService.deductBalance(callSession.getCallerId(), totalCost, "视频通话费用");
                    System.out.println("扣除通话费用: " + totalCost + "元");
                } catch (Exception e) {
                    System.out.println("扣除费用失败: " + e.getMessage());
                }
            }
            
            // 5. 更新通话状态
            callSession.setStatus("ENDED");
            callSession.setEndTime(LocalDateTime.now());
            callSession.setUpdatedAt(LocalDateTime.now());
            
            // 6. 保存到数据库
            callSession = callSessionRepository.save(callSession);
            activeCalls.remove(callSessionId);
            
            System.out.println("通话结束成功: " + callSessionId + ", 费用: " + totalCost + "元");
            
            return createCallSessionResponse(callSession);
            
        } catch (Exception e) {
            System.out.println("结束通话异常: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("结束通话失败: " + e.getMessage(), "END_CALL_FAILED");
        }
    }
    
    /**
     * 获取通话状态
     */
    public Object getCallStatus(String callSessionId, Long userId) {
        try {
            CallSession callSession = activeCalls.get(callSessionId);
            if (callSession == null) {
                callSession = callSessionRepository.findById(callSessionId).orElse(null);
            }
            
            if (callSession == null) {
                return createErrorResponse("通话会话不存在", "CALL_SESSION_NOT_FOUND");
            }
            
            // 检查用户权限
            if (!callSession.getCallerId().equals(userId) && !callSession.getReceiverId().equals(userId)) {
                return createErrorResponse("无权限查看此通话", "UNAUTHORIZED");
            }
            
            return createCallSessionResponse(callSession);
            
        } catch (Exception e) {
            System.out.println("获取通话状态异常: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("获取通话状态失败: " + e.getMessage(), "GET_CALL_STATUS_FAILED");
        }
    }
    
    /**
     * 获取通话历史
     */
    public Object getCallHistory(Long userId, int page, int size) {
        try {
            // 这里应该实现分页查询通话历史
            // 暂时返回空列表
            Map<String, Object> result = new HashMap<>();
            result.put("calls", new ArrayList<>());
            result.put("total", 0);
            result.put("page", page);
            result.put("size", size);
            
            return result;
            
        } catch (Exception e) {
            System.out.println("获取通话历史异常: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("获取通话历史失败: " + e.getMessage(), "GET_CALL_HISTORY_FAILED");
        }
    }
    
    /**
     * 生成通话会话ID
     */
    private String generateCallSessionId() {
        return "call_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    /**
     * 创建通话会话响应
     */
    private Map<String, Object> createCallSessionResponse(CallSession callSession) {
        Map<String, Object> response = new HashMap<>();
        response.put("callSessionId", callSession.getId());
        response.put("callerId", callSession.getCallerId());
        response.put("receiverId", callSession.getReceiverId());
        response.put("status", callSession.getStatus());
        response.put("rate", callSession.getRate());
        response.put("callerBalance", callSession.getCallerBalance());
        response.put("receiverBalance", callSession.getReceiverBalance());
        response.put("isOnline", callSession.getIsOnline());
        response.put("duration", callSession.getDuration());
        response.put("totalCost", callSession.getTotalCost());
        response.put("createdAt", callSession.getCreatedAt());
        response.put("updatedAt", callSession.getUpdatedAt());
        return response;
    }
    
    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message, String errorCode) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("errorCode", errorCode);
        return response;
    }
}
