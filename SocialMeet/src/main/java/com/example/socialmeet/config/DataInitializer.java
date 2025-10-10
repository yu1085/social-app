package com.example.socialmeet.config;

import com.example.socialmeet.entity.*;
import com.example.socialmeet.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private VipLevelRepository vipLevelRepository;
    
    @Autowired
    private WealthLevelRepository wealthLevelRepository;
    
    @Autowired
    private GiftRepository giftRepository;
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Autowired
    private SystemConfigRepository systemConfigRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initializeVipLevels();
        // initializeWealthLevels(); // 禁用：WealthLevel 是用户专属数据，不应该初始化示例数据
        initializeGifts();
        initializeCoupons();
        initializeSystemConfigs();
    }
    
    private void initializeVipLevels() {
        if (vipLevelRepository.count() == 0) {
            // 普通会员
            VipLevel normalLevel = new VipLevel("普通会员", 0, BigDecimal.ZERO, 0, "基础功能");
            vipLevelRepository.save(normalLevel);
            
            // VIP会员
            VipLevel vipLevel = new VipLevel("VIP会员", 1, new BigDecimal("99.00"), 30, 
                    "无限制聊天、查看访客、优先推荐");
            vipLevelRepository.save(vipLevel);
            
            // SVIP会员
            VipLevel svipLevel = new VipLevel("SVIP会员", 2, new BigDecimal("334.00"), 365, 
                    "所有VIP功能、专属客服、高级筛选");
            vipLevelRepository.save(svipLevel);
        }
    }
    
    private void initializeWealthLevels() {
        if (wealthLevelRepository.count() == 0) {
            // 创建示例用户财富等级数据
            // 青铜等级用户 (1000财富值)
            WealthLevel bronze = new WealthLevel(1L, 1000);
            wealthLevelRepository.save(bronze);
            
            // 白银等级用户 (2000财富值)
            WealthLevel silver = new WealthLevel(2L, 2000);
            wealthLevelRepository.save(silver);
            
            // 黄金等级用户 (5000财富值)
            WealthLevel gold = new WealthLevel(3L, 5000);
            wealthLevelRepository.save(gold);
            
            // 铂金等级用户 (10000财富值)
            WealthLevel platinum = new WealthLevel(4L, 10000);
            wealthLevelRepository.save(platinum);
            
            // 青钻等级用户 (30000财富值)
            WealthLevel cyanDiamond = new WealthLevel(5L, 30000);
            wealthLevelRepository.save(cyanDiamond);
            
            // 蓝钻等级用户 (50000财富值)
            WealthLevel blueDiamond = new WealthLevel(6L, 50000);
            wealthLevelRepository.save(blueDiamond);
            
            // 紫钻等级用户 (100000财富值)
            WealthLevel purpleDiamond = new WealthLevel(7L, 100000);
            wealthLevelRepository.save(purpleDiamond);
            
            // 橙钻等级用户 (300000财富值)
            WealthLevel orangeDiamond = new WealthLevel(8L, 300000);
            wealthLevelRepository.save(orangeDiamond);
            
            // 红钻等级用户 (500000财富值)
            WealthLevel redDiamond = new WealthLevel(9L, 500000);
            wealthLevelRepository.save(redDiamond);
            
            // 金钻等级用户 (700000财富值)
            WealthLevel goldDiamond = new WealthLevel(10L, 700000);
            wealthLevelRepository.save(goldDiamond);
            
            // 黑钻等级用户 (1000000财富值)
            WealthLevel blackDiamond = new WealthLevel(11L, 1000000);
            wealthLevelRepository.save(blackDiamond);
        }
    }
    
    private void initializeGifts() {
        if (giftRepository.count() == 0) {
            // 爱情类礼物
            Gift rose = new Gift("玫瑰花", "表达爱意的经典礼物", new BigDecimal("1.00"), "LOVE");
            giftRepository.save(rose);
            
            Gift chocolate = new Gift("巧克力", "甜蜜的象征", new BigDecimal("5.00"), "LOVE");
            giftRepository.save(chocolate);
            
            Gift ring = new Gift("钻戒", "永恒的承诺", new BigDecimal("99.00"), "LOVE");
            giftRepository.save(ring);
            
            // 奢华类礼物
            Gift car = new Gift("跑车", "豪华座驾", new BigDecimal("999.00"), "LUXURY");
            giftRepository.save(car);
            
            Gift castle = new Gift("城堡", "梦幻家园", new BigDecimal("9999.00"), "LUXURY");
            giftRepository.save(castle);
        }
    }
    
    private void initializeCoupons() {
        if (couponRepository.count() == 0) {
            // 新用户优惠券
            Coupon newUserCoupon = new Coupon("新用户优惠券", "新用户专享", 
                    "DISCOUNT", new BigDecimal("10.00"), 30);
            newUserCoupon.setMinAmount(new BigDecimal("50.00"));
            couponRepository.save(newUserCoupon);
            
            // 充值优惠券
            Coupon rechargeCoupon = new Coupon("充值优惠券", "充值满减", 
                    "CASH", new BigDecimal("20.00"), 7);
            rechargeCoupon.setMinAmount(new BigDecimal("100.00"));
            couponRepository.save(rechargeCoupon);
            
            // VIP体验券
            Coupon vipCoupon = new Coupon("VIP体验券", "VIP功能体验", 
                    "VIP", BigDecimal.ZERO, 3);
            couponRepository.save(vipCoupon);
        }
    }
    
    private void initializeSystemConfigs() {
        if (systemConfigRepository.count() == 0) {
            // 应用版本
            SystemConfig appVersion = new SystemConfig("app_version", "1.0.0", "应用版本号");
            systemConfigRepository.save(appVersion);
            
            // 最小充值金额
            SystemConfig minRecharge = new SystemConfig("min_recharge_amount", "10.00", "最小充值金额");
            systemConfigRepository.save(minRecharge);
            
            // 最大充值金额
            SystemConfig maxRecharge = new SystemConfig("max_recharge_amount", "10000.00", "最大充值金额");
            systemConfigRepository.save(maxRecharge);
            
            // 礼物税率
            SystemConfig giftTax = new SystemConfig("gift_tax_rate", "0.05", "礼物税率");
            systemConfigRepository.save(giftTax);
            
            // VIP折扣率
            SystemConfig vipDiscount = new SystemConfig("vip_discount_rate", "0.1", "VIP折扣率");
            systemConfigRepository.save(vipDiscount);
        }
    }
}
