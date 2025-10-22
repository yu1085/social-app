package com.socialmeet.backend.service;

import com.socialmeet.backend.dto.IntimacyActionRequest;
import com.socialmeet.backend.dto.IntimacyDTO;
import com.socialmeet.backend.dto.IntimacyRewardDTO;
import com.socialmeet.backend.dto.UserDTO;
import com.socialmeet.backend.entity.*;
import com.socialmeet.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntimacyService {

    private final UserIntimacyRepository userIntimacyRepository;
    private final IntimacyLevelRepository intimacyLevelRepository;
    private final IntimacyLogRepository intimacyLogRepository;
    private final IntimacyRewardRepository intimacyRewardRepository;
    private final UserRepository userRepository;

    /**
     * 记录亲密度行为并更新温度
     * 规则：每消耗10聊币 = +1°C
     */
    @Transactional
    public IntimacyDTO recordAction(Long userId, IntimacyActionRequest request) {
        log.info("记录亲密度行为 - 用户:{}, 目标:{}, 类型:{}, 聊币:{}",
                userId, request.getTargetUserId(), request.getActionType(), request.getCoinsSpent());

        // 1. 获取或创建亲密度记录
        UserIntimacy intimacy = userIntimacyRepository
                .findByUserIdAndTargetUserId(userId, request.getTargetUserId())
                .orElseGet(() -> createNewIntimacy(userId, request.getTargetUserId()));

        // 2. 记录变化前的状态
        int beforeTemp = intimacy.getCurrentTemperature();
        int beforeLevel = intimacy.getCurrentLevel();

        // 3. 计算温度增加值：每10聊币 = +1°C
        int tempIncrease = request.getCoinsSpent() / 10;

        // 4. 更新温度和统计数据
        intimacy.setCurrentTemperature(beforeTemp + tempIncrease);
        intimacy.setTotalCoinsSpent(intimacy.getTotalCoinsSpent() + request.getCoinsSpent());
        intimacy.setLastInteractionDate(LocalDate.now());

        // 更新相识天数
        if (intimacy.getFirstInteractionDate() != null) {
            long days = ChronoUnit.DAYS.between(intimacy.getFirstInteractionDate(), LocalDate.now());
            intimacy.setDaysKnown((int) days);
        }

        // 根据行为类型更新统计
        updateActionStats(intimacy, request);

        // 5. 检查并更新等级
        IntimacyLevel newLevel = intimacyLevelRepository
                .findTopByRequiredTemperatureLessThanEqualOrderByRequiredTemperatureDesc(
                        intimacy.getCurrentTemperature());

        boolean levelUp = false;
        int afterLevel = beforeLevel;

        if (newLevel != null && newLevel.getLevel() > beforeLevel) {
            afterLevel = newLevel.getLevel();
            intimacy.setCurrentLevel(afterLevel);
            levelUp = true;
            log.info("用户{}与{}的亲密度升级: {} -> {}", userId, request.getTargetUserId(),
                    beforeLevel, afterLevel);

            // 创建奖励记录
            createRewardIfNeeded(userId, request.getTargetUserId(), afterLevel, newLevel);
        }

        // 6. 保存亲密度记录
        intimacy = userIntimacyRepository.save(intimacy);

        // 7. 记录日志
        IntimacyLog log = new IntimacyLog();
        log.setUserId(userId);
        log.setTargetUserId(request.getTargetUserId());
        log.setActionType(request.getActionType());
        log.setTemperatureChange(tempIncrease);
        log.setCoinsSpent(request.getCoinsSpent());
        log.setBeforeTemperature(beforeTemp);
        log.setAfterTemperature(intimacy.getCurrentTemperature());
        log.setBeforeLevel(beforeLevel);
        log.setAfterLevel(afterLevel);
        log.setLevelUp(levelUp);
        intimacyLogRepository.save(log);

        // 8. 返回DTO
        return convertToDTO(intimacy);
    }

    /**
     * 创建新的亲密度记录
     */
    private UserIntimacy createNewIntimacy(Long userId, Long targetUserId) {
        UserIntimacy intimacy = new UserIntimacy();
        intimacy.setUserId(userId);
        intimacy.setTargetUserId(targetUserId);
        intimacy.setCurrentTemperature(0);
        intimacy.setCurrentLevel(1); // 默认等级1: 相遇
        intimacy.setFirstInteractionDate(LocalDate.now());
        intimacy.setLastInteractionDate(LocalDate.now());
        intimacy.setDaysKnown(0);
        return intimacy;
    }

    /**
     * 根据行为类型更新统计数据
     */
    private void updateActionStats(UserIntimacy intimacy, IntimacyActionRequest request) {
        switch (request.getActionType()) {
            case "MESSAGE":
                intimacy.setMessageCount(intimacy.getMessageCount() +
                        (request.getActionCount() != null ? request.getActionCount() : 1));
                break;
            case "GIFT":
                intimacy.setGiftCount(intimacy.getGiftCount() +
                        (request.getActionCount() != null ? request.getActionCount() : 1));
                break;
            case "VIDEO_CALL":
                intimacy.setVideoCallMinutes(intimacy.getVideoCallMinutes() +
                        (request.getActionCount() != null ? request.getActionCount() : 0));
                break;
            case "VOICE_CALL":
                intimacy.setVoiceCallMinutes(intimacy.getVoiceCallMinutes() +
                        (request.getActionCount() != null ? request.getActionCount() : 0));
                break;
        }
    }

    /**
     * 创建奖励记录
     */
    private void createRewardIfNeeded(Long userId, Long targetUserId, Integer level, IntimacyLevel levelConfig) {
        // 检查是否已存在该等级的奖励
        if (intimacyRewardRepository.existsByUserIdAndTargetUserIdAndLevel(userId, targetUserId, level)) {
            return;
        }

        // 如果等级有奖励，创建奖励记录
        if (levelConfig.getRewardType() != null) {
            IntimacyReward reward = new IntimacyReward();
            reward.setUserId(userId);
            reward.setTargetUserId(targetUserId);
            reward.setLevel(level);
            reward.setRewardType(levelConfig.getRewardType());
            reward.setRewardValue(levelConfig.getRewardValue());
            reward.setIsClaimed(false);
            intimacyRewardRepository.save(reward);
            log.info("创建亲密度奖励 - 用户:{}, 等级:{}, 类型:{}", userId, level, levelConfig.getRewardType());
        }
    }

    /**
     * 查询用户的亲密度列表（按温度降序）
     */
    public List<IntimacyDTO> getIntimacyList(Long userId, Integer limit) {
        Pageable pageable = limit != null ? PageRequest.of(0, limit) : Pageable.unpaged();

        List<UserIntimacy> intimacies;
        if (limit != null) {
            intimacies = userIntimacyRepository.findTopIntimacyUsers(userId, pageable);
        } else {
            intimacies = userIntimacyRepository.findByUserIdOrderByCurrentTemperatureDesc(userId);
        }

        return intimacies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 查询两个用户之间的亲密度
     */
    public IntimacyDTO getIntimacy(Long userId, Long targetUserId) {
        return userIntimacyRepository.findByUserIdAndTargetUserId(userId, targetUserId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * 查询用户的未领取奖励列表
     */
    public List<IntimacyRewardDTO> getUnclaimedRewards(Long userId) {
        List<IntimacyReward> rewards = intimacyRewardRepository.findByUserIdAndIsClaimedFalse(userId);
        return rewards.stream()
                .map(this::convertRewardToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 领取奖励
     */
    @Transactional
    public IntimacyRewardDTO claimReward(Long userId, Long rewardId) {
        IntimacyReward reward = intimacyRewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("奖励不存在"));

        if (!reward.getUserId().equals(userId)) {
            throw new RuntimeException("无权限领取该奖励");
        }

        if (reward.getIsClaimed()) {
            throw new RuntimeException("奖励已领取");
        }

        reward.setIsClaimed(true);
        reward.setClaimedAt(java.time.LocalDateTime.now());
        reward = intimacyRewardRepository.save(reward);

        log.info("用户{}领取亲密度奖励: {}", userId, rewardId);
        return convertRewardToDTO(reward);
    }

    /**
     * 查询所有等级配置
     */
    public List<IntimacyLevel> getAllLevels() {
        return intimacyLevelRepository.findAllByOrderByLevelAsc();
    }

    /**
     * 将UserIntimacy转换为IntimacyDTO
     */
    private IntimacyDTO convertToDTO(UserIntimacy intimacy) {
        IntimacyDTO dto = new IntimacyDTO();
        dto.setTargetUserId(intimacy.getTargetUserId());
        dto.setCurrentTemperature(intimacy.getCurrentTemperature());
        dto.setCurrentLevel(intimacy.getCurrentLevel());
        dto.setMessageCount(intimacy.getMessageCount());
        dto.setGiftCount(intimacy.getGiftCount());
        dto.setVideoCallMinutes(intimacy.getVideoCallMinutes());
        dto.setVoiceCallMinutes(intimacy.getVoiceCallMinutes());
        dto.setTotalCoinsSpent(intimacy.getTotalCoinsSpent());
        dto.setFirstInteractionDate(intimacy.getFirstInteractionDate());
        dto.setLastInteractionDate(intimacy.getLastInteractionDate());
        dto.setDaysKnown(intimacy.getDaysKnown());

        // 设置当前等级信息
        intimacyLevelRepository.findByLevel(intimacy.getCurrentLevel()).ifPresent(level -> {
            dto.setCurrentLevelName(level.getLevelName());
        });

        // 设置下一等级信息
        intimacyLevelRepository.findByLevel(intimacy.getCurrentLevel() + 1).ifPresent(nextLevel -> {
            dto.setNextLevelTemperature(nextLevel.getRequiredTemperature());
            dto.setNextLevelName(nextLevel.getLevelName());
            dto.setMaxLevel(false);
        });

        // 如果没有下一等级，说明已达到最高等级
        if (dto.getNextLevelTemperature() == null) {
            dto.setMaxLevel(true);
        }

        // 加载目标用户信息
        userRepository.findById(intimacy.getTargetUserId()).ifPresent(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setNickname(user.getNickname());
            userDTO.setAvatarUrl(user.getAvatarUrl());
            userDTO.setGender(user.getGender() != null ? user.getGender().name() : null);
            dto.setTargetUser(userDTO);
        });

        return dto;
    }

    /**
     * 将IntimacyReward转换为IntimacyRewardDTO
     */
    private IntimacyRewardDTO convertRewardToDTO(IntimacyReward reward) {
        IntimacyRewardDTO dto = new IntimacyRewardDTO();
        dto.setId(reward.getId());
        dto.setTargetUserId(reward.getTargetUserId());
        dto.setLevel(reward.getLevel());
        dto.setRewardType(reward.getRewardType());
        dto.setRewardValue(reward.getRewardValue());
        dto.setIsClaimed(reward.getIsClaimed());
        if (reward.getClaimedAt() != null) {
            dto.setClaimedAt(reward.getClaimedAt().toString());
        }

        // 设置等级名称
        intimacyLevelRepository.findByLevel(reward.getLevel()).ifPresent(level -> {
            dto.setLevelName(level.getLevelName());
        });

        return dto;
    }
}
