package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户设备数据访问层
 */
@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    /**
     * 根据用户ID查找所有活跃设备
     */
    @Query("SELECT ud FROM UserDevice ud WHERE ud.userId = :userId AND ud.isActive = true")
    List<UserDevice> findActiveDevicesByUserId(@Param("userId") Long userId);

    /**
     * 根据Registration ID查找设备
     */
    Optional<UserDevice> findByRegistrationId(String registrationId);

    /**
     * 根据用户ID和Registration ID查找设备
     */
    Optional<UserDevice> findByUserIdAndRegistrationId(Long userId, String registrationId);

    /**
     * 根据用户ID查找所有设备（包括非活跃）
     */
    List<UserDevice> findByUserId(Long userId);

    /**
     * 统计用户活跃设备数量
     */
    @Query("SELECT COUNT(ud) FROM UserDevice ud WHERE ud.userId = :userId AND ud.isActive = true")
    long countActiveDevicesByUserId(@Param("userId") Long userId);
}
