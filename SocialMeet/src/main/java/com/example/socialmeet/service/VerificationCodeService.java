package com.example.socialmeet.service;

import com.example.socialmeet.entity.PhoneVerification;
import com.example.socialmeet.repository.PhoneVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class VerificationCodeService {
    
    @Autowired
    private PhoneVerificationRepository phoneVerificationRepository;
    
    @Autowired
    private AliyunSmsService aliyunSmsService;
    
    private static final int CODE_LENGTH = 6;
    private static final int EXPIRE_MINUTES = 5;
    
    @Transactional
    public String generateAndStoreCode(String phone) {
        // 生成6位数字验证码
        String code = generateCode();
        
        // 先删除该手机号的旧验证码
        phoneVerificationRepository.deleteByPhoneNumber(phone);
        
        // 创建新的验证码记录
        PhoneVerification verification = new PhoneVerification();
        verification.setPhoneNumber(phone);
        verification.setVerificationCode(code);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES));
        verification.setStatus("PENDING");
        verification.setCreatedAt(LocalDateTime.now());
        
        // 保存到数据库
        phoneVerificationRepository.save(verification);
        
        // 打印到控制台（测试环境）
        System.out.println("=== 验证码发送 ===");
        System.out.println("手机号: " + phone);
        System.out.println("验证码: " + code);
        System.out.println("有效期: " + EXPIRE_MINUTES + "分钟");
        
        return code;
    }
    
    @Transactional
    public boolean verifyCode(String phone, String code) {
        // 查找有效的验证码（包括PENDING和VERIFIED状态，允许重复使用）
        List<PhoneVerification> verifications = phoneVerificationRepository
                .findByPhoneNumberAndVerificationCode(phone, code);
        
        if (verifications.isEmpty()) {
            System.out.println("验证码不存在: " + phone);
            return false;
        }
        
        // 检查是否过期
        PhoneVerification verification = verifications.get(0);
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            System.out.println("验证码已过期: " + phone);
            // 更新状态为过期
            verification.setStatus("EXPIRED");
            phoneVerificationRepository.save(verification);
            return false;
        }
        
        // 验证成功，如果状态不是VERIFIED则更新状态
        if (!"VERIFIED".equals(verification.getStatus())) {
            verification.setStatus("VERIFIED");
            verification.setVerifiedAt(LocalDateTime.now());
            phoneVerificationRepository.save(verification);
        }
        
        System.out.println("验证码验证成功: " + phone);
        return true;
    }
    
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
