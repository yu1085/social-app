package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.CallSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 通话会话数据访问层
 */
@Repository
public interface CallSessionRepository extends JpaRepository<CallSession, Long> {

    /**
     * 根据通话会话ID查找
     */
    Optional<CallSession> findByCallSessionId(String callSessionId);

    /**
     * 查找用户的所有通话记录
     */
    List<CallSession> findByCallerIdOrReceiverIdOrderByCreatedAtDesc(Long callerId, Long receiverId);

    /**
     * 查找用户的通话记录（带分页）
     */
    List<CallSession> findTop10ByCallerIdOrReceiverIdOrderByCreatedAtDesc(Long callerId, Long receiverId);
}
