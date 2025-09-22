package com.example.socialmeet.repository;

import com.example.socialmeet.entity.UserOnlineStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户在线状态数据访问层
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface UserOnlineStatusRepository extends JpaRepository<UserOnlineStatusEntity, Long> {
    
    /**
     * 根据用户ID查询在线状态
     */
    Optional<UserOnlineStatusEntity> findByUserId(Long userId);
    
    /**
     * 查询所有在线用户
     */
    @Query("SELECT u FROM UserOnlineStatusEntity u WHERE u.isOnline = true ORDER BY u.lastSeen DESC")
    List<UserOnlineStatusEntity> findOnlineUsers();
    
    /**
     * 根据状态查询用户
     */
    @Query("SELECT u FROM UserOnlineStatusEntity u WHERE u.status = :status ORDER BY u.lastSeen DESC")
    List<UserOnlineStatusEntity> findByStatus(@Param("status") UserOnlineStatusEntity.OnlineStatus status);
    
    /**
     * 查询指定时间内在线的用户
     */
    @Query("SELECT u FROM UserOnlineStatusEntity u WHERE u.lastSeen >= :since ORDER BY u.lastSeen DESC")
    List<UserOnlineStatusEntity> findUsersOnlineSince(@Param("since") LocalDateTime since);
    
    /**
     * 更新用户在线状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserOnlineStatusEntity u SET " +
           "u.isOnline = :isOnline, " +
           "u.status = :status, " +
           "u.lastSeen = :lastSeen " +
           "WHERE u.userId = :userId")
    int updateOnlineStatus(@Param("userId") Long userId,
                          @Param("isOnline") Boolean isOnline,
                          @Param("status") UserOnlineStatusEntity.OnlineStatus status,
                          @Param("lastSeen") LocalDateTime lastSeen);
    
    /**
     * 设置用户为离线状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserOnlineStatusEntity u SET " +
           "u.isOnline = false, " +
           "u.status = 'OFFLINE', " +
           "u.lastSeen = :lastSeen " +
           "WHERE u.userId = :userId")
    int setUserOffline(@Param("userId") Long userId, @Param("lastSeen") LocalDateTime lastSeen);
    
    /**
     * 更新设备信息
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserOnlineStatusEntity u SET " +
           "u.deviceType = :deviceType, " +
           "u.deviceId = :deviceId, " +
           "u.appVersion = :appVersion, " +
           "u.osVersion = :osVersion, " +
           "u.networkType = :networkType, " +
           "u.ipAddress = :ipAddress " +
           "WHERE u.userId = :userId")
    int updateDeviceInfo(@Param("userId") Long userId,
                        @Param("deviceType") String deviceType,
                        @Param("deviceId") String deviceId,
                        @Param("appVersion") String appVersion,
                        @Param("osVersion") String osVersion,
                        @Param("networkType") String networkType,
                        @Param("ipAddress") String ipAddress);
    
    /**
     * 更新位置信息
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserOnlineStatusEntity u SET " +
           "u.location = :location, " +
           "u.latitude = :latitude, " +
           "u.longitude = :longitude " +
           "WHERE u.userId = :userId")
    int updateLocationInfo(@Param("userId") Long userId,
                          @Param("location") String location,
                          @Param("latitude") Double latitude,
                          @Param("longitude") Double longitude);
    
    /**
     * 更新通话状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserOnlineStatusEntity u SET " +
           "u.inCall = :inCall, " +
           "u.inVideoCall = :inVideoCall " +
           "WHERE u.userId = :userId")
    int updateCallStatus(@Param("userId") Long userId,
                        @Param("inCall") Boolean inCall,
                        @Param("inVideoCall") Boolean inVideoCall);
    
    /**
     * 设置勿扰模式
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserOnlineStatusEntity u SET " +
           "u.doNotDisturb = :doNotDisturb, " +
           "u.quietHoursStart = :quietHoursStart, " +
           "u.quietHoursEnd = :quietHoursEnd " +
           "WHERE u.userId = :userId")
    int setDoNotDisturb(@Param("userId") Long userId,
                       @Param("doNotDisturb") Boolean doNotDisturb,
                       @Param("quietHoursStart") String quietHoursStart,
                       @Param("quietHoursEnd") String quietHoursEnd);
    
    /**
     * 查询在线用户数量
     */
    @Query("SELECT COUNT(u) FROM UserOnlineStatusEntity u WHERE u.isOnline = true")
    Long countOnlineUsers();
    
    /**
     * 查询指定状态的用户数量
     */
    @Query("SELECT COUNT(u) FROM UserOnlineStatusEntity u WHERE u.status = :status")
    Long countUsersByStatus(@Param("status") UserOnlineStatusEntity.OnlineStatus status);
}
