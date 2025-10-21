package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.CallRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CallRecordRepository extends JpaRepository<CallRecord, Long> {

    /**
     * 根据session ID查找通话记录
     */
    Optional<CallRecord> findBySessionId(String sessionId);

    /**
     * 获取用户的通话记录
     */
    @Query("SELECT c FROM CallRecord c WHERE c.callerId = :userId OR c.calleeId = :userId " +
           "ORDER BY c.createdAt DESC")
    List<CallRecord> findUserCallRecords(@Param("userId") Long userId);

    /**
     * 获取两个用户之间的通话记录
     */
    @Query("SELECT c FROM CallRecord c WHERE " +
           "(c.callerId = :userId1 AND c.calleeId = :userId2) OR " +
           "(c.callerId = :userId2 AND c.calleeId = :userId1) " +
           "ORDER BY c.createdAt DESC")
    List<CallRecord> findCallRecordsBetweenUsers(@Param("userId1") Long userId1,
                                                @Param("userId2") Long userId2);

    /**
     * 获取用户未接来电数量
     */
    @Query("SELECT COUNT(c) FROM CallRecord c WHERE c.calleeId = :userId " +
           "AND c.callStatus = 'MISSED'")
    Long countMissedCalls(@Param("userId") Long userId);
}
