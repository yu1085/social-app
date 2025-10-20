package com.socialmeet.backend.service;

import com.socialmeet.backend.dto.LevelStatusDTO;
import com.socialmeet.backend.dto.VipBenefitDTO;
import com.socialmeet.backend.dto.WealthLevelDataDTO;
import com.socialmeet.backend.entity.Transaction;
import com.socialmeet.backend.entity.User;
import com.socialmeet.backend.entity.WealthLevel;
import com.socialmeet.backend.repository.TransactionRepository;
import com.socialmeet.backend.repository.UserRepository;
import com.socialmeet.backend.repository.WealthLevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 财富值服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WealthService {
    
    private final TransactionRepository transactionRepository;
    private final WealthLevelRepository wealthLevelRepository;
    private final UserRepository userRepository;
    
    /**
     * 计算用户财富值
     */
    public Integer calculateWealthValue(Long userId) {
        log.info("计算用户财富值 - userId: {}", userId);
        
        Integer totalWealthValue = transactionRepository.calculateTotalWealthValue(userId);
        log.info("用户 {} 总财富值: {}", userId, totalWealthValue);
        
        return totalWealthValue;
    }
    
    /**
     * 获取用户当前财富等级
     */
    public WealthLevelDataDTO getCurrentWealthLevel(Long userId) {
        log.info("获取用户当前财富等级 - userId: {}", userId);
        
        Integer wealthValue = calculateWealthValue(userId);
        Optional<WealthLevel> currentLevel = wealthLevelRepository.findByWealthValue(wealthValue);
        
        if (currentLevel.isPresent()) {
            WealthLevel level = currentLevel.get();
            Optional<WealthLevel> nextLevel = wealthLevelRepository.findNextLevel(wealthValue);
            
            return new WealthLevelDataDTO(
                level.getLevelName(),
                level.getLevelId(),
                wealthValue,
                nextLevel.map(WealthLevel::getMinWealthValue).orElse(null),
                level.getLevelDescription(),
                level.getLevelIcon(),
                getLevelColor(level.getLevelId()),
                level.getIsMaxLevel()
            );
        }
        
        // 如果没有找到等级，返回默认等级
        return new WealthLevelDataDTO("青铜", 1, wealthValue, 2000, "青铜等级", "bronze_icon", "#CD7F32", false);
    }
    
    /**
     * 获取财富等级进度
     */
    public List<LevelStatusDTO> getWealthLevelProgress(Long userId) {
        log.info("获取财富等级进度 - userId: {}", userId);
        
        Integer wealthValue = calculateWealthValue(userId);
        List<WealthLevel> allLevels = wealthLevelRepository.findAllByOrderBySortOrderAsc();
        List<LevelStatusDTO> progress = new ArrayList<>();
        
        for (WealthLevel level : allLevels) {
            boolean isCurrent = wealthValue >= level.getMinWealthValue() && 
                               (level.getMaxWealthValue() == null || wealthValue <= level.getMaxWealthValue());
            boolean isLocked = wealthValue < level.getMinWealthValue();
            
            progress.add(new LevelStatusDTO(
                level.getLevelName(),
                level.getLevelId(),
                isCurrent,
                isLocked,
                level.getLevelIcon(),
                getLevelColor(level.getLevelId()),
                level.getSortOrder()
            ));
        }
        
        return progress;
    }
    
    /**
     * 获取VIP特权列表
     */
    public List<VipBenefitDTO> getVipBenefits(Long userId) {
        log.info("获取VIP特权列表 - userId: {}", userId);
        
        WealthLevelDataDTO currentLevel = getCurrentWealthLevel(userId);
        List<VipBenefitDTO> benefits = new ArrayList<>();
        
        // 根据等级返回不同的特权
        String levelName = currentLevel.getLevelName();
        Integer levelId = currentLevel.getLevelId();
        
        // 青铜及以上：靓号购买折扣
        if (levelId >= 1) {
            benefits.add(new VipBenefitDTO(
                "靓号购买折扣", "购买靓号享受折扣优惠", "id_icon", 
                "8折", new BigDecimal("0.8"), "discount", false, 1
            ));
        }
        
        // 青铜及以上：每周促销
        if (levelId >= 1) {
            benefits.add(new VipBenefitDTO(
                "每周促销", "每周限时促销活动", "sale_icon", 
                null, null, "promotion", false, 2
            ));
        }
        
        // 白银及以上：会员购买折扣
        if (levelId >= 2) {
            benefits.add(new VipBenefitDTO(
                "会员购买折扣", "购买VIP/SVIP享受折扣", "vip_icon", 
                "9折", new BigDecimal("0.9"), "discount", false, 3
            ));
        }
        
        // 白银及以上：进场特效折扣
        if (levelId >= 2) {
            benefits.add(new VipBenefitDTO(
                "进场特效折扣", "购买进场特效享受折扣", "car_icon", 
                "8折", new BigDecimal("0.8"), "discount", false, 4
            ));
        }
        
        // 青钻及以上：专属服务
        if (levelId >= 6) {
            benefits.add(new VipBenefitDTO(
                "专属客服特权", "享受专属客服服务", "service_icon", 
                null, null, "privilege", false, 5
            ));
        }
        
        // 橙钻及以上：专属进场特效
        if (levelId >= 8) {
            benefits.add(new VipBenefitDTO(
                "专属进场特效", "专属进场特效购买特权", "exclusive_car_icon", 
                null, null, "privilege", false, 6
            ));
        }
        
        // 橙钻及以上：靓号定制特权
        if (levelId >= 8) {
            benefits.add(new VipBenefitDTO(
                "靓号定制特权", "定制专属靓号，有效期30天", "custom_id_icon", 
                "8800聊币", null, "privilege", false, 7
            ));
        }
        
        // 橙钻及以上：专属礼物
        if (levelId >= 8) {
            benefits.add(new VipBenefitDTO(
                "专属礼物特权", "赠送专属礼物特权", "gift_icon", 
                null, null, "privilege", false, 8
            ));
        }
        
        return benefits;
    }
    
    /**
     * 记录交易并计算财富值
     */
    @Transactional
    public void recordTransaction(Long userId, Transaction.TransactionType transactionType, 
                                Transaction.CoinSource coinSource, BigDecimal amount, 
                                Integer coinAmount, String description, String orderId) {
        log.info("记录交易 - userId: {}, type: {}, source: {}, amount: {}, coins: {}", 
                userId, transactionType, coinSource, amount, coinAmount);
        
        // 计算财富值（只有购买的聊币才计算）
        Integer wealthValue = 0;
        if (coinSource == Transaction.CoinSource.PURCHASED) {
            wealthValue = coinAmount / 100; // 每100聊币 = 1财富值
        }
        
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setTransactionType(transactionType);
        transaction.setCoinSource(coinSource);
        transaction.setAmount(amount);
        transaction.setCoinAmount(BigDecimal.valueOf(coinAmount));
        transaction.setWealthValue(wealthValue);
        transaction.setDescription(description);
        transaction.setOrderId(orderId);
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
        
        transactionRepository.save(transaction);
        
        // 更新用户财富等级
        updateUserWealthLevel(userId);
        
        log.info("交易记录成功 - 财富值: {}", wealthValue);
    }
    
    /**
     * 更新用户财富等级
     */
    @Transactional
    public void updateUserWealthLevel(Long userId) {
        log.info("更新用户财富等级 - userId: {}", userId);
        
        Integer wealthValue = calculateWealthValue(userId);
        Optional<WealthLevel> currentLevel = wealthLevelRepository.findByWealthValue(wealthValue);
        
        if (currentLevel.isPresent()) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setWealthLevel(currentLevel.get().getLevelId());
                userRepository.save(user);
                log.info("用户 {} 财富等级更新为: {}", userId, currentLevel.get().getLevelName());
            }
        }
    }
    
    /**
     * 根据等级ID获取等级颜色
     */
    private String getLevelColor(Integer levelId) {
        if (levelId == null) return "#CD7F32"; // 默认青铜色
        
        switch (levelId) {
            case 1: return "#CD7F32"; // 青铜
            case 2: return "#C0C0C0"; // 白银
            case 3: return "#FFD700"; // 黄金
            case 4: return "#E5E4E2"; // 铂金
            case 5: return "#00CED1"; // 青钻
            case 6: return "#4169E1"; // 蓝钻
            case 7: return "#9932CC"; // 紫钻
            case 8: return "#FF8C00"; // 橙钻
            case 9: return "#DC143C"; // 红钻
            case 10: return "#FFD700"; // 金钻
            case 11: return "#2F2F2F"; // 黑钻
            default: return "#CD7F32"; // 默认青铜色
        }
    }
}
