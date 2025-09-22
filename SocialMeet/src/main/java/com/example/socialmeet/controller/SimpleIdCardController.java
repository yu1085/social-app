package com.example.socialmeet.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 简化的身份证二要素核验控制器
 * 不依赖Spring Boot，可以直接测试
 */
public class SimpleIdCardController {
    
    /**
     * 身份证二要素核验（模拟实现）
     */
    public static Map<String, Object> verifyIdCard(String certName, String certNo) {
        try {
            // 模拟支付宝API调用
            Map<String, Object> alipayResponse = callAlipayIdCardVerifyAPI(certName, certNo);
            
            // 生成认证ID
            String verifyId = "VERIFY_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
            
            // 解析响应
            boolean isMatch = "T".equals(alipayResponse.get("match"));
            String certifyId = (String) alipayResponse.get("certify_id");
            
            // 构建结果
            Map<String, Object> result = new HashMap<>();
            result.put("verifyId", verifyId);
            result.put("match", isMatch);
            result.put("certifyId", certifyId);
            result.put("message", isMatch ? "身份证二要素核验通过" : "身份证二要素核验失败");
            result.put("status", isMatch ? "SUCCESS" : "FAILED");
            result.put("verifiedAt", System.currentTimeMillis());
            
            return result;
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "验证失败: " + e.getMessage());
            return errorResult;
        }
    }
    
    /**
     * 模拟支付宝身份证二要素核验API调用
     */
    private static Map<String, Object> callAlipayIdCardVerifyAPI(String certName, String certNo) {
        try {
            // 模拟API调用延迟
            Thread.sleep(1000);
            
            // 简单的验证逻辑（实际应该调用支付宝API）
            boolean isValid = isValidIdCard(certNo) && isValidName(certName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("certify_id", "CERT_" + System.currentTimeMillis());
            response.put("match", isValid ? "T" : "F");
            response.put("code", "10000");
            response.put("msg", "Success");
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("调用支付宝API失败: " + e.getMessage());
        }
    }
    
    /**
     * 简单的身份证号格式验证
     */
    private static boolean isValidIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return false;
        }
        // 简单的格式检查
        return idCard.matches("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
    }
    
    /**
     * 简单的姓名验证
     */
    private static boolean isValidName(String name) {
        return name != null && name.length() >= 2 && name.length() <= 20;
    }
    
    /**
     * 测试方法
     */
    public static void main(String[] args) {
        // 测试用例1：有效身份证
        System.out.println("=== 测试用例1：有效身份证 ===");
        Map<String, Object> result1 = verifyIdCard("张三", "110101199001011234");
        System.out.println("结果: " + result1);
        
        // 测试用例2：无效身份证
        System.out.println("\n=== 测试用例2：无效身份证 ===");
        Map<String, Object> result2 = verifyIdCard("李四", "123456789012345678");
        System.out.println("结果: " + result2);
        
        // 测试用例3：空姓名
        System.out.println("\n=== 测试用例3：空姓名 ===");
        Map<String, Object> result3 = verifyIdCard("", "110101199001011234");
        System.out.println("结果: " + result3);
    }
}
