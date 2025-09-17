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
        initializeWealthLevels();
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
            // 青铜
            WealthLevel bronze = new WealthLevel("青铜", 1, BigDecimal.ZERO, new BigDecimal("99.99"), 
                    "基础财富等级");
            wealthLevelRepository.save(bronze);
            
            // 白银
            WealthLevel silver = new WealthLevel("白银", 2, new BigDecimal("100.00"), new BigDecimal("499.99"), 
                    "中等财富等级");
            wealthLevelRepository.save(silver);
            
            // 黄金
            WealthLevel gold = new WealthLevel("黄金", 3, new BigDecimal("500.00"), new BigDecimal("999.99"), 
                    "高级财富等级");
            wealthLevelRepository.save(gold);
            
            // 钻石
            WealthLevel diamond = new WealthLevel("钻石", 4, new BigDecimal("1000.00"), new BigDecimal("4999.99"), 
                    "顶级财富等级");
            wealthLevelRepository.save(diamond);
            
            // 王者
            WealthLevel king = new WealthLevel("王者", 5, new BigDecimal("5000.00"), null, 
                    "至尊财富等级");
            wealthLevelRepository.save(king);
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
