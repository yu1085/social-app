package com.socialmeet.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_relationships")
public class UserRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private RelationshipType relationshipType;

    @Column(name = "intimacy_score", nullable = false)
    private Integer intimacyScore = 0;

    @Column(name = "remark")
    private String remark; // 备注

    @Column(name = "is_subscribed")
    private Boolean isSubscribed = false; // 是否订阅状态通知

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum RelationshipType {
        FRIEND,     // 知友
        LIKE,       // 喜欢
        INTIMATE,   // 亲密
        BLACKLIST,  // 黑名单
        SUBSCRIBE   // 订阅
    }
}
