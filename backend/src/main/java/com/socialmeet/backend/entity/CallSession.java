package com.socialmeet.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 通话会话实体类
 * 对应数据库表: call_sessions
 */
@Entity
@Table(name = "call_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "call_session_id", nullable = false, unique = true, length = 100)
    private String callSessionId;

    @Column(name = "caller_id", nullable = false)
    private Long callerId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "call_type", nullable = false, length = 10)
    private CallType callType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CallStatus status = CallStatus.INITIATED;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "price_per_minute", precision = 10, scale = 2)
    private BigDecimal pricePerMinute;

    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 通话类型枚举
     */
    public enum CallType {
        VIDEO,  // 视频通话
        VOICE   // 语音通话
    }

    /**
     * 通话状态枚举
     */
    public enum CallStatus {
        INITIATED,   // 已发起
        RINGING,     // 振铃中
        ACCEPTED,    // 已接受
        ONGOING,     // 进行中
        ENDED,       // 已结束
        CANCELLED,   // 已取消
        REJECTED,    // 已拒绝
        MISSED       // 未接听
    }
}
