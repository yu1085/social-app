package com.example.socialmeet.service;

import com.example.socialmeet.entity.IdCardVerify;
import com.example.socialmeet.repository.IdCardVerifyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class IdCardVerifyService {
    
    @Autowired
    private IdCardVerifyRepository verifyRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private AlipayApiService alipayApiService;
    
    /**
     * 身份证二要素核验（支付宝官方推荐）
     */
    public Map<String, Object> verifyIdCard(Long userId, String certName, String certNo) {
        try {
            // 调用支付宝身份证二要素核验接口
            Map<String, Object> alipayResponse = alipayApiService.verifyIdCard(certName, certNo);
            
            // 创建认证记录
            String verifyId = generateVerifyId();
            IdCardVerify verify = new IdCardVerify();
            verify.setVerifyId(verifyId);
            verify.setUserId(userId);
            verify.setCertName(certName);
            verify.setCertNo(certNo);
            verify.setStatus("PENDING");
            verify.setCreatedAt(LocalDateTime.now());
            verify.setExpiresAt(LocalDateTime.now().plusMinutes(30));
            
            // 解析支付宝响应
            boolean isSuccess = (Boolean) alipayResponse.getOrDefault("success", false);
            boolean isMatch = "T".equals(alipayResponse.get("match"));
            String certifyId = (String) alipayResponse.get("certify_id");
            
            if (isSuccess && isMatch) {
                verify.setStatus("SUCCESS");
                verify.setMessage("身份证二要素核验通过");
                verify.setCertifyId(certifyId);
            } else if (isSuccess && !isMatch) {
                verify.setStatus("FAILED");
                verify.setMessage("身份证二要素核验失败");
                verify.setRejectReason("姓名与身份证号不匹配");
            } else {
                verify.setStatus("FAILED");
                verify.setMessage("API调用失败");
                verify.setRejectReason(alipayResponse.getOrDefault("msg", "未知错误").toString());
            }
            
            verify.setAlipayResponse(objectMapper.writeValueAsString(alipayResponse));
            verify.setCompletedAt(LocalDateTime.now());
            
            // 保存到数据库
            verifyRepository.save(verify);
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("verifyId", verifyId);
            result.put("match", isMatch);
            result.put("certifyId", certifyId);
            result.put("message", verify.getMessage());
            result.put("status", verify.getStatus());
            result.put("verifiedAt", verify.getCompletedAt());
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("身份证二要素核验失败: " + e.getMessage());
        }
    }

    /**
     * 获取实名认证状态
     */
    public Map<String, Object> getVerificationStatus(Long userId) {
        try {
            // 查找用户最新的认证记录
            List<IdCardVerify> verifies = verifyRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("hasVerified", false);
            result.put("verificationStatus", "NOT_VERIFIED");
            result.put("verificationTime", null);
            result.put("verificationMethod", "ID_CARD_VERIFY");
            
            if (!verifies.isEmpty()) {
                IdCardVerify latestVerify = verifies.get(0);
                if ("SUCCESS".equals(latestVerify.getStatus())) {
                    result.put("hasVerified", true);
                    result.put("verificationStatus", "VERIFIED");
                    result.put("verificationTime", latestVerify.getCompletedAt());
                    result.put("realName", latestVerify.getCertName());
                    result.put("idCardNumber", maskIdCard(latestVerify.getCertNo()));
                } else if ("FAILED".equals(latestVerify.getStatus())) {
                    result.put("verificationStatus", "FAILED");
                    result.put("failureReason", latestVerify.getRejectReason());
                } else if ("PENDING".equals(latestVerify.getStatus())) {
                    result.put("verificationStatus", "PENDING");
                }
            }
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("获取认证状态失败: " + e.getMessage());
        }
    }

    /**
     * 提交实名认证
     */
    public Map<String, Object> submitVerification(Long userId, String certName, String certNo) {
        try {
            // 检查是否已经认证过
            Map<String, Object> status = getVerificationStatus(userId);
            if ((Boolean) status.get("hasVerified")) {
                return Map.of(
                    "success", false,
                    "message", "您已经完成实名认证",
                    "verificationStatus", status
                );
            }
            
            // 执行身份证二要素核验
            Map<String, Object> verifyResult = verifyIdCard(userId, certName, certNo);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "实名认证提交成功");
            result.put("verificationResult", verifyResult);
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("提交实名认证失败: " + e.getMessage());
        }
    }

    /**
     * 查询认证结果
     */
    public Map<String, Object> getVerificationResult(Long userId) {
        try {
            // 查找用户最新的认证记录
            List<IdCardVerify> verifies = verifyRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("hasRecord", false);
            result.put("verificationResult", null);
            
            if (!verifies.isEmpty()) {
                IdCardVerify latestVerify = verifies.get(0);
                result.put("hasRecord", true);
                
                Map<String, Object> verificationResult = new HashMap<>();
                verificationResult.put("verifyId", latestVerify.getVerifyId());
                verificationResult.put("status", latestVerify.getStatus());
                verificationResult.put("message", latestVerify.getMessage());
                verificationResult.put("realName", latestVerify.getCertName());
                verificationResult.put("idCardNumber", maskIdCard(latestVerify.getCertNo()));
                verificationResult.put("createdAt", latestVerify.getCreatedAt());
                verificationResult.put("completedAt", latestVerify.getCompletedAt());
                verificationResult.put("rejectReason", latestVerify.getRejectReason());
                
                result.put("verificationResult", verificationResult);
            }
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("查询认证结果失败: " + e.getMessage());
        }
    }


    /**
     * 生成认证ID
     */
    private String generateVerifyId() {
        return "VERIFY_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * 身份证号脱敏
     */
    private String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) {
            return idCard;
        }
        return idCard.substring(0, 4) + "****" + idCard.substring(idCard.length() - 4);
    }
}
