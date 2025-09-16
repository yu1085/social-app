package com.example.socialmeet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final String CODE_PREFIX = "verification_code:";
    private static final int CODE_LENGTH = 6;
    private static final int EXPIRE_MINUTES = 5;
    
    public String generateAndStoreCode(String phone) {
        // 生成6位数字验证码
        String code = generateCode();
        
        // 存储到Redis，5分钟过期
        String key = CODE_PREFIX + phone;
        redisTemplate.opsForValue().set(key, code, EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        // 打印到控制台（测试环境）
        System.out.println("=== 验证码发送 ===");
        System.out.println("手机号: " + phone);
        System.out.println("验证码: " + code);
        System.out.println("有效期: " + EXPIRE_MINUTES + "分钟");
        
        return code;
    }
    
    public boolean verifyCode(String phone, String code) {
        String key = CODE_PREFIX + phone;
        String storedCode = redisTemplate.opsForValue().get(key);
        
        if (storedCode == null) {
            System.out.println("验证码不存在或已过期: " + phone);
            return false;
        }
        
        boolean isValid = storedCode.equals(code);
        if (isValid) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
            System.out.println("验证码验证成功: " + phone);
        } else {
            System.out.println("验证码错误: " + phone + ", 输入: " + code + ", 正确: " + storedCode);
        }
        
        return isValid;
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
