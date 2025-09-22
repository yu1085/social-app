package com.example.socialmeet.repository;

import com.example.socialmeet.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 设备Token仓库
 */
@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    
    /**
     * 根据用户ID查找活跃的设备Token
     */
    List<DeviceToken> findByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * 根据Token查找设备Token
     */
    Optional<DeviceToken> findByToken(String token);
    
    /**
     * 根据用户ID和Token查找设备Token
     */
    Optional<DeviceToken> findByUserIdAndToken(Long userId, String token);
    
    /**
     * 根据用户ID统计活跃设备数量
     */
    long countByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * 根据平台查找活跃设备Token
     */
    List<DeviceToken> findByPlatformAndIsActiveTrue(String platform);
    
    /**
     * 查找所有活跃设备Token
     */
    List<DeviceToken> findByIsActiveTrue();
    
    /**
     * 根据用户ID查找所有设备Token（包括非活跃）
     */
    List<DeviceToken> findByUserIdOrderByUpdatedAtDesc(Long userId);
    
    /**
     * 查找过期的设备Token（超过30天未更新）
     */
    @Query("SELECT dt FROM DeviceToken dt WHERE dt.updatedAt < :expiredTime AND dt.isActive = true")
    List<DeviceToken> findExpiredTokens(@Param("expiredTime") java.time.LocalDateTime expiredTime);
    
    /**
     * 根据用户ID和平台查找活跃设备Token
     */
    List<DeviceToken> findByUserIdAndPlatformAndIsActiveTrue(Long userId, String platform);
}
