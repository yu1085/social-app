package com.socialmeet.backend.service;

import com.socialmeet.backend.dto.*;
import com.socialmeet.backend.entity.User;
import com.socialmeet.backend.entity.UserSettings;
import com.socialmeet.backend.entity.Wallet;
import com.socialmeet.backend.repository.UserRepository;
import com.socialmeet.backend.repository.UserSettingsRepository;
import com.socialmeet.backend.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 个人资料服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final WalletRepository walletRepository;
    private final WealthService wealthService;

    /**
     * 获取用户完整资料信息
     */
    public Map<String, Object> getUserProfile(Long userId) {
        log.info("获取用户完整资料 - userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Map<String, Object> profile = new HashMap<>();
        
        // 基本信息
        profile.put("user", UserDTO.fromEntity(user));
        
        // VIP信息
        profile.put("vipInfo", getVipInfo(user));
        
        // 钱包信息
        profile.put("wallet", getWalletInfo(userId));
        
        // 用户设置
        profile.put("settings", getUserSettings(userId));

        return profile;
    }

    /**
     * 更新用户资料
     */
    @Transactional
    public UserDTO updateProfile(Long userId, ProfileUpdateRequest request) {
        log.info("更新用户资料 - userId: {}, request: {}", userId, request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 更新允许修改的字段
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            user.setNickname(request.getNickname().trim());
        }
        if (request.getGender() != null && !request.getGender().trim().isEmpty()) {
            try {
                user.setGender(User.Gender.valueOf(request.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("无效的性别参数");
            }
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }
        if (request.getSignature() != null) {
            user.setSignature(request.getSignature());
        }
        if (request.getHeight() != null) {
            user.setHeight(request.getHeight());
        }
        if (request.getWeight() != null) {
            user.setWeight(request.getWeight());
        }
        if (request.getIncomeLevel() != null) {
            user.setIncomeLevel(request.getIncomeLevel());
        }
        if (request.getEducation() != null) {
            user.setEducation(request.getEducation());
        }
        if (request.getMaritalStatus() != null) {
            user.setMaritalStatus(request.getMaritalStatus());
        }

        user = userRepository.save(user);
        log.info("用户资料更新成功 - userId: {}", userId);
        
        return UserDTO.fromEntity(user);
    }

    /**
     * 获取VIP信息
     */
    public VipInfoDTO getVipInfo(User user) {
        VipInfoDTO vipInfo = new VipInfoDTO();
        vipInfo.setIsVip(user.getIsVip());
        vipInfo.setVipLevel(user.getVipLevel());
        vipInfo.setVipExpireAt(user.getVipExpireAt());

        if (user.getIsVip() && user.getVipExpireAt() != null) {
            long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), user.getVipExpireAt());
            vipInfo.setRemainingDays(Math.max(0, remainingDays));
        } else {
            vipInfo.setRemainingDays(0L);
        }

        // 设置VIP等级名称和权益
        vipInfo.setVipLevelName(getVipLevelName(user.getVipLevel()));
        vipInfo.setVipBenefits(getVipBenefits(user.getVipLevel()));
        vipInfo.setCanUpgrade(canUpgradeVip(user.getVipLevel()));
        vipInfo.setNextLevelName(getNextVipLevelName(user.getVipLevel()));
        vipInfo.setNextLevelRequirement(getNextVipLevelRequirement(user.getVipLevel()));

        // 添加财富值相关信息
        try {
            WealthLevelDataDTO wealthLevel = wealthService.getCurrentWealthLevel(user.getId());
            vipInfo.setWealthValue(wealthLevel.getWealthValue());
            vipInfo.setWealthLevelName(wealthLevel.getLevelName());
            vipInfo.setWealthLevelId(wealthLevel.getLevelId());
            vipInfo.setWealthThresholdForBenefits(wealthLevel.getNextLevelRequirement());
            vipInfo.setCurrentLevelStatus(wealthLevel.getWealthValue() >= (wealthLevel.getNextLevelRequirement() != null ? wealthLevel.getNextLevelRequirement() : 0) ? "已达成" : "未达成");
            vipInfo.setLevelProgression(wealthService.getWealthLevelProgress(user.getId()));
            vipInfo.setCurrentLevelBenefits(wealthService.getVipBenefits(user.getId()));
        } catch (Exception e) {
            log.error("获取财富值信息失败", e);
            // 设置默认值
            vipInfo.setWealthValue(0);
            vipInfo.setWealthLevelName("青铜");
            vipInfo.setWealthLevelId(1);
            vipInfo.setWealthThresholdForBenefits(2000);
            vipInfo.setCurrentLevelStatus("未达成");
        }

        return vipInfo;
    }

    /**
     * 获取钱包信息
     */
    public WalletDTO getWalletInfo(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> createWallet(userId));

        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setUserId(wallet.getUserId());
        walletDTO.setBalance(wallet.getBalance());
        walletDTO.setTotalRecharge(wallet.getTotalRecharge());
        walletDTO.setTotalConsume(wallet.getTotalConsume());
        walletDTO.setTransactionCount(wallet.getTransactionCount());
        walletDTO.setLastTransactionAt(wallet.getLastTransactionAt());
        walletDTO.setCreatedAt(wallet.getCreatedAt());
        walletDTO.setUpdatedAt(wallet.getUpdatedAt());

        return walletDTO;
    }

    /**
     * 获取用户设置
     */
    public UserSettingsDTO getUserSettings(Long userId) {
        UserSettings settings = userSettingsRepository.findByUserId(userId)
                .orElseGet(() -> createUserSettings(userId));

        UserSettingsDTO settingsDTO = new UserSettingsDTO();
        settingsDTO.setVoiceCallEnabled(settings.getVoiceCallEnabled());
        settingsDTO.setVideoCallEnabled(settings.getVideoCallEnabled());
        settingsDTO.setMessageChargeEnabled(settings.getMessageChargeEnabled());
        settingsDTO.setVoiceCallPrice(settings.getVoiceCallPrice().doubleValue());
        settingsDTO.setVideoCallPrice(settings.getVideoCallPrice().doubleValue());
        settingsDTO.setMessagePrice(settings.getMessagePrice().doubleValue());

        return settingsDTO;
    }

    /**
     * 更新用户设置
     */
    @Transactional
    public UserSettingsDTO updateUserSettings(Long userId, UserSettingsDTO settingsDTO) {
        log.info("更新用户设置 - userId: {}, settings: {}", userId, settingsDTO);

        UserSettings settings = userSettingsRepository.findByUserId(userId)
                .orElseGet(() -> createUserSettings(userId));

        if (settingsDTO.getVoiceCallEnabled() != null) {
            settings.setVoiceCallEnabled(settingsDTO.getVoiceCallEnabled());
        }
        if (settingsDTO.getVideoCallEnabled() != null) {
            settings.setVideoCallEnabled(settingsDTO.getVideoCallEnabled());
        }
        if (settingsDTO.getMessageChargeEnabled() != null) {
            settings.setMessageChargeEnabled(settingsDTO.getMessageChargeEnabled());
        }
        if (settingsDTO.getVoiceCallPrice() != null) {
            settings.setVoiceCallPrice(BigDecimal.valueOf(settingsDTO.getVoiceCallPrice()));
        }
        if (settingsDTO.getVideoCallPrice() != null) {
            settings.setVideoCallPrice(BigDecimal.valueOf(settingsDTO.getVideoCallPrice()));
        }
        if (settingsDTO.getMessagePrice() != null) {
            settings.setMessagePrice(BigDecimal.valueOf(settingsDTO.getMessagePrice()));
        }

        settings = userSettingsRepository.save(settings);
        log.info("用户设置更新成功 - userId: {}", userId);

        return getUserSettings(userId);
    }

    /**
     * 创建钱包
     */
    private Wallet createWallet(Long userId) {
        log.info("创建用户钱包 - userId: {}", userId);
        
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setTotalRecharge(BigDecimal.ZERO);
        wallet.setTotalConsume(BigDecimal.ZERO);
        wallet.setTransactionCount(0);
        
        return walletRepository.save(wallet);
    }

    /**
     * 创建用户设置
     */
    private UserSettings createUserSettings(Long userId) {
        log.info("创建用户设置 - userId: {}", userId);
        
        UserSettings settings = new UserSettings();
        settings.setUserId(userId);
        settings.setVoiceCallEnabled(true);
        settings.setVideoCallEnabled(true);
        settings.setMessageChargeEnabled(false);
        settings.setVoiceCallPrice(BigDecimal.ZERO);
        settings.setVideoCallPrice(BigDecimal.ZERO);
        settings.setMessagePrice(BigDecimal.ZERO);
        
        return userSettingsRepository.save(settings);
    }

    /**
     * 获取VIP等级名称
     */
    private String getVipLevelName(Integer vipLevel) {
        if (vipLevel == null || vipLevel == 0) {
            return "普通用户";
        }
        return switch (vipLevel) {
            case 1 -> "VIP1";
            case 2 -> "VIP2";
            case 3 -> "VIP3";
            case 4 -> "VIP4";
            case 5 -> "VIP5";
            default -> "VIP" + vipLevel;
        };
    }

    /**
     * 获取VIP权益
     */
    private String getVipBenefits(Integer vipLevel) {
        if (vipLevel == null || vipLevel == 0) {
            return "无特殊权益";
        }
        return switch (vipLevel) {
            case 1 -> "专属客服、优先匹配";
            case 2 -> "专属客服、优先匹配、免费通话";
            case 3 -> "专属客服、优先匹配、免费通话、专属标识";
            case 4 -> "专属客服、优先匹配、免费通话、专属标识、高级筛选";
            case 5 -> "专属客服、优先匹配、免费通话、专属标识、高级筛选、无限匹配";
            default -> "专属权益";
        };
    }

    /**
     * 是否可以升级VIP
     */
    private Boolean canUpgradeVip(Integer vipLevel) {
        return vipLevel == null || vipLevel < 5;
    }

    /**
     * 获取下一级VIP等级名称
     */
    private String getNextVipLevelName(Integer vipLevel) {
        if (vipLevel == null || vipLevel >= 5) {
            return null;
        }
        return "VIP" + (vipLevel + 1);
    }

    /**
     * 获取下一级VIP等级要求
     */
    private Integer getNextVipLevelRequirement(Integer vipLevel) {
        if (vipLevel == null || vipLevel >= 5) {
            return null;
        }
        return switch (vipLevel) {
            case 0 -> 100; // 升级到VIP1需要100元
            case 1 -> 300; // 升级到VIP2需要300元
            case 2 -> 600; // 升级到VIP3需要600元
            case 3 -> 1000; // 升级到VIP4需要1000元
            case 4 -> 2000; // 升级到VIP5需要2000元
            default -> null;
        };
    }

    /**
     * 获取用户统计信息
     */
    public Map<String, Object> getUserStats(Long userId) {
        log.info("获取用户统计信息 - userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Map<String, Object> stats = new HashMap<>();
        
        // 基本信息统计
        stats.put("userId", user.getId());
        stats.put("username", user.getUsername());
        stats.put("nickname", user.getNickname());
        stats.put("isOnline", user.getIsOnline());
        stats.put("isVerified", user.getIsVerified());
        stats.put("isVip", user.getIsVip());
        stats.put("vipLevel", user.getVipLevel());
        stats.put("wealthLevel", user.getWealthLevel());
        
        // 注册时间统计
        stats.put("registerDays", ChronoUnit.DAYS.between(user.getCreatedAt(), LocalDateTime.now()));
        stats.put("lastActiveAt", user.getLastActiveAt());
        
        // 钱包统计
        Wallet wallet = walletRepository.findByUserId(userId).orElse(null);
        if (wallet != null) {
            stats.put("balance", wallet.getBalance());
            stats.put("totalRecharge", wallet.getTotalRecharge());
            stats.put("totalConsume", wallet.getTotalConsume());
            stats.put("transactionCount", wallet.getTransactionCount());
        } else {
            stats.put("balance", BigDecimal.ZERO);
            stats.put("totalRecharge", BigDecimal.ZERO);
            stats.put("totalConsume", BigDecimal.ZERO);
            stats.put("transactionCount", 0);
        }

        return stats;
    }
}
