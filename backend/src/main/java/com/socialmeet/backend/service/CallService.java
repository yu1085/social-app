package com.socialmeet.backend.service;

import com.socialmeet.backend.entity.CallSession;
import com.socialmeet.backend.entity.User;
import com.socialmeet.backend.repository.CallSessionRepository;
import com.socialmeet.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 通话服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CallService {

    private final CallSessionRepository callSessionRepository;
    private final UserRepository userRepository;
    private final JPushService jPushService;

    /**
     * 获取用户通话价格信息
     */
    public Map<String, Object> getUserCallPrices(Long userId) {
        log.info("获取用户通话价格 - userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Map<String, Object> prices = new HashMap<>();

        // 默认价格（可以从用户设置中获取）
        prices.put("videoCallPrice", 300.0);
        prices.put("voiceCallPrice", 150.0);
        prices.put("messagePrice", 1.0);
        prices.put("videoCallEnabled", true);
        prices.put("voiceCallEnabled", true);
        prices.put("messageChargeEnabled", false);

        return prices;
    }

    /**
     * 发起通话
     */
    @Transactional
    public CallSession initiateCall(Long callerId, Long receiverId, String callType) {
        log.info("发起通话 - callerId: {}, receiverId: {}, callType: {}", callerId, receiverId, callType);

        // 检查发起者是否存在
        User caller = userRepository.findById(callerId)
                .orElseThrow(() -> new RuntimeException("发起者不存在"));

        // 检查接收者是否存在
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("接收者不存在"));

        // 检查接收者是否在线
        if (!receiver.getIsOnline()) {
            throw new RuntimeException("对方不在线");
        }

        // 创建通话会话
        CallSession callSession = new CallSession();
        callSession.setCallSessionId(generateCallSessionId());
        callSession.setCallerId(callerId);
        callSession.setReceiverId(receiverId);
        callSession.setCallType(CallSession.CallType.valueOf(callType));
        callSession.setStatus(CallSession.CallStatus.INITIATED);

        // 设置价格
        if ("VIDEO".equals(callType)) {
            callSession.setPricePerMinute(BigDecimal.valueOf(300.0));
        } else {
            callSession.setPricePerMinute(BigDecimal.valueOf(150.0));
        }

        callSession = callSessionRepository.save(callSession);
        log.info("通话会话创建成功 - sessionId: {}", callSession.getCallSessionId());

        // 发送JPush通知给接收方
        try {
            boolean sent = jPushService.sendCallNotification(
                    receiverId,
                    callerId,
                    caller.getNickname() != null ? caller.getNickname() : caller.getUsername(),
                    caller.getAvatarUrl(),
                    callSession.getCallSessionId(),
                    callType
            );
            if (sent) {
                log.info("来电通知已发送 - receiverId: {}, sessionId: {}", receiverId, callSession.getCallSessionId());
            } else {
                log.warn("来电通知发送失败 - receiverId: {}, sessionId: {}", receiverId, callSession.getCallSessionId());
            }
        } catch (Exception e) {
            log.error("发送来电通知异常 - receiverId: {}, sessionId: {}", receiverId, callSession.getCallSessionId(), e);
            // 不影响通话会话的创建，继续返回
        }

        return callSession;
    }

    /**
     * 接受通话
     */
    @Transactional
    public CallSession acceptCall(String callSessionId, Long userId) {
        log.info("接受通话 - callSessionId: {}, userId: {}", callSessionId, userId);

        CallSession callSession = callSessionRepository.findByCallSessionId(callSessionId)
                .orElseThrow(() -> new RuntimeException("通话会话不存在"));

        if (!callSession.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权接受此通话");
        }

        callSession.setStatus(CallSession.CallStatus.ACCEPTED);
        callSession.setStartTime(LocalDateTime.now());

        return callSessionRepository.save(callSession);
    }

    /**
     * 拒绝通话
     */
    @Transactional
    public CallSession rejectCall(String callSessionId, Long userId) {
        log.info("拒绝通话 - callSessionId: {}, userId: {}", callSessionId, userId);

        CallSession callSession = callSessionRepository.findByCallSessionId(callSessionId)
                .orElseThrow(() -> new RuntimeException("通话会话不存在"));

        if (!callSession.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权拒绝此通话");
        }

        callSession.setStatus(CallSession.CallStatus.REJECTED);
        callSession.setEndTime(LocalDateTime.now());

        return callSessionRepository.save(callSession);
    }

    /**
     * 结束通话
     */
    @Transactional
    public CallSession endCall(String callSessionId, Long userId) {
        log.info("结束通话 - callSessionId: {}, userId: {}", callSessionId, userId);

        CallSession callSession = callSessionRepository.findByCallSessionId(callSessionId)
                .orElseThrow(() -> new RuntimeException("通话会话不存在"));

        if (!callSession.getCallerId().equals(userId) && !callSession.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权结束此通话");
        }

        callSession.setStatus(CallSession.CallStatus.ENDED);
        callSession.setEndTime(LocalDateTime.now());

        // 计算通话时长（秒）
        if (callSession.getStartTime() != null) {
            long durationSeconds = java.time.Duration.between(
                    callSession.getStartTime(),
                    callSession.getEndTime()
            ).getSeconds();
            callSession.setDurationSeconds((int) durationSeconds);

            // 计算费用（向上取整到分钟）
            int durationMinutes = (int) Math.ceil(durationSeconds / 60.0);
            BigDecimal totalCost = callSession.getPricePerMinute().multiply(BigDecimal.valueOf(durationMinutes));
            callSession.setTotalCost(totalCost);

            log.info("通话结束 - 时长: {}秒, 费用: {}元", durationSeconds, totalCost);
        }

        return callSessionRepository.save(callSession);
    }

    /**
     * 生成唯一的通话会话ID
     */
    private String generateCallSessionId() {
        return "CALL_" + UUID.randomUUID().toString().replace("-", "");
    }
}
