package com.socialmeet.backend.service;

import com.socialmeet.backend.dto.AlipayOrderResponse;
import com.socialmeet.backend.entity.PaymentOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 支付宝服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlipayService {

    @Value("${alipay.app-id:2021005195696348}")
    private String appId;

    @Value("${alipay.private-key:}")
    private String privateKey;

    @Value("${alipay.public-key:}")
    private String alipayPublicKey;

    @Value("${alipay.gateway-url:https://openapi.alipay.com/gateway.do}")
    private String gatewayUrl;

    @Value("${alipay.notify-url:http://localhost:8080/api/payment/alipay/callback}")
    private String notifyUrl;

    @Value("${alipay.return-url:http://localhost:8080/api/payment/alipay/return}")
    private String returnUrl;

    /**
     * 创建支付宝订单
     */
    public AlipayOrderResponse createOrder(PaymentOrder order) {
        try {
            log.info("创建支付宝订单: {}", order.getOrderId());

            // 生成支付宝商户订单号
            String alipayOutTradeNo = generateAlipayOutTradeNo();
            
            // 构建支付参数
            Map<String, String> payParams = buildPayParams(order, alipayOutTradeNo);
            
            // 生成签名
            String sign = generateSign(payParams);
            payParams.put("sign", sign);
            
            // 构建支付信息字符串
            String payInfo = buildPayInfoString(payParams);
            
            // 更新订单的支付宝商户订单号
            order.setAlipayOutTradeNo(alipayOutTradeNo);
            
            AlipayOrderResponse response = new AlipayOrderResponse();
            response.setOrderId(order.getOrderId());
            response.setAlipayOrderInfo(payInfo);
            response.setAlipayOutTradeNo(alipayOutTradeNo);
            response.setExpireTime(System.currentTimeMillis() + 30 * 60 * 1000); // 30分钟过期
            
            log.info("支付宝订单创建成功: {}", order.getOrderId());
            return response;
            
        } catch (Exception e) {
            log.error("创建支付宝订单失败: {}", order.getOrderId(), e);
            throw new RuntimeException("创建支付宝订单失败: " + e.getMessage());
        }
    }

    /**
     * 验证支付宝回调签名
     */
    public boolean verifyCallback(Map<String, String> params) {
        try {
            // 移除sign和sign_type参数
            Map<String, String> verifyParams = new HashMap<>(params);
            verifyParams.remove("sign");
            verifyParams.remove("sign_type");
            
            // 生成待签名字符串
            String signContent = buildSignContent(verifyParams);
            
            // 验证签名
            String sign = params.get("sign");
            return verifySign(signContent, sign);
            
        } catch (Exception e) {
            log.error("验证支付宝回调签名失败", e);
            return false;
        }
    }

    /**
     * 查询订单状态
     */
    public Map<String, String> queryOrder(String alipayOutTradeNo) {
        try {
            // 构建查询参数
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("app_id", appId);
            queryParams.put("method", "alipay.trade.query");
            queryParams.put("charset", "utf-8");
            queryParams.put("sign_type", "RSA2");
            queryParams.put("timestamp", getCurrentTimestamp());
            queryParams.put("version", "1.0");
            queryParams.put("biz_content", String.format("{\"out_trade_no\":\"%s\"}", alipayOutTradeNo));
            
            // 生成签名
            String sign = generateSign(queryParams);
            queryParams.put("sign", sign);
            
            // 发送查询请求
            // TODO: 实际项目中需要发送HTTP请求到支付宝网关
            log.info("查询支付宝订单状态: {}", alipayOutTradeNo);
            
            // 模拟返回结果
            Map<String, String> result = new HashMap<>();
            result.put("trade_status", "TRADE_SUCCESS");
            result.put("trade_no", "2024010100000000000000000000000000");
            result.put("total_amount", "12.00");
            
            return result;
            
        } catch (Exception e) {
            log.error("查询支付宝订单状态失败: {}", alipayOutTradeNo, e);
            throw new RuntimeException("查询订单状态失败: " + e.getMessage());
        }
    }

    /**
     * 构建支付参数
     */
    private Map<String, String> buildPayParams(PaymentOrder order, String alipayOutTradeNo) {
        Map<String, String> params = new HashMap<>();
        params.put("app_id", appId);
        params.put("method", "alipay.trade.app.pay");
        params.put("charset", "utf-8");
        params.put("sign_type", "RSA2");
        params.put("timestamp", getCurrentTimestamp());
        params.put("version", "1.0");
        params.put("notify_url", notifyUrl);
        
        // 构建biz_content
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", alipayOutTradeNo);
        bizContent.put("total_amount", order.getAmount().toString());
        bizContent.put("subject", order.getDescription());
        bizContent.put("body", "SocialMeet金币充值");
        bizContent.put("timeout_express", "30m");
        
        params.put("biz_content", mapToJson(bizContent));
        
        return params;
    }

    /**
     * 生成签名
     */
    private String generateSign(Map<String, String> params) {
        try {
            // 构建待签名字符串
            String signContent = buildSignContent(params);
            
            // 使用RSA私钥签名
            // TODO: 实际项目中需要使用真实的RSA签名算法
            return "mock_sign_" + System.currentTimeMillis();
            
        } catch (Exception e) {
            log.error("生成签名失败", e);
            throw new RuntimeException("生成签名失败: " + e.getMessage());
        }
    }

    /**
     * 验证签名
     */
    private boolean verifySign(String signContent, String sign) {
        try {
            // TODO: 实际项目中需要使用支付宝公钥验证签名
            log.info("验证签名: content={}, sign={}", signContent, sign);
            return true; // 模拟验证成功
        } catch (Exception e) {
            log.error("验证签名失败", e);
            return false;
        }
    }

    /**
     * 构建待签名字符串
     */
    private String buildSignContent(Map<String, String> params) {
        return params.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
    }

    /**
     * 构建支付信息字符串
     */
    private String buildPayInfoString(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
    }

    /**
     * 生成支付宝商户订单号
     */
    private String generateAlipayOutTradeNo() {
        return "ALIPAY_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 获取当前时间戳
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Map转JSON字符串
     */
    private String mapToJson(Map<String, Object> map) {
        // 简单的JSON转换，实际项目中建议使用Jackson或Gson
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        json.append("}");
        return json.toString();
    }
}
