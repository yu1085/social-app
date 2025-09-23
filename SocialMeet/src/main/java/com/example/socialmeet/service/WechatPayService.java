package com.example.socialmeet.service;

import com.example.socialmeet.config.PaymentConfig;
import com.example.socialmeet.entity.RechargeOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

/**
 * 微信支付服务
 */
@Service
@Slf4j
public class WechatPayService {
    
    @Autowired
    private PaymentConfig paymentConfig;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 创建微信支付订单
     */
    public Map<String, String> createPaymentOrder(RechargeOrder order) {
        try {
            log.info("创建微信支付订单: {}", order.getOrderId());
            
            PaymentConfig.WechatPayConfig config = paymentConfig.getWechat();
            
            // 构建请求参数
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("appid", config.getAppId());
            requestBody.put("mchid", config.getMchId());
            requestBody.put("description", "充值" + order.getCoins() + "金币");
            requestBody.put("out_trade_no", order.getOrderId());
            requestBody.put("notify_url", config.getNotifyUrl());
            
            // 金额信息
            Map<String, Object> amount = new HashMap<>();
            amount.put("total", order.getAmount().multiply(new java.math.BigDecimal(100)).intValue()); // 转换为分
            amount.put("currency", "CNY");
            requestBody.put("amount", amount);
            
            // 设置过期时间
            requestBody.put("time_expire", java.time.format.DateTimeFormatter.ISO_INSTANT
                .format(order.getExpiredAt().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            
            // 发起统一下单请求
            String url = config.getServerUrl() + "/v3/pay/transactions/app";
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            headers.set("User-Agent", "SocialMeet/1.0");
            
            // TODO: 添加微信支付签名
            // String authorization = buildWechatPayAuthorization("POST", "/v3/pay/transactions/app", requestBodyJson);
            // headers.set("Authorization", authorization);
            
            HttpEntity<String> request = new HttpEntity<>(requestBodyJson, headers);
            
            // 模拟微信支付响应（实际项目中需要真实调用）
            log.warn("微信支付API调用暂时使用模拟数据，生产环境需要实现真实调用");
            
            // 构建返回给客户端的支付参数
            Map<String, String> paymentParams = new HashMap<>();
            paymentParams.put("appId", config.getAppId());
            paymentParams.put("partnerId", config.getMchId());
            paymentParams.put("prepayId", "prepay_id_" + order.getOrderId()); // 实际应该从微信接口获取
            paymentParams.put("packageValue", "Sign=WXPay");
            paymentParams.put("nonceStr", generateNonceStr());
            paymentParams.put("timeStamp", String.valueOf(Instant.now().getEpochSecond()));
            
            // TODO: 添加客户端签名
            // String sign = generateWechatPaySign(paymentParams, config.getApiV3Key());
            // paymentParams.put("sign", sign);
            
            log.info("微信支付订单创建成功: {}", order.getOrderId());
            return paymentParams;
            
        } catch (Exception e) {
            log.error("创建微信支付订单失败: {}", order.getOrderId(), e);
            throw new RuntimeException("创建微信支付订单失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理微信支付回调
     */
    public boolean handleCallback(String xmlData) {
        try {
            log.info("处理微信支付回调");
            
            // 解析XML数据
            Map<String, String> params = parseWechatXml(xmlData);
            
            if (params == null || params.isEmpty()) {
                log.error("微信支付回调数据解析失败");
                return false;
            }
            
            // 验证签名
            if (!verifyWechatCallback(params)) {
                log.error("微信支付回调签名验证失败");
                return false;
            }
            
            String outTradeNo = params.get("out_trade_no");
            String transactionId = params.get("transaction_id");
            String resultCode = params.get("result_code");
            String returnCode = params.get("return_code");
            
            log.info("微信支付回调参数 - 订单号: {}, 交易号: {}, 结果: {}/{}", 
                outTradeNo, transactionId, returnCode, resultCode);
            
            // 验证必要参数
            if (outTradeNo == null || returnCode == null) {
                log.error("微信支付回调缺少必要参数");
                return false;
            }
            
            // 处理支付成功
            if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
                log.info("微信支付成功: {}", outTradeNo);
                return true;
            } else {
                log.warn("微信支付失败: {}, 返回码: {}, 结果码: {}", outTradeNo, returnCode, resultCode);
                return true; // 即使失败也返回true，表示回调处理完成
            }
            
        } catch (Exception e) {
            log.error("处理微信支付回调异常", e);
            return false;
        }
    }
    
    /**
     * 验证微信支付回调签名
     */
    private boolean verifyWechatCallback(Map<String, String> params) {
        try {
            log.info("验证微信支付回调签名");
            
            String sign = params.get("sign");
            if (sign == null) {
                log.error("微信支付回调缺少签名");
                return false;
            }
            
            // TODO: 实现真实的微信支付签名验证
            // 1. 按照微信支付规则对参数进行排序和拼接
            // 2. 使用API密钥进行MD5签名
            // 3. 与回调中的签名进行比较
            
            log.warn("微信支付签名验证暂时跳过，生产环境必须实现真实验证");
            return true;
            
        } catch (Exception e) {
            log.error("微信支付回调签名验证失败", e);
            return false;
        }
    }
    
    /**
     * 解析微信支付XML回调数据
     */
    private Map<String, String> parseWechatXml(String xmlData) {
        try {
            Map<String, String> result = new HashMap<>();
            
            // 简单的XML解析（实际项目中建议使用专业的XML解析库）
            if (xmlData == null || !xmlData.contains("<xml>")) {
                return result;
            }
            
            // 模拟解析结果
            result.put("return_code", "SUCCESS");
            result.put("result_code", "SUCCESS");
            result.put("out_trade_no", extractXmlValue(xmlData, "out_trade_no"));
            result.put("transaction_id", extractXmlValue(xmlData, "transaction_id"));
            result.put("total_fee", extractXmlValue(xmlData, "total_fee"));
            result.put("sign", extractXmlValue(xmlData, "sign"));
            
            return result;
            
        } catch (Exception e) {
            log.error("解析微信支付XML数据失败", e);
            return new HashMap<>();
        }
    }
    
    /**
     * 从XML中提取指定字段的值
     */
    private String extractXmlValue(String xml, String fieldName) {
        try {
            String startTag = "<" + fieldName + ">";
            String endTag = "</" + fieldName + ">";
            
            int startIndex = xml.indexOf(startTag);
            int endIndex = xml.indexOf(endTag);
            
            if (startIndex >= 0 && endIndex > startIndex) {
                return xml.substring(startIndex + startTag.length(), endIndex);
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 生成随机字符串
     */
    private String generateNonceStr() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    /**
     * 生成微信支付签名
     */
    private String generateWechatPaySign(Map<String, String> params, String key) {
        try {
            // 排序参数
            TreeMap<String, String> sortedParams = new TreeMap<>(params);
            
            // 拼接参数
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
            }
            sb.append("key=").append(key);
            
            // MD5签名
            String signStr = sb.toString();
            return md5(signStr).toUpperCase();
            
        } catch (Exception e) {
            log.error("生成微信支付签名失败", e);
            return "";
        }
    }
    
    /**
     * MD5加密
     */
    private String md5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("MD5加密失败", e);
            return "";
        }
    }
    
    /**
     * 查询微信支付订单状态
     */
    public Map<String, String> queryPaymentStatus(String orderId) {
        log.info("查询微信支付订单状态 - 订单号: {}", orderId);
        
        try {
            PaymentConfig.WechatPayConfig config = paymentConfig.getWechat();
            
            // 构建查询参数
            Map<String, String> params = new TreeMap<>();
            params.put("appid", config.getAppId());
            params.put("mch_id", config.getMchId());
            params.put("out_trade_no", orderId);
            params.put("nonce_str", generateNonceStr());
            
            // 生成签名
            String sign = generateWechatSign(params, config.getApiV3Key());
            params.put("sign", sign);
            
            // 构建XML请求
            StringBuilder xmlBuilder = new StringBuilder();
            xmlBuilder.append("<xml>");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                xmlBuilder.append("<").append(entry.getKey()).append(">")
                         .append(entry.getValue())
                         .append("</").append(entry.getKey()).append(">");
            }
            xmlBuilder.append("</xml>");
            
            // 发送查询请求（这里使用模拟实现）
            log.info("微信支付订单查询请求: {}", xmlBuilder.toString());
            
            // 模拟返回结果
            Map<String, String> result = new java.util.HashMap<>();
            result.put("trade_state", "SUCCESS");
            result.put("transaction_id", "4200001234567890");
            result.put("out_trade_no", orderId);
            result.put("total_fee", "600");
            
            log.info("微信支付订单查询成功 - 订单号: {}, 状态: {}", orderId, result.get("trade_state"));
            return result;
            
        } catch (Exception e) {
            log.error("查询微信支付订单状态异常 - 订单号: {}", orderId, e);
            return null;
        }
    }
    
    /**
     * 生成微信支付签名
     */
    private String generateWechatSign(Map<String, String> params, String key) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!"sign".equals(entry.getKey()) && entry.getValue() != null && !entry.getValue().isEmpty()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        sb.append("key=").append(key);
        
        return md5(sb.toString()).toUpperCase();
    }
}
