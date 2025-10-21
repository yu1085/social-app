package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.MessageQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MessageQuotaRepository extends JpaRepository<MessageQuota, Long> {

    /**
     * 查找用户某天的配额记录
     */
    Optional<MessageQuota> findByUserIdAndQuotaDate(Long userId, LocalDate quotaDate);
}
