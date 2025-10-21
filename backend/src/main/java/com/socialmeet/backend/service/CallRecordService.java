package com.socialmeet.backend.service;

import com.socialmeet.backend.dto.CallRecordDTO;
import com.socialmeet.backend.entity.CallRecord;
import com.socialmeet.backend.entity.User;
import com.socialmeet.backend.repository.CallRecordRepository;
import com.socialmeet.backend.repository.UserRepository;
import com.socialmeet.backend.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallRecordService {

    private final CallRecordRepository callRecordRepository;
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;

    /**
     * 获取用户的通话记录列表
     */
    public List<CallRecordDTO> getCallRecords(Long userId) {
        log.info("获取用户{}的通话记录", userId);

        List<CallRecord> records = callRecordRepository.findUserCallRecords(userId);
        List<CallRecordDTO> dtoList = new ArrayList<>();

        for (CallRecord record : records) {
            // 确定对方用户ID
            Long otherUserId = record.getCallerId().equals(userId) ?
                    record.getCalleeId() : record.getCallerId();

            User otherUser = userRepository.findById(otherUserId).orElse(null);
            if (otherUser == null) continue;

            // 获取当前价格
            BigDecimal currentPrice = userSettingsRepository.findByUserId(otherUserId)
                    .map(settings -> record.getCallType() == CallRecord.CallType.VIDEO ?
                            settings.getVideoCallPrice() : settings.getVoiceCallPrice())
                    .orElse(BigDecimal.ZERO);

            // 构建DTO
            CallRecordDTO dto = CallRecordDTO.builder()
                    .id(record.getId())
                    .sessionId(record.getSessionId())
                    .userId(otherUserId)
                    .nickname(otherUser.getNickname())
                    .avatar("")  // User实体没有avatar字段，使用空字符串
                    .callType(record.getCallType().name())
                    .callStatus(record.getCallStatus().name())
                    .duration(record.getDuration())
                    .pricePerMin(record.getPricePerMin())
                    .totalCost(record.getTotalCost())
                    .callTime(record.getCreatedAt())
                    .isMissed(record.getCallStatus() == CallRecord.CallStatus.MISSED)
                    .callStatusText(getCallStatusText(record))
                    .build();

            dtoList.add(dto);
        }

        return dtoList;
    }

    /**
     * 创建通话记录
     */
    @Transactional
    public CallRecord createCallRecord(String sessionId, Long callerId, Long calleeId,
                                      CallRecord.CallType callType) {
        log.info("创建通话记录: sessionId={}, caller={}, callee={}", sessionId, callerId, calleeId);

        // 获取被叫用户的价格设置
        BigDecimal pricePerMin = userSettingsRepository.findByUserId(calleeId)
                .map(settings -> callType == CallRecord.CallType.VIDEO ?
                        settings.getVideoCallPrice() : settings.getVoiceCallPrice())
                .orElse(BigDecimal.ZERO);

        CallRecord record = new CallRecord();
        record.setSessionId(sessionId);
        record.setCallerId(callerId);
        record.setCalleeId(calleeId);
        record.setCallType(callType);
        record.setCallStatus(CallRecord.CallStatus.RINGING);
        record.setPricePerMin(pricePerMin);
        record.setStartTime(LocalDateTime.now());

        return callRecordRepository.save(record);
    }

    /**
     * 更新通话状态
     */
    @Transactional
    public void updateCallStatus(String sessionId, CallRecord.CallStatus status) {
        CallRecord record = callRecordRepository.findBySessionId(sessionId).orElse(null);
        if (record == null) {
            log.warn("通话记录不存在: {}", sessionId);
            return;
        }

        record.setCallStatus(status);

        if (status == CallRecord.CallStatus.CONNECTED) {
            record.setStartTime(LocalDateTime.now());
        } else if (status == CallRecord.CallStatus.ENDED || status == CallRecord.CallStatus.CANCELLED) {
            record.setEndTime(LocalDateTime.now());
            // 计算通话时长和费用
            if (record.getStartTime() != null && status == CallRecord.CallStatus.ENDED) {
                long seconds = Duration.between(record.getStartTime(), record.getEndTime()).getSeconds();
                record.setDuration((int) seconds);

                // 计算费用（向上取整到分钟）
                int minutes = (int) Math.ceil(seconds / 60.0);
                BigDecimal totalCost = record.getPricePerMin().multiply(BigDecimal.valueOf(minutes));
                record.setTotalCost(totalCost);
            }
        }

        callRecordRepository.save(record);
    }

    /**
     * 获取通话状态文本
     */
    private String getCallStatusText(CallRecord record) {
        if (record.getCallStatus() == CallRecord.CallStatus.CANCELLED) {
            return "已取消通话";
        } else if (record.getCallStatus() == CallRecord.CallStatus.MISSED) {
            return "未接来电";
        } else if (record.getCallStatus() == CallRecord.CallStatus.ENDED && record.getDuration() > 0) {
            int minutes = record.getDuration() / 60;
            int seconds = record.getDuration() % 60;
            return String.format("通话时长 %02d:%02d:%02d", minutes / 60, minutes % 60, seconds);
        }
        return "通话中";
    }
}
