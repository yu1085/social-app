package com.socialmeet.backend.service;

import com.socialmeet.backend.entity.UserDevice;
import com.socialmeet.backend.repository.UserDeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户设备管理服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;

    /**
     * 注册或更新用户设备
     */
    @Transactional
    public UserDevice registerOrUpdateDevice(Long userId, String registrationId, 
                                           String deviceName, String deviceType) {
        try {
            // 查找是否已存在该设备
            Optional<UserDevice> existingDevice = userDeviceRepository
                    .findByUserIdAndRegistrationId(userId, registrationId);

            if (existingDevice.isPresent()) {
                // 更新现有设备
                UserDevice device = existingDevice.get();
                device.setDeviceName(deviceName);
                device.setDeviceType(deviceType);
                device.setIsActive(true);
                device.setLastActiveAt(LocalDateTime.now());
                
                device = userDeviceRepository.save(device);
                log.info("✅ 设备信息已更新 - userId: {}, registrationId: {}", userId, registrationId);
                return device;
            } else {
                // 创建新设备
                UserDevice newDevice = new UserDevice();
                newDevice.setUserId(userId);
                newDevice.setRegistrationId(registrationId);
                newDevice.setDeviceName(deviceName);
                newDevice.setDeviceType(deviceType);
                newDevice.setIsActive(true);
                newDevice.setLastActiveAt(LocalDateTime.now());
                
                newDevice = userDeviceRepository.save(newDevice);
                log.info("✅ 新设备已注册 - userId: {}, registrationId: {}", userId, registrationId);
                return newDevice;
            }
        } catch (Exception e) {
            log.error("注册设备失败 - userId: {}, registrationId: {}", userId, registrationId, e);
            throw new RuntimeException("设备注册失败", e);
        }
    }

    /**
     * 获取用户所有活跃设备
     */
    public List<UserDevice> getActiveDevices(Long userId) {
        return userDeviceRepository.findActiveDevicesByUserId(userId);
    }

    /**
     * 获取用户所有活跃设备的Registration ID列表
     */
    public List<String> getActiveRegistrationIds(Long userId) {
        return userDeviceRepository.findActiveDevicesByUserId(userId)
                .stream()
                .map(UserDevice::getRegistrationId)
                .toList();
    }

    /**
     * 停用设备
     */
    @Transactional
    public void deactivateDevice(Long userId, String registrationId) {
        Optional<UserDevice> device = userDeviceRepository
                .findByUserIdAndRegistrationId(userId, registrationId);
        
        if (device.isPresent()) {
            device.get().setIsActive(false);
            userDeviceRepository.save(device.get());
            log.info("✅ 设备已停用 - userId: {}, registrationId: {}", userId, registrationId);
        }
    }

    /**
     * 用户登出时停用所有设备
     */
    @Transactional
    public void deactivateAllDevices(Long userId) {
        List<UserDevice> devices = userDeviceRepository.findActiveDevicesByUserId(userId);
        for (UserDevice device : devices) {
            device.setIsActive(false);
        }
        userDeviceRepository.saveAll(devices);
        log.info("✅ 用户所有设备已停用 - userId: {}", userId);
    }

    /**
     * 清理过期设备（超过30天未活跃）
     */
    @Transactional
    public void cleanupInactiveDevices() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        List<UserDevice> inactiveDevices = userDeviceRepository.findAll()
                .stream()
                .filter(device -> device.getLastActiveAt() != null && 
                                device.getLastActiveAt().isBefore(cutoffDate))
                .toList();
        
        for (UserDevice device : inactiveDevices) {
            device.setIsActive(false);
        }
        userDeviceRepository.saveAll(inactiveDevices);
        log.info("✅ 已清理 {} 个过期设备", inactiveDevices.size());
    }
}
