package com.example.socialmeet.repository;

import com.example.socialmeet.entity.CallSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 通话设置Repository
 */
@Repository
public interface CallSettingsRepository extends JpaRepository<CallSettings, Long> {
    
    /**
     * 根据用户ID查找通话设置
     */
    Optional<CallSettings> findByUserId(Long userId);
    
    /**
     * 检查用户是否存在通话设置
     */
    boolean existsByUserId(Long userId);
}
