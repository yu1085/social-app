package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.IntimacyLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IntimacyLogRepository extends JpaRepository<IntimacyLog, Long> {

    /**
     * 查询两个用户之间的亲密度变更日志
     */
    List<IntimacyLog> findByUserIdAndTargetUserIdOrderByCreatedAtDesc(Long userId, Long targetUserId);

    /**
     * 分页查询两个用户之间的亲密度变更日志
     */
    Page<IntimacyLog> findByUserIdAndTargetUserIdOrderByCreatedAtDesc(Long userId, Long targetUserId, Pageable pageable);

    /**
     * 查询用户的所有升级记录
     */
    List<IntimacyLog> findByUserIdAndLevelUpTrueOrderByCreatedAtDesc(Long userId);

    /**
     * 查询指定时间范围内的日志
     */
    List<IntimacyLog> findByUserIdAndTargetUserIdAndCreatedAtBetween(
        Long userId, Long targetUserId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询指定行为类型的日志
     */
    List<IntimacyLog> findByUserIdAndTargetUserIdAndActionType(
        Long userId, Long targetUserId, String actionType);
}
