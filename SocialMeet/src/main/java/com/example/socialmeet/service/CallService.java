package com.example.socialmeet.service;

import com.example.socialmeet.entity.CallSession;
import com.example.socialmeet.entity.CallCharge;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.entity.Wallet;
import com.example.socialmeet.repository.CallSessionRepository;
import com.example.socialmeet.repository.CallChargeRepository;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class CallService {
    
    @Autowired
    private CallSessionRepository callSessionRepository;
    
    @Autowired
    private CallChargeRepository callChargeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private JPushService jpushService;
    
    // 内存中存储活跃的通话会话
    private final Map<String, CallSession> activeCalls = new ConcurrentHashMap<>();
    
    // 通话费率（元/分钟）
    private static final double CALL_RATE = 300.0;
    
    // 实时计费定时器
    private final Map<String, java.util.Timer> billingTimers = new ConcurrentHashMap<>();
    
    // 通话超时定时器
    private final Map<String, java.util.Timer> timeoutTimers = new ConcurrentHashMap<>();
    
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
            
            // 3. 检查发起方余额并计算最大可通话时间
            Wallet callerWallet = walletRepository.findByUserId(callerId).orElse(null);
            if (callerWallet == null) {
                return createErrorResponse("发起方钱包不存在", "CALLER_WALLET_NOT_FOUND");
            }
            
            double balance = callerWallet.getBalance().doubleValue();
            double ratePerMinute = CALL_RATE; // 每分钟费用
            int maxDurationSeconds = (int) (balance / ratePerMinute * 60); // 最大可通话时间（秒）
            
            if (maxDurationSeconds < 60) {
                // 计算需要充值的金额
                double requiredAmount = ratePerMinute - balance;
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("currentBalance", balance);
                errorData.put("requiredBalance", ratePerMinute);
                errorData.put("shortfall", requiredAmount);
                errorData.put("ratePerMinute", ratePerMinute);
                errorData.put("message", String.format("余额不足，需要充值%.2f元才能发起通话", requiredAmount));
                
                return createErrorResponseWithData("余额不足1分钟，无法发起通话", "INSUFFICIENT_BALANCE", errorData);
            }
            
            // 4. 创建通话会话
            String callSessionId = generateCallSessionId();
            CallSession callSession = new CallSession();
            callSession.setId(callSessionId);
            callSession.setCallerId(callerId);
            callSession.setReceiverId(receiverId);
            callSession.setStatus("INITIATED");
            callSession.setRate(BigDecimal.valueOf(ratePerMinute));
            callSession.setCallerBalance(callerWallet.getBalance());
            callSession.setReceiverBalance(BigDecimal.valueOf(receiver.getIsOnline() ? 1000.0 : 0.0));
            callSession.setMaxDuration(maxDurationSeconds); // 设置最大可通话时间
            callSession.setChargedAmount(BigDecimal.ZERO); // 初始扣费金额为0
            callSession.setIsOnline(receiver.getIsOnline());
            callSession.setCreatedAt(LocalDateTime.now());
            callSession.setUpdatedAt(LocalDateTime.now());
            
            // 5. 保存到数据库
            callSession = callSessionRepository.save(callSession);
            
            // 6. 存储到内存
            activeCalls.put(callSessionId, callSession);
            
            // 7. 异步发送推送通知
            try {
                // 获取发起方信息
                Optional<User> callerOpt = userRepository.findById(callerId);
                if (callerOpt.isPresent()) {
                    User caller = callerOpt.get();
                    // 异步发送推送，避免阻塞事务
                    new Thread(() -> {
                        try {
                            jpushService.sendIncomingCallNotification(
                                receiverId, 
                                caller.getNickname(), 
                                callerId, 
                                callSessionId
                            );
                            System.out.println("来电推送发送成功: " + callSessionId);
                        } catch (Exception e) {
                            System.err.println("发送来电推送失败: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }).start();
                } else {
                    System.err.println("发起方用户不存在: " + callerId);
                }
            } catch (Exception e) {
                System.err.println("启动推送线程失败: " + e.getMessage());
                e.printStackTrace();
                // 推送失败不影响通话创建
            }
            
            // 8. 设置通话状态为RINGING，表示正在响铃
            callSession.setStatus("RINGING");
            callSession = callSessionRepository.save(callSession);
            activeCalls.put(callSessionId, callSession);
            
            // 9. 启动通话超时定时器（30秒后自动结束）
            startCallTimeoutTimer(callSessionId);
            
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
            callSession.setStartTime(getSafeCurrentTime());
            callSession.setUpdatedAt(getSafeCurrentTime());
            
            // 5. 停止超时定时器
            stopCallTimeoutTimer(callSessionId);
            
            // 6. 保存到数据库
            callSession = callSessionRepository.save(callSession);
            activeCalls.put(callSessionId, callSession);
            
            // 7. 启动实时计费定时器
            startBillingTimer(callSessionId);
            
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
            
            // 4. 停止超时定时器
            stopCallTimeoutTimer(callSessionId);
            
            // 5. 保存到数据库
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
            
            // 3. 计算通话费用（按分钟向上取整）
            BigDecimal chargedAmount = BigDecimal.ZERO;
            if ("ACTIVE".equals(callSession.getStatus()) && callSession.getStartTime() != null) {
                long durationSeconds = java.time.Duration.between(callSession.getStartTime(), LocalDateTime.now()).getSeconds();
                callSession.setDuration(durationSeconds);
                
                // 按分钟向上取整计算费用
                int minutes = (int) Math.ceil(durationSeconds / 60.0);
                chargedAmount = callSession.getRate().multiply(BigDecimal.valueOf(minutes));
                callSession.setChargedAmount(chargedAmount);
                callSession.setTotalCost(chargedAmount);
            }
            
            // 4. 一次性扣除费用
            if (chargedAmount.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    // 获取扣费前余额
                    Wallet callerWallet = walletRepository.findByUserId(callSession.getCallerId()).orElse(null);
                    BigDecimal balanceBefore = callerWallet != null ? callerWallet.getBalance() : BigDecimal.ZERO;
                    
                    // 扣除费用
                    walletService.consume(callSession.getCallerId(), chargedAmount, "视频通话费用", null);
                    
                    // 获取扣费后余额
                    callerWallet = walletRepository.findByUserId(callSession.getCallerId()).orElse(null);
                    BigDecimal balanceAfter = callerWallet != null ? callerWallet.getBalance() : BigDecimal.ZERO;
                    
                    // 记录扣费详情
                    CallCharge callCharge = new CallCharge(
                        callSessionId,
                        callSession.getCallerId(),
                        callSession.getDuration().intValue(),
                        callSession.getRate(),
                        chargedAmount,
                        balanceBefore,
                        balanceAfter
                    );
                    callChargeRepository.save(callCharge);
                    
                    System.out.println("扣除通话费用: " + chargedAmount + "元，通话时长: " + callSession.getDuration() + "秒");
                } catch (Exception e) {
                    System.out.println("扣除费用失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // 5. 更新通话状态
            callSession.setStatus("ENDED");
            callSession.setEndTime(LocalDateTime.now());
            callSession.setUpdatedAt(LocalDateTime.now());
            
            // 6. 停止实时计费定时器
            stopBillingTimer(callSessionId);
            
            // 7. 停止超时定时器
            stopCallTimeoutTimer(callSessionId);
            
            // 8. 保存到数据库
            callSession = callSessionRepository.save(callSession);
            activeCalls.remove(callSessionId);
            
            System.out.println("通话结束成功: " + callSessionId + ", 费用: " + chargedAmount + "元");
            
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
        response.put("maxDuration", callSession.getMaxDuration()); // 添加最大可通话时间字段
        response.put("chargedAmount", callSession.getChargedAmount()); // 添加已扣费金额字段
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
    
    /**
     * 创建带数据的错误响应
     */
    private Map<String, Object> createErrorResponseWithData(String message, String errorCode, Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("errorCode", errorCode);
        response.put("data", data);
        return response;
    }
    
    /**
     * 启动实时计费定时器
     */
    private void startBillingTimer(String callSessionId) {
        try {
            // 停止已存在的定时器
            stopBillingTimer(callSessionId);
            
            // 创建新的定时器，每分钟执行一次
            java.util.Timer timer = new java.util.Timer();
            timer.scheduleAtFixedRate(new java.util.TimerTask() {
                @Override
                public void run() {
                    processMinuteBillingInternal(callSessionId);
                }
            }, 60000, 60000); // 1分钟后开始，每60秒执行一次
            
            billingTimers.put(callSessionId, timer);
            System.out.println("启动实时计费定时器: " + callSessionId);
            
        } catch (Exception e) {
            System.out.println("启动计费定时器失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 停止实时计费定时器
     */
    private void stopBillingTimer(String callSessionId) {
        try {
            java.util.Timer timer = billingTimers.remove(callSessionId);
            if (timer != null) {
                timer.cancel();
                System.out.println("停止实时计费定时器: " + callSessionId);
            }
        } catch (Exception e) {
            System.out.println("停止计费定时器失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 内部实时计费处理
     */
    private void processMinuteBillingInternal(String callSessionId) {
        try {
            CallSession callSession = activeCalls.get(callSessionId);
            if (callSession == null) {
                callSession = callSessionRepository.findById(callSessionId).orElse(null);
            }
            
            if (callSession == null || !"ACTIVE".equals(callSession.getStatus())) {
                stopBillingTimer(callSessionId);
                return;
            }
            
            // 检查余额是否足够
            Wallet callerWallet = walletRepository.findByUserId(callSession.getCallerId()).orElse(null);
            if (callerWallet == null || callerWallet.getBalance().doubleValue() < CALL_RATE) {
                // 余额不足，自动结束通话
                System.out.println("余额不足，自动结束通话: " + callSessionId);
                endCall(callSessionId, callSession.getCallerId(), "INSUFFICIENT_BALANCE");
                return;
            }
            
            // 扣除1分钟费用
            try {
                walletService.consume(callSession.getCallerId(), BigDecimal.valueOf(CALL_RATE), "视频通话费用-实时扣费", null);
                
                // 更新通话会话
                callSession.setTotalCost(callSession.getTotalCost().add(BigDecimal.valueOf(CALL_RATE)));
                callSession.setCallerBalance(callerWallet.getBalance().subtract(BigDecimal.valueOf(CALL_RATE)));
                callSession.setUpdatedAt(LocalDateTime.now());
                
                // 保存到数据库
                callSessionRepository.save(callSession);
                activeCalls.put(callSessionId, callSession);
                
                System.out.println("实时扣费成功: " + callSessionId + ", 扣费: " + CALL_RATE + "元");
                
            } catch (Exception e) {
                System.out.println("实时扣费失败: " + e.getMessage());
                // 扣费失败，结束通话
                endCall(callSessionId, callSession.getCallerId(), "INSUFFICIENT_BALANCE");
            }
            
        } catch (Exception e) {
            System.out.println("实时计费处理异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 处理分钟计费（API接口）
     */
    public Object processMinuteBilling(String callSessionId, Long userId) {
        try {
            CallSession callSession = activeCalls.get(callSessionId);
            if (callSession == null) {
                callSession = callSessionRepository.findById(callSessionId).orElse(null);
            }
            
            if (callSession == null) {
                return createErrorResponse("通话会话不存在", "CALL_SESSION_NOT_FOUND");
            }
            
            // 检查用户权限
            if (!callSession.getCallerId().equals(userId)) {
                return createErrorResponse("无权限处理此通话计费", "UNAUTHORIZED");
            }
            
            // 检查通话状态
            if (!"ACTIVE".equals(callSession.getStatus())) {
                return createErrorResponse("通话未激活，无法计费", "CALL_NOT_ACTIVE");
            }
            
            // 执行计费
            processMinuteBillingInternal(callSessionId);
            
            return createCallSessionResponse(callSession);
            
        } catch (Exception e) {
            System.out.println("处理分钟计费异常: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("处理分钟计费失败: " + e.getMessage(), "PROCESS_BILLING_FAILED");
        }
    }
    
    /**
     * 检查余额是否足够继续通话
     */
    public Object checkBalanceForCall(String callSessionId, Long userId) {
        try {
            CallSession callSession = activeCalls.get(callSessionId);
            if (callSession == null) {
                callSession = callSessionRepository.findById(callSessionId).orElse(null);
            }
            
            if (callSession == null) {
                return createErrorResponse("通话会话不存在", "CALL_SESSION_NOT_FOUND");
            }
            
            // 检查用户权限
            if (!callSession.getCallerId().equals(userId)) {
                return createErrorResponse("无权限检查此通话余额", "UNAUTHORIZED");
            }
            
            // 获取当前余额
            Wallet callerWallet = walletRepository.findByUserId(callSession.getCallerId()).orElse(null);
            if (callerWallet == null) {
                return createErrorResponse("发起方钱包不存在", "CALLER_WALLET_NOT_FOUND");
            }
            
            double currentBalance = callerWallet.getBalance().doubleValue();
            boolean canContinue = currentBalance >= CALL_RATE;
            
            Map<String, Object> response = new HashMap<>();
            response.put("callSessionId", callSessionId);
            response.put("currentBalance", currentBalance);
            response.put("requiredBalance", CALL_RATE);
            response.put("canContinue", canContinue);
            response.put("remainingMinutes", Math.floor(currentBalance / CALL_RATE));
            
            return response;
            
        } catch (Exception e) {
            System.out.println("检查余额异常: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("检查余额失败: " + e.getMessage(), "CHECK_BALANCE_FAILED");
        }
    }
    
    /**
     * 获取通话费率信息
     */
    public Object getCallRateInfo(Long userId) {
        try {
            // 获取用户钱包信息
            Wallet userWallet = walletRepository.findByUserId(userId).orElse(null);
            double currentBalance = userWallet != null ? userWallet.getBalance().doubleValue() : 0.0;
            
            Map<String, Object> response = new HashMap<>();
            response.put("rate", CALL_RATE);
            response.put("rateText", String.format("¥%.0f/分钟", CALL_RATE));
            response.put("currentBalance", currentBalance);
            response.put("estimatedMinutes", Math.floor(currentBalance / CALL_RATE));
            response.put("currency", "CNY");
            response.put("billingUnit", "分钟");
            response.put("minimumCharge", CALL_RATE);
            
            return response;
            
        } catch (Exception e) {
            System.out.println("获取费率信息异常: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("获取费率信息失败: " + e.getMessage(), "GET_RATE_INFO_FAILED");
        }
    }
    
    /**
     * 检查来电
     */
    public Object checkIncomingCalls(Long userId) {
        try {
            // 查找状态为INITIATED或RINGING且接收方为当前用户的通话会话
            List<CallSession> incomingCalls = callSessionRepository.findByReceiverIdAndStatusInOrderByCreatedAtDesc(userId, Arrays.asList("INITIATED", "RINGING"));
            
            if (incomingCalls.isEmpty()) {
                return null; // 没有来电
            }
            
            // 返回最新的来电
            CallSession latestCall = incomingCalls.get(0);
            
            // 获取发起方信息
            Optional<User> callerOpt = userRepository.findById(latestCall.getCallerId());
            if (!callerOpt.isPresent()) {
                return null;
            }
            
            User caller = callerOpt.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("callSessionId", latestCall.getId());
            response.put("callerId", latestCall.getCallerId());
            response.put("callerName", caller.getNickname());
            response.put("callerPhone", caller.getPhone());
            response.put("rate", latestCall.getRate());
            response.put("status", latestCall.getStatus());
            response.put("createdAt", latestCall.getCreatedAt());
            
            System.out.println("检测到来电: " + latestCall.getId() + ", 发起方: " + caller.getNickname());
            
            return response;
            
        } catch (Exception e) {
            System.out.println("检查来电异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 启动通话超时定时器
     */
    private void startCallTimeoutTimer(String callSessionId) {
        try {
            // 停止已存在的超时定时器
            stopCallTimeoutTimer(callSessionId);
            
            // 创建新的超时定时器，30秒后执行
            java.util.Timer timer = new java.util.Timer();
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    handleCallTimeout(callSessionId);
                }
            }, 30000); // 30秒后执行
            
            timeoutTimers.put(callSessionId, timer);
            System.out.println("启动通话超时定时器: " + callSessionId + " (30秒)");
            
        } catch (Exception e) {
            System.out.println("启动通话超时定时器失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 停止通话超时定时器
     */
    private void stopCallTimeoutTimer(String callSessionId) {
        try {
            java.util.Timer timer = timeoutTimers.remove(callSessionId);
            if (timer != null) {
                timer.cancel();
                System.out.println("停止通话超时定时器: " + callSessionId);
            }
        } catch (Exception e) {
            System.out.println("停止通话超时定时器失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 获取安全的时间（带异常处理）
     */
    private LocalDateTime getSafeCurrentTime() {
        try {
            return LocalDateTime.now();
        } catch (Exception e) {
            System.err.println("获取当前时间失败，使用备用时间: " + e.getMessage());
            // 备用方案：返回一个合理的时间
            return LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        }
    }
    
    /**
     * 处理通话超时
     */
    private void handleCallTimeout(String callSessionId) {
        try {
            CallSession callSession = activeCalls.get(callSessionId);
            if (callSession == null) {
                callSession = callSessionRepository.findById(callSessionId).orElse(null);
            }
            
            if (callSession == null) {
                return;
            }
            
            // 检查通话状态，如果还是RINGING，则超时
            if ("RINGING".equals(callSession.getStatus())) {
                System.out.println("通话超时，自动结束: " + callSessionId);
                
                // 更新通话状态为超时
                callSession.setStatus("TIMEOUT");
                callSession.setEndTime(getSafeCurrentTime());
                callSession.setUpdatedAt(getSafeCurrentTime());
                
                // 保存到数据库
                callSession = callSessionRepository.save(callSession);
                activeCalls.remove(callSessionId);
                
                // 停止超时定时器
                stopCallTimeoutTimer(callSessionId);
                
                System.out.println("通话超时处理完成: " + callSessionId);
            }
            
        } catch (Exception e) {
            System.out.println("处理通话超时异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
