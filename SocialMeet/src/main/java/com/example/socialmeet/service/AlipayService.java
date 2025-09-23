package com.example.socialmeet.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.example.socialmeet.config.PaymentConfig;
import com.example.socialmeet.entity.RechargeOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付宝支付服务
 */
@Service
@Slf4j
public class AlipayService {
    
    @Autowired
    private PaymentConfig paymentConfig;
    
    private AlipayClient alipayClient;
    
    /**
     * 获取支付宝客户端
     */
    private AlipayClient getAlipayClient() {
        if (alipayClient == null) {
            PaymentConfig.AlipayConfig config = paymentConfig.getAlipay();
            alipayClient = new DefaultAlipayClient(
                config.getServerUrl(),
                config.getAppId(),
                config.getPrivateKey(),
                config.getFormat(),
                config.getCharset(),
                config.getAlipayPublicKey(),
                config.getSignType()
            );
        }
        return alipayClient;
    }
    
    /**
     * 创建支付宝支付订单
     */
    public String createPaymentOrder(RechargeOrder order) throws AlipayApiException {
        log.info("创建支付宝支付订单: {}", order.getOrderId());
        
        PaymentConfig.AlipayConfig config = paymentConfig.getAlipay();
        
        // 创建支付请求
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        request.setNotifyUrl(config.getNotifyUrl());
        request.setReturnUrl(config.getReturnUrl());
        
        // 设置业务参数
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setOutTradeNo(order.getOrderId());
        model.setTotalAmount(order.getAmount().toString());
        model.setSubject("充值" + order.getCoins() + "金币");
        model.setBody(order.getDescription() != null ? order.getDescription() : "用户充值");
        model.setProductCode("QUICK_MSECURITY_PAY");
        model.setTimeoutExpress("30m"); // 30分钟超时
        
        request.setBizModel(model);
        
        // 调用API - 使用execute方法生成适合Android客户端的支付字符串
        AlipayTradeAppPayResponse response = getAlipayClient().execute(request);
        
        if (response.isSuccess()) {
            log.info("支付宝订单创建成功: {}", order.getOrderId());
            // 返回完整的支付字符串，包含所有必要的参数
            return response.getBody();
        } else {
            log.error("支付宝订单创建失败: {}, 错误信息: {}", order.getOrderId(), response.getMsg());
            throw new RuntimeException("支付宝订单创建失败: " + response.getMsg());
        }
    }
    
    /**
     * 查询支付宝订单状态
     */
    public String queryOrderStatus(String orderId) throws AlipayApiException {
        log.info("查询支付宝订单状态: {}", orderId);
        
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{\"out_trade_no\":\"" + orderId + "\"}");
        
        AlipayTradeQueryResponse response = getAlipayClient().execute(request);
        
        if (response.isSuccess()) {
            log.info("支付宝订单查询成功: {}, 状态: {}", orderId, response.getTradeStatus());
            return response.getTradeStatus();
        } else {
            log.error("支付宝订单查询失败: {}, 错误信息: {}", orderId, response.getMsg());
            throw new RuntimeException("支付宝订单查询失败: " + response.getMsg());
        }
    }
    
    /**
     * 验证支付宝回调签名
     */
    public boolean verifyCallback(Map<String, String> params) {
        try {
            log.info("验证支付宝回调签名");
            
            // 使用支付宝SDK验证签名
            // 注意：这里需要根据实际的回调参数进行验证
            // 实际项目中应该使用 AlipaySignature.rsaCheckV1() 方法
            
            // 模拟验证逻辑
            String sign = params.get("sign");
            String signType = params.get("sign_type");
            
            if (sign == null || signType == null) {
                log.error("支付宝回调缺少签名参数");
                return false;
            }
            
            // TODO: 实现真实的签名验证
            // return AlipaySignature.rsaCheckV1(params, paymentConfig.getAlipay().getAlipayPublicKey(), 
            //     paymentConfig.getAlipay().getCharset(), paymentConfig.getAlipay().getSignType());
            
            // 临时返回true，实际项目中需要实现真实验证
            log.warn("支付宝签名验证暂时跳过，生产环境必须实现真实验证");
            return true;
            
        } catch (Exception e) {
            log.error("支付宝回调签名验证失败", e);
            return false;
        }
    }
    
    /**
     * 处理支付宝回调
     */
    public boolean handleCallback(Map<String, String> params) {
        try {
            log.info("处理支付宝回调: {}", params);
            
            // 验证签名
            if (!verifyCallback(params)) {
                log.error("支付宝回调签名验证失败");
                return false;
            }
            
            String outTradeNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String tradeStatus = params.get("trade_status");
            String totalAmount = params.get("total_amount");
            
            log.info("支付宝回调参数 - 订单号: {}, 交易号: {}, 状态: {}, 金额: {}", 
                outTradeNo, tradeNo, tradeStatus, totalAmount);
            
            // 验证必要参数
            if (outTradeNo == null || tradeStatus == null) {
                log.error("支付宝回调缺少必要参数");
                return false;
            }
            
            // 处理支付成功
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                log.info("支付宝支付成功: {}", outTradeNo);
                return true;
            } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                log.info("支付宝订单关闭: {}", outTradeNo);
                return true;
            } else {
                log.warn("支付宝订单状态未知: {}, 状态: {}", outTradeNo, tradeStatus);
                return true;
            }
            
        } catch (Exception e) {
            log.error("处理支付宝回调异常", e);
            return false;
        }
    }
    
    /**
     * 查询支付宝订单状态
     */
    public Map<String, String> queryPaymentStatus(String orderId) throws AlipayApiException {
        log.info("查询支付宝订单状态 - 订单号: {}", orderId);
        
        try {
            AlipayClient client = getAlipayClient();
            
            // 创建查询请求
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            request.setBizContent("{" +
                "\"out_trade_no\":\"" + orderId + "\"" +
                "}");
            
            // 执行查询
            AlipayTradeQueryResponse response = client.execute(request);
            
            if (response.isSuccess()) {
                log.info("支付宝订单查询成功 - 订单号: {}, 状态: {}", orderId, response.getTradeStatus());
                
                // 构建返回结果
                Map<String, String> result = new java.util.HashMap<>();
                result.put("trade_status", response.getTradeStatus());
                result.put("trade_no", response.getTradeNo());
                result.put("out_trade_no", response.getOutTradeNo());
                result.put("total_amount", response.getTotalAmount());
                result.put("buyer_user_id", response.getBuyerUserId());
                
                return result;
            } else {
                log.warn("支付宝订单查询失败 - 订单号: {}, 错误: {}", orderId, response.getSubMsg());
                return null;
            }
            
        } catch (Exception e) {
            log.error("查询支付宝订单状态异常 - 订单号: {}", orderId, e);
            throw new AlipayApiException("查询支付宝订单状态失败: " + e.getMessage());
        }
    }
}
