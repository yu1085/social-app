package com.example.socialmeet.service;

import com.example.socialmeet.dto.VipLevelDTO;
import com.example.socialmeet.dto.VipSubscriptionDTO;
import com.example.socialmeet.entity.VipLevel;
import com.example.socialmeet.entity.VipSubscription;
import com.example.socialmeet.repository.VipLevelRepository;
import com.example.socialmeet.repository.VipSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VipService {
    
    @Autowired
    private VipLevelRepository vipLevelRepository;
    
    @Autowired
    private VipSubscriptionRepository vipSubscriptionRepository;
    
    @Autowired
    private WalletService walletService;
    
    public List<VipLevelDTO> getAllVipLevels() {
        List<VipLevel> levels = vipLevelRepository.findByIsActiveTrueOrderByLevelAsc();
        return levels.stream().map(level -> new VipLevelDTO(level.getId(), level.getName(), 
                                                          level.getLevel(), level.getPrice(), 
                                                          level.getDuration(), level.getBenefits()))
                     .collect(Collectors.toList());
    }
    
    public VipLevelDTO getVipLevelById(Long id) {
        Optional<VipLevel> levelOpt = vipLevelRepository.findById(id);
        if (levelOpt.isPresent()) {
            VipLevel level = levelOpt.get();
            return new VipLevelDTO(level.getId(), level.getName(), level.getLevel(), 
                                 level.getPrice(), level.getDuration(), level.getBenefits());
        }
        return null;
    }
    
    public VipSubscriptionDTO subscribeVip(Long userId, Long vipLevelId) {
        Optional<VipLevel> levelOpt = vipLevelRepository.findById(vipLevelId);
        if (!levelOpt.isPresent()) {
            throw new RuntimeException("VIP等级不存在");
        }
        
        VipLevel level = levelOpt.get();
        
        // 检查是否已有有效的VIP订阅
        Optional<VipSubscription> existingSubscription = vipSubscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now());
        
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(level.getDuration());
        
        VipSubscription subscription;
        if (existingSubscription.isPresent()) {
            // 续费现有订阅
            subscription = existingSubscription.get();
            subscription.setEndDate(subscription.getEndDate().plusDays(level.getDuration()));
            subscription.setAmount(subscription.getAmount().add(level.getPrice()));
        } else {
            // 创建新订阅
            subscription = new VipSubscription(userId, vipLevelId, startDate, endDate, level.getPrice());
        }
        
        subscription = vipSubscriptionRepository.save(subscription);
        
        // 扣除费用
        if (!walletService.consume(userId, level.getPrice(), 
                                 "VIP订阅: " + level.getName(), subscription.getId())) {
            throw new RuntimeException("余额不足");
        }
        
        VipSubscriptionDTO dto = new VipSubscriptionDTO(subscription);
        dto.setVipLevelName(level.getName());
        dto.setVipLevel(level.getLevel());
        
        return dto;
    }
    
    public VipSubscriptionDTO getCurrentVipSubscription(Long userId) {
        Optional<VipSubscription> subscriptionOpt = vipSubscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now());
        
        if (subscriptionOpt.isPresent()) {
            VipSubscription subscription = subscriptionOpt.get();
            VipSubscriptionDTO dto = new VipSubscriptionDTO(subscription);
            
            // 获取VIP等级信息
            Optional<VipLevel> levelOpt = vipLevelRepository.findById(subscription.getVipLevelId());
            if (levelOpt.isPresent()) {
                VipLevel level = levelOpt.get();
                dto.setVipLevelName(level.getName());
                dto.setVipLevel(level.getLevel());
            }
            
            return dto;
        }
        
        return null;
    }
    
    public List<VipSubscriptionDTO> getUserVipHistory(Long userId) {
        List<VipSubscription> subscriptions = vipSubscriptionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return subscriptions.stream().map(subscription -> {
            VipSubscriptionDTO dto = new VipSubscriptionDTO(subscription);
            
            // 获取VIP等级信息
            Optional<VipLevel> levelOpt = vipLevelRepository.findById(subscription.getVipLevelId());
            if (levelOpt.isPresent()) {
                VipLevel level = levelOpt.get();
                dto.setVipLevelName(level.getName());
                dto.setVipLevel(level.getLevel());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    public boolean isVipUser(Long userId) {
        return vipSubscriptionRepository.findActiveSubscriptionByUserId(userId, LocalDateTime.now()).isPresent();
    }
    
    public Integer getVipLevel(Long userId) {
        Optional<VipSubscription> subscriptionOpt = vipSubscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now());
        
        if (subscriptionOpt.isPresent()) {
            Optional<VipLevel> levelOpt = vipLevelRepository.findById(subscriptionOpt.get().getVipLevelId());
            if (levelOpt.isPresent()) {
                return levelOpt.get().getLevel();
            }
        }
        
        return 0; // 普通用户
    }
}
