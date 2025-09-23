package com.example.socialmeet.service;

import com.example.socialmeet.entity.WealthLevel;
import com.example.socialmeet.entity.WealthLevelRule;
import com.example.socialmeet.repository.WealthLevelRepository;
import com.example.socialmeet.repository.WealthLevelRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WealthLevelService {
    
    @Autowired
    private WealthLevelRepository wealthLevelRepository;
    
    @Autowired
    private WealthLevelRuleRepository wealthLevelRuleRepository;
    
    /**
     * 获取或创建用户的财富等级
     */
    public WealthLevel getOrCreateWealthLevel(Long userId) {
        Optional<WealthLevel> existingLevel = wealthLevelRepository.findByUserId(userId);
        if (existingLevel.isPresent()) {
            WealthLevel level = existingLevel.get();
            // 确保等级信息是最新的
            updateLevelInfoFromRules(level);
            return level;
        }
        
        // 创建新的财富等级记录
        WealthLevel newLevel = new WealthLevel(userId, 0);
        updateLevelInfoFromRules(newLevel);
        return wealthLevelRepository.save(newLevel);
    }
    
    /**
     * 根据规则更新等级信息
     */
    private void updateLevelInfoFromRules(WealthLevel wealthLevel) {
        Optional<WealthLevelRule> matchingRule = wealthLevelRuleRepository.findMatchingRule(wealthLevel.getWealthValue());
        if (matchingRule.isPresent()) {
            wealthLevel.updateLevelInfo(matchingRule.get());
        }
    }
    
    /**
     * 根据用户ID获取财富等级
     */
    public WealthLevel getUserWealthLevel(Long userId) {
        return getOrCreateWealthLevel(userId);
    }
    
    /**
     * 更新用户财富值
     * 根据规则：每成功购买100聊币，获得1财富值
     */
    public WealthLevel updateWealthValue(Long userId, BigDecimal rechargeAmount) {
        WealthLevel wealthLevel = getOrCreateWealthLevel(userId);
        
        // 计算新增的财富值：1元 = 100聊币 = 1财富值
        int newWealthValue = rechargeAmount.multiply(new BigDecimal(100)).intValue();
        int currentWealthValue = wealthLevel.getWealthValue();
        
        // 更新财富值
        wealthLevel.setWealthValue(currentWealthValue + newWealthValue);
        
        // 根据新财富值更新等级信息
        updateLevelInfoFromRules(wealthLevel);
        
        return wealthLevelRepository.save(wealthLevel);
    }
    
    /**
     * 检查用户是否有特定特权
     */
    public boolean hasPrivilege(Long userId, WealthLevel.PrivilegeType privilege) {
        WealthLevel wealthLevel = getUserWealthLevel(userId);
        return wealthLevel.hasPrivilege(privilege);
    }
    
    /**
     * 获取用户的所有特权
     */
    public List<WealthLevel.PrivilegeType> getUserPrivileges(Long userId) {
        WealthLevel wealthLevel = getUserWealthLevel(userId);
        List<WealthLevel.PrivilegeType> privileges = new java.util.ArrayList<>();
        
        for (WealthLevel.PrivilegeType privilege : WealthLevel.PrivilegeType.values()) {
            if (wealthLevel.hasPrivilege(privilege)) {
                privileges.add(privilege);
            }
        }
        
        return privileges;
    }
    
    /**
     * 获取用户特权名称列表（用于API返回）
     */
    public List<String> getUserPrivilegeNames(Long userId) {
        List<WealthLevel.PrivilegeType> privileges = getUserPrivileges(userId);
        return privileges.stream()
                .map(WealthLevel.PrivilegeType::getDescription)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 获取用户等级进度信息
     */
    public LevelProgressInfo getLevelProgress(Long userId) {
        WealthLevel wealthLevel = getUserWealthLevel(userId);
        
        LevelProgressInfo progressInfo = new LevelProgressInfo();
        progressInfo.setCurrentLevel(wealthLevel.getLevelName());
        progressInfo.setCurrentWealthValue(wealthLevel.getWealthValue());
        progressInfo.setCurrentLevelIcon(wealthLevel.getLevelIcon());
        progressInfo.setCurrentLevelColor(wealthLevel.getLevelColor());
        
        // 获取下一等级规则
        Optional<WealthLevelRule> nextRule = wealthLevelRuleRepository.findNextLevelRule(wealthLevel.getWealthValue());
        if (nextRule.isPresent()) {
            WealthLevelRule nextLevelRule = nextRule.get();
            progressInfo.setNextLevelRequirement(wealthLevel.getNextLevelRequirement(nextLevelRule));
            progressInfo.setNextLevelName(nextLevelRule.getLevelName());
            progressInfo.setNextLevelIcon(nextLevelRule.getLevelIcon());
            progressInfo.setNextLevelColor(nextLevelRule.getLevelColor());
            progressInfo.setProgressPercentage(wealthLevel.getLevelProgress(nextLevelRule));
        } else {
            progressInfo.setNextLevelRequirement(null);
            progressInfo.setNextLevelName("最高等级");
            progressInfo.setNextLevelIcon("👑");
            progressInfo.setNextLevelColor("#FFD700");
            progressInfo.setProgressPercentage(100.0);
        }
        
        return progressInfo;
    }
    
    /**
     * 获取财富排行榜
     */
    public List<WealthLevel> getWealthRanking(int limit) {
        return wealthLevelRepository.findTopNByOrderByWealthValueDesc(limit);
    }
    
    /**
     * 获取用户排名
     */
    public Long getUserRank(Long userId) {
        WealthLevel wealthLevel = getUserWealthLevel(userId);
        return wealthLevelRepository.getUserRank(wealthLevel.getWealthValue());
    }
    
    /**
     * 获取指定等级的用户列表
     */
    public List<WealthLevel> getUsersByLevel(String levelName) {
        return wealthLevelRepository.findByLevelNameOrderByWealthValueDesc(levelName);
    }
    
    /**
     * 获取特权用户列表
     */
    public List<WealthLevel> getPrivilegedUsers(WealthLevel.PrivilegeType privilege) {
        int minWealthValue = getMinWealthValueForPrivilege(privilege);
        return wealthLevelRepository.findPrivilegedUsers(minWealthValue);
    }
    
    /**
     * 获取特权所需的最小财富值
     */
    private int getMinWealthValueForPrivilege(WealthLevel.PrivilegeType privilege) {
        switch (privilege) {
            case LUCKY_NUMBER_DISCOUNT:
            case WEEKLY_PROMOTION:
                return 1000; // 青铜及以上
            case VIP_DISCOUNT:
            case EFFECT_DISCOUNT:
                return 2000; // 白银及以上
            case FREE_VIP:
            case FREE_EFFECT:
                return 500000; // 红钻及以上
            case EXCLUSIVE_EFFECT:
            case LUCKY_NUMBER_CUSTOM:
            case EXCLUSIVE_GIFT:
                return 300000; // 橙钻及以上
            case EXCLUSIVE_SERVICE:
                return 30000; // 青钻及以上
            default:
                return 0;
        }
    }
    
    /**
     * 等级进度信息类
     */
    public static class LevelProgressInfo {
        private String currentLevel;
        private Integer currentWealthValue;
        private String currentLevelIcon;
        private String currentLevelColor;
        private Double progressPercentage;
        private Integer nextLevelRequirement;
        private String nextLevelName;
        private String nextLevelIcon;
        private String nextLevelColor;
        
        // Getters and Setters
        public String getCurrentLevel() { return currentLevel; }
        public void setCurrentLevel(String currentLevel) { this.currentLevel = currentLevel; }
        
        public Integer getCurrentWealthValue() { return currentWealthValue; }
        public void setCurrentWealthValue(Integer currentWealthValue) { this.currentWealthValue = currentWealthValue; }
        
        public String getCurrentLevelIcon() { return currentLevelIcon; }
        public void setCurrentLevelIcon(String currentLevelIcon) { this.currentLevelIcon = currentLevelIcon; }
        
        public String getCurrentLevelColor() { return currentLevelColor; }
        public void setCurrentLevelColor(String currentLevelColor) { this.currentLevelColor = currentLevelColor; }
        
        public Double getProgressPercentage() { return progressPercentage; }
        public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }
        
        public Integer getNextLevelRequirement() { return nextLevelRequirement; }
        public void setNextLevelRequirement(Integer nextLevelRequirement) { this.nextLevelRequirement = nextLevelRequirement; }
        
        public String getNextLevelName() { return nextLevelName; }
        public void setNextLevelName(String nextLevelName) { this.nextLevelName = nextLevelName; }
        
        public String getNextLevelIcon() { return nextLevelIcon; }
        public void setNextLevelIcon(String nextLevelIcon) { this.nextLevelIcon = nextLevelIcon; }
        
        public String getNextLevelColor() { return nextLevelColor; }
        public void setNextLevelColor(String nextLevelColor) { this.nextLevelColor = nextLevelColor; }
    }
}
