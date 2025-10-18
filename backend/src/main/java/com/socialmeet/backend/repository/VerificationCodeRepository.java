package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 验证码数据仓库接口
 */
@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    /**
     * 查找有效的验证码
     */
    Optional<VerificationCode> findByPhoneAndCodeAndIsUsedFalseAndExpiredAtAfter(
        String phone,
        String code,
        LocalDateTime now
    );

    /**
     * 查找最新的有效验证码（按创建时间倒序）
     */
    Optional<VerificationCode> findFirstByPhoneAndCodeAndIsUsedFalseAndExpiredAtAfterOrderByCreatedAtDesc(
        String phone,
        String code,
        LocalDateTime now
    );

    /**
     * 查找最近的验证码（用于限制发送频率）
     */
    Optional<VerificationCode> findFirstByPhoneOrderByCreatedAtDesc(String phone);

    /**
     * 删除过期的验证码
     */
    void deleteByExpiredAtBefore(LocalDateTime now);
}
