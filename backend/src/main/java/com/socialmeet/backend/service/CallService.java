package com.socialmeet.backend.service;

import com.socialmeet.backend.entity.CallSession;
import com.socialmeet.backend.entity.Transaction;
import com.socialmeet.backend.entity.User;
import com.socialmeet.backend.entity.UserSettings;
import com.socialmeet.backend.entity.Wallet;
import com.socialmeet.backend.repository.CallSessionRepository;
import com.socialmeet.backend.repository.TransactionRepository;
import com.socialmeet.backend.repository.UserRepository;
import com.socialmeet.backend.repository.UserSettingsRepository;
import com.socialmeet.backend.repository.WalletRepository;
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
    private final UserSettingsRepository userSettingsRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final JPushService jPushService;
    private final SignalingService signalingService;

    /**
     * 获取用户通话价格信息
     */
    public Map<String, Object> getUserCallPrices(Long userId) {
        log.info("获取用户通话价格 - userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 从数据库获取用户设置，如果不存在则创建默认设置
        UserSettings settings = userSettingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultUserSettings(userId));

        Map<String, Object> prices = new HashMap<>();
        prices.put("videoCallPrice", settings.getVideoCallPrice().doubleValue());
        prices.put("voiceCallPrice", settings.getVoiceCallPrice().doubleValue());
        prices.put("messagePrice", settings.getMessagePrice().doubleValue());
        prices.put("videoCallEnabled", settings.getVideoCallEnabled());
        prices.put("voiceCallEnabled", settings.getVoiceCallEnabled());
        prices.put("messageChargeEnabled", settings.getMessageChargeEnabled());

        log.info("用户价格信息 - userId: {}, videoPrice: {}, voicePrice: {}, videoEnabled: {}, voiceEnabled: {}",
                userId, settings.getVideoCallPrice(), settings.getVoiceCallPrice(),
                settings.getVideoCallEnabled(), settings.getVoiceCallEnabled());

        return prices;
    }

    /**
     * 创建默认用户设置
     */
    private UserSettings createDefaultUserSettings(Long userId) {
        UserSettings settings = new UserSettings();
        settings.setUserId(userId);
        settings.setVideoCallEnabled(true);
        settings.setVoiceCallEnabled(true);
        settings.setMessageChargeEnabled(false);
        settings.setVideoCallPrice(BigDecimal.ZERO);
        settings.setVoiceCallPrice(BigDecimal.ZERO);
        settings.setMessagePrice(BigDecimal.ZERO);

        UserSettings saved = userSettingsRepository.save(settings);
        log.info("创建默认用户设置 - userId: {}", userId);

        return saved;
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

        // 获取接收者的通话设置
        UserSettings receiverSettings = userSettingsRepository.findByUserId(receiverId)
                .orElseGet(() -> createDefaultUserSettings(receiverId));

        // 检查接收者是否开启了该类型的通话
        if ("VIDEO".equals(callType) && !receiverSettings.getVideoCallEnabled()) {
            log.warn("接收者未开启视频通话 - receiverId: {}", receiverId);
            throw new RuntimeException("对方未开启视频通话功能");
        }

        if ("VOICE".equals(callType) && !receiverSettings.getVoiceCallEnabled()) {
            log.warn("接收者未开启语音通话 - receiverId: {}", receiverId);
            throw new RuntimeException("对方未开启语音通话功能");
        }

        // 获取通话价格
        BigDecimal pricePerMinute;
        if ("VIDEO".equals(callType)) {
            pricePerMinute = receiverSettings.getVideoCallPrice();
        } else {
            pricePerMinute = receiverSettings.getVoiceCallPrice();
        }

        log.info("通话价格 - receiverId: {}, callType: {}, price: {}", receiverId, callType, pricePerMinute);

        // 如果价格大于0，检查发起者余额（预估1分钟通话费用）
        if (pricePerMinute.compareTo(BigDecimal.ZERO) > 0) {
            Wallet callerWallet = walletRepository.findByUserId(callerId)
                    .orElseThrow(() -> new RuntimeException("发起者钱包不存在"));

            if (callerWallet.getBalance().compareTo(pricePerMinute) < 0) {
                log.warn("发起者余额不足 - callerId: {}, balance: {}, required: {}",
                        callerId, callerWallet.getBalance(), pricePerMinute);
                throw new RuntimeException("余额不足，请先充值");
            }

            log.info("余额检查通过 - callerId: {}, balance: {}, pricePerMinute: {}",
                    callerId, callerWallet.getBalance(), pricePerMinute);
        }

        // 创建通话会话
        CallSession callSession = new CallSession();
        callSession.setCallSessionId(generateCallSessionId());
        callSession.setCallerId(callerId);
        callSession.setReceiverId(receiverId);
        callSession.setCallType(CallSession.CallType.valueOf(callType));
        callSession.setStatus(CallSession.CallStatus.INITIATED);

        // 设置价格（从接收者设置获取）
        callSession.setPricePerMinute(pricePerMinute);

        callSession = callSessionRepository.save(callSession);
        log.info("通话会话创建成功 - sessionId: {}, price: {}", callSession.getCallSessionId(), pricePerMinute);

        // 1. 发送WebSocket信令消息（主要方式）
        try {
            signalingService.sendCallInitiate(callerId, receiverId, callSession.getCallSessionId(), callType);
            log.info("✅ 信令消息已发送 - receiverId: {}, sessionId: {}", receiverId, callSession.getCallSessionId());
        } catch (Exception e) {
            log.error("❌ 发送信令消息失败 - receiverId: {}, sessionId: {}", receiverId, callSession.getCallSessionId(), e);
        }

        // 2. 发送JPush通知给接收方（备用方式）
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
                log.info("✅ 推送通知已发送 - receiverId: {}, sessionId: {}", receiverId, callSession.getCallSessionId());
            } else {
                log.warn("⚠️ 推送通知发送失败 - receiverId: {}, sessionId: {}", receiverId, callSession.getCallSessionId());
            }
        } catch (Exception e) {
            log.error("❌ 发送推送通知异常 - receiverId: {}, sessionId: {}", receiverId, callSession.getCallSessionId(), e);
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

        callSession = callSessionRepository.save(callSession);

        // 1. 发送WebSocket信令消息（主要方式）
        try {
            signalingService.sendCallAccept(callSession.getCallerId(), callSessionId);
            log.info("✅ 接听信令已发送 - callerId: {}, sessionId: {}", callSession.getCallerId(), callSessionId);
        } catch (Exception e) {
            log.error("❌ 发送接听信令失败 - callerId: {}, sessionId: {}", callSession.getCallerId(), callSessionId, e);
        }

        // 2. 发送状态通知给发起方（备用方式）
        try {
            User receiver = userRepository.findById(userId).orElse(null);
            String receiverName = receiver != null ? 
                (receiver.getNickname() != null ? receiver.getNickname() : receiver.getUsername()) : "用户";
            
            boolean sent = jPushService.sendCallStatusNotification(
                    callSession.getCallerId(),
                    callSessionId,
                    "ACCEPTED",
                    receiverName + " 已接受您的通话"
            );
            
            if (sent) {
                log.info("✅ 接听推送已发送 - callerId: {}, sessionId: {}", 
                        callSession.getCallerId(), callSessionId);
            } else {
                log.warn("⚠️ 接听推送发送失败 - callerId: {}, sessionId: {}", 
                        callSession.getCallerId(), callSessionId);
            }
        } catch (Exception e) {
            log.error("❌ 发送接听推送异常 - callerId: {}, sessionId: {}", 
                    callSession.getCallerId(), callSessionId, e);
        }

        return callSession;
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

        callSession = callSessionRepository.save(callSession);

        // 1. 发送WebSocket信令消息（主要方式）
        try {
            signalingService.sendCallReject(callSession.getCallerId(), callSessionId);
            log.info("✅ 拒绝信令已发送 - callerId: {}, sessionId: {}", callSession.getCallerId(), callSessionId);
        } catch (Exception e) {
            log.error("❌ 发送拒绝信令失败 - callerId: {}, sessionId: {}", callSession.getCallerId(), callSessionId, e);
        }

        // 2. 发送状态通知给发起方（备用方式）
        try {
            User receiver = userRepository.findById(userId).orElse(null);
            String receiverName = receiver != null ? 
                (receiver.getNickname() != null ? receiver.getNickname() : receiver.getUsername()) : "用户";
            
            boolean sent = jPushService.sendCallStatusNotification(
                    callSession.getCallerId(),
                    callSessionId,
                    "REJECTED",
                    receiverName + " 拒绝了您的通话"
            );
            
            if (sent) {
                log.info("✅ 拒绝推送已发送 - callerId: {}, sessionId: {}", 
                        callSession.getCallerId(), callSessionId);
            } else {
                log.warn("⚠️ 拒绝推送发送失败 - callerId: {}, sessionId: {}", 
                        callSession.getCallerId(), callSessionId);
            }
        } catch (Exception e) {
            log.error("❌ 发送拒绝推送异常 - callerId: {}, sessionId: {}", 
                    callSession.getCallerId(), callSessionId, e);
        }

        return callSession;
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

            // 如果费用大于0，执行扣费和转账
            if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    processCallPayment(callSession, totalCost);
                } catch (Exception e) {
                    log.error("通话扣费失败 - sessionId: {}, error: {}", callSessionId, e.getMessage(), e);
                    // 不影响通话会话的保存，只记录错误
                }
            }
        }

        return callSessionRepository.save(callSession);
    }

    /**
     * 处理通话扣费
     * @param callSession 通话会话
     * @param totalCost 总费用
     */
    private void processCallPayment(CallSession callSession, BigDecimal totalCost) {
        Long callerId = callSession.getCallerId();
        Long receiverId = callSession.getReceiverId();

        log.info("开始处理通话扣费 - callerId: {}, receiverId: {}, amount: {}",
                callerId, receiverId, totalCost);

        // 获取发起者和接收者的钱包
        Wallet callerWallet = walletRepository.findByUserId(callerId)
                .orElseThrow(() -> new RuntimeException("发起者钱包不存在"));

        Wallet receiverWallet = walletRepository.findByUserId(receiverId)
                .orElseThrow(() -> new RuntimeException("接收者钱包不存在"));

        // 检查发起者余额
        if (callerWallet.getBalance().compareTo(totalCost) < 0) {
            log.warn("发起者余额不足 - callerId: {}, balance: {}, required: {}",
                    callerId, callerWallet.getBalance(), totalCost);
            throw new RuntimeException("余额不足，无法完成扣费");
        }

        // 从发起者扣费
        callerWallet.setBalance(callerWallet.getBalance().subtract(totalCost));
        callerWallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(callerWallet);

        log.info("发起者扣费成功 - callerId: {}, 扣除: {}, 剩余: {}",
                callerId, totalCost, callerWallet.getBalance());

        // 同步更新User表的balance字段（向后兼容）
        User caller = userRepository.findById(callerId).orElse(null);
        if (caller != null) {
            caller.setBalance(callerWallet.getBalance());
            userRepository.save(caller);
        }

        // 转账给接收者
        receiverWallet.setBalance(receiverWallet.getBalance().add(totalCost));
        receiverWallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(receiverWallet);

        log.info("接收者入账成功 - receiverId: {}, 收入: {}, 余额: {}",
                receiverId, totalCost, receiverWallet.getBalance());

        // 同步更新User表的balance字段（向后兼容）
        User receiver = userRepository.findById(receiverId).orElse(null);
        if (receiver != null) {
            receiver.setBalance(receiverWallet.getBalance());
            userRepository.save(receiver);
        }

        // 记录发起者的消费交易
        Transaction callerTransaction = new Transaction();
        callerTransaction.setUserId(callerId);
        callerTransaction.setTransactionType(Transaction.TransactionType.CALL_CHARGE);
        callerTransaction.setCoinAmount(totalCost.negate()); // 负数表示扣费
        callerTransaction.setBalanceBefore(callerWallet.getBalance().add(totalCost));
        callerTransaction.setBalanceAfter(callerWallet.getBalance());
        callerTransaction.setDescription(String.format("通话扣费 - 会话ID: %s, 时长: %d秒",
                callSession.getCallSessionId(), callSession.getDurationSeconds()));
        callerTransaction.setStatus(Transaction.TransactionStatus.SUCCESS);
        callerTransaction.setCoinSource(Transaction.CoinSource.CONSUMED);
        callerTransaction.setWealthValue(0); // 消费不增加财富值
        transactionRepository.save(callerTransaction);

        // 记录接收者的收入交易
        Transaction receiverTransaction = new Transaction();
        receiverTransaction.setUserId(receiverId);
        receiverTransaction.setTransactionType(Transaction.TransactionType.CALL_INCOME);
        receiverTransaction.setCoinAmount(totalCost);
        receiverTransaction.setBalanceBefore(receiverWallet.getBalance().subtract(totalCost));
        receiverTransaction.setBalanceAfter(receiverWallet.getBalance());
        receiverTransaction.setDescription(String.format("通话收入 - 会话ID: %s, 时长: %d秒",
                callSession.getCallSessionId(), callSession.getDurationSeconds()));
        receiverTransaction.setStatus(Transaction.TransactionStatus.SUCCESS);
        receiverTransaction.setCoinSource(Transaction.CoinSource.EARNED);
        receiverTransaction.setWealthValue(0); // 收入不增加财富值
        transactionRepository.save(receiverTransaction);

        log.info("通话扣费完成 - callerId: {}, receiverId: {}, amount: {}",
                callerId, receiverId, totalCost);
    }

    /**
     * 获取通话状态
     */
    public CallSession getCallStatus(String callSessionId, Long userId) {
        log.info("获取通话状态 - callSessionId: {}, userId: {}", callSessionId, userId);

        CallSession callSession = callSessionRepository.findByCallSessionId(callSessionId)
                .orElseThrow(() -> new RuntimeException("通话会话不存在"));

        // 检查用户是否有权限查看此通话
        if (!callSession.getCallerId().equals(userId) && !callSession.getReceiverId().equals(userId)) {
            throw new RuntimeException("无权查看此通话状态");
        }

        return callSession;
    }

    /**
     * 生成唯一的通话会话ID
     */
    private String generateCallSessionId() {
        return "CALL_" + UUID.randomUUID().toString().replace("-", "");
    }
}
