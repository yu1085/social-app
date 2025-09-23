package com.example.socialmeet.service;

import com.example.socialmeet.entity.RechargeOrder;
import com.example.socialmeet.repository.RechargeOrderRepository;
import com.example.socialmeet.exception.PaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 充值服务类
 */
@Service
public class RechargeService {
    
    private static final Logger logger = LoggerFactory.getLogger(RechargeService.class);
    
    @Autowired
    private RechargeOrderRepository rechargeOrderRepository;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private AlipayService alipayService;
    
    @Autowired
    private WechatPayService wechatPayService;
    
    @Autowired
    private WealthLevelService wealthLevelService;
    
    /**
     * 创建充值订单
     */
    @Transactional
    public Map<String, Object> createOrder(Long userId, String packageId, Long coins, 
                                         BigDecimal amount, String paymentMethod, String description) {
        logger.info("开始创建充值订单 - 用户ID: {}, 套餐ID: {}, 金额: {}, 支付方式: {}", 
                   userId, packageId, amount, paymentMethod);
        
        // 参数验证
        validateCreateOrderParams(userId, packageId, coins, amount, paymentMethod);
        
        // 生成订单号
        String orderId = generateOrderId();
        
        try {
            // 创建订单
            RechargeOrder order = new RechargeOrder(
                orderId, userId, packageId, coins, amount, paymentMethod, description
            );
            
            // 保存订单
            RechargeOrder savedOrder = rechargeOrderRepository.save(order);
            logger.info("订单保存成功 - 订单号: {}", orderId);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", savedOrder.getOrderId());
            result.put("amount", savedOrder.getAmount());
            result.put("coins", savedOrder.getCoins());
            result.put("paymentMethod", savedOrder.getPaymentMethod());
            result.put("status", savedOrder.getStatus());
            result.put("expiredAt", savedOrder.getExpiredAt());
            
            // 根据支付方式生成支付参数
            if ("ALIPAY".equals(paymentMethod)) {
                try {
                    logger.info("创建支付宝支付订单 - 订单号: {}", orderId);
                    String alipayOrderInfo = alipayService.createPaymentOrder(savedOrder);
                    result.put("alipayOrderInfo", alipayOrderInfo);
                    logger.info("支付宝订单创建成功 - 订单号: {}", orderId);
                } catch (Exception e) {
                    logger.error("创建支付宝订单失败 - 订单号: {}, 错误: {}", orderId, e.getMessage(), e);
                    throw new PaymentException(
                        PaymentException.ErrorCodes.ORDER_CREATION_FAILED,
                        "创建支付宝订单失败: " + e.getMessage(),
                        orderId,
                        e
                    );
                }
            } else if ("WECHAT".equals(paymentMethod)) {
                try {
                    logger.info("创建微信支付订单 - 订单号: {}", orderId);
                    Map<String, String> wechatPayInfo = wechatPayService.createPaymentOrder(savedOrder);
                    result.put("wechatPayInfo", wechatPayInfo);
                    logger.info("微信支付订单创建成功 - 订单号: {}", orderId);
                } catch (Exception e) {
                    logger.error("创建微信支付订单失败 - 订单号: {}, 错误: {}", orderId, e.getMessage(), e);
                    throw new PaymentException(
                        PaymentException.ErrorCodes.ORDER_CREATION_FAILED,
                        "创建微信支付订单失败: " + e.getMessage(),
                        orderId,
                        e
                    );
                }
            }
            
            logger.info("充值订单创建完成 - 订单号: {}", orderId);
            return result;
            
        } catch (PaymentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("创建充值订单失败 - 用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw new PaymentException(
                PaymentException.ErrorCodes.ORDER_CREATION_FAILED,
                "创建充值订单失败: " + e.getMessage(),
                orderId,
                e
            );
        }
    }
    
    /**
     * 验证创建订单参数
     */
    private void validateCreateOrderParams(Long userId, String packageId, Long coins, 
                                         BigDecimal amount, String paymentMethod) {
        if (userId == null || userId <= 0) {
            throw new PaymentException(
                PaymentException.ErrorCodes.USER_NOT_FOUND,
                PaymentException.ErrorMessages.USER_NOT_FOUND
            );
        }
        
        if (!StringUtils.hasText(packageId)) {
            throw new PaymentException(
                PaymentException.ErrorCodes.INVALID_AMOUNT,
                "套餐ID不能为空"
            );
        }
        
        if (coins == null || coins <= 0) {
            throw new PaymentException(
                PaymentException.ErrorCodes.INVALID_AMOUNT,
                "金币数量必须大于0"
            );
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentException(
                PaymentException.ErrorCodes.INVALID_AMOUNT,
                PaymentException.ErrorMessages.INVALID_AMOUNT
            );
        }
        
        if (!StringUtils.hasText(paymentMethod) || 
            (!paymentMethod.equals("ALIPAY") && !paymentMethod.equals("WECHAT"))) {
            throw new PaymentException(
                PaymentException.ErrorCodes.INVALID_PAYMENT_METHOD,
                PaymentException.ErrorMessages.INVALID_PAYMENT_METHOD
            );
        }
    }
    
    /**
     * 根据订单ID查询订单
     */
    public RechargeOrder getOrderById(String orderId) {
        logger.info("查询订单 - 订单号: {}", orderId);
        
        if (!StringUtils.hasText(orderId)) {
            throw new PaymentException(
                PaymentException.ErrorCodes.ORDER_NOT_FOUND,
                "订单号不能为空"
            );
        }
        
        RechargeOrder order = rechargeOrderRepository.findById(orderId).orElse(null);
        if (order == null) {
            logger.warn("订单不存在 - 订单号: {}", orderId);
            throw new PaymentException(
                PaymentException.ErrorCodes.ORDER_NOT_FOUND,
                PaymentException.ErrorMessages.ORDER_NOT_FOUND,
                orderId
            );
        }
        
        logger.info("订单查询成功 - 订单号: {}, 状态: {}", orderId, order.getStatus());
        return order;
    }
    
    /**
     * 查询订单状态并同步支付平台状态
     */
    public Map<String, Object> queryOrderStatus(String orderId) {
        logger.info("查询订单状态 - 订单号: {}", orderId);
        
        RechargeOrder order = getOrderById(orderId);
        
        // 如果订单未支付且未过期，尝试同步支付平台状态
        if ("PENDING".equals(order.getStatus()) && 
            order.getExpiredAt().isAfter(LocalDateTime.now())) {
            
            try {
                syncPaymentStatus(order);
            } catch (Exception e) {
                logger.warn("同步支付状态失败 - 订单号: {}, 错误: {}", orderId, e.getMessage());
            }
        }
        
        // 重新查询订单（可能已更新状态）
        order = rechargeOrderRepository.findById(orderId).orElse(order);
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getOrderId());
        result.put("userId", order.getUserId());
        result.put("amount", order.getAmount());
        result.put("coins", order.getCoins());
        result.put("status", order.getStatus());
        result.put("paymentMethod", order.getPaymentMethod());
        result.put("createdAt", order.getCreatedAt());
        result.put("updatedAt", order.getUpdatedAt());
        result.put("expiredAt", order.getExpiredAt());
        result.put("description", order.getDescription());
        
        // 如果有第三方交易号，也返回
        if (StringUtils.hasText(order.getThirdPartyTransactionId())) {
            result.put("thirdPartyTradeNo", order.getThirdPartyTransactionId());
        }
        
        logger.info("订单状态查询完成 - 订单号: {}, 状态: {}", orderId, order.getStatus());
        return result;
    }
    
    /**
     * 同步支付平台状态
     */
    private void syncPaymentStatus(RechargeOrder order) {
        String paymentMethod = order.getPaymentMethod();
        String orderId = order.getOrderId();
        
        logger.info("同步支付状态 - 订单号: {}, 支付方式: {}", orderId, paymentMethod);
        
        try {
            if ("ALIPAY".equals(paymentMethod)) {
                // 调用支付宝查询接口
                Map<String, String> queryResult = alipayService.queryPaymentStatus(orderId);
                if (queryResult != null && "TRADE_SUCCESS".equals(queryResult.get("trade_status"))) {
                    // 更新订单状态为已支付
                    updateOrderStatus(order, "SUCCESS", queryResult.get("trade_no"));
                    logger.info("支付宝订单状态同步成功 - 订单号: {}, 状态: 已支付", orderId);
                }
            } else if ("WECHAT".equals(paymentMethod)) {
                // 调用微信支付查询接口
                Map<String, String> queryResult = wechatPayService.queryPaymentStatus(orderId);
                if (queryResult != null && "SUCCESS".equals(queryResult.get("trade_state"))) {
                    // 更新订单状态为已支付
                    updateOrderStatus(order, "SUCCESS", queryResult.get("transaction_id"));
                    logger.info("微信支付订单状态同步成功 - 订单号: {}, 状态: 已支付", orderId);
                }
            }
        } catch (Exception e) {
            logger.error("同步支付状态失败 - 订单号: {}, 错误: {}", orderId, e.getMessage(), e);
            throw new PaymentException(
                PaymentException.ErrorCodes.NETWORK_ERROR,
                "同步支付状态失败: " + e.getMessage(),
                orderId,
                e
            );
        }
    }
    
    /**
     * 更新订单状态
     */
    @Transactional
    public void updateOrderStatus(RechargeOrder order, String status, String thirdPartyTradeNo) {
        logger.info("更新订单状态 - 订单号: {}, 新状态: {}", order.getOrderId(), status);
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        if (StringUtils.hasText(thirdPartyTradeNo)) {
            order.setThirdPartyTransactionId(thirdPartyTradeNo);
        }
        
        if ("SUCCESS".equals(status)) {
            order.setPaidAt(LocalDateTime.now());
            
            // 更新用户钱包余额
            try {
                walletService.addBalance(order.getUserId(), order.getCoins());
                logger.info("钱包余额更新成功 - 用户ID: {}, 增加金币: {}", 
                          order.getUserId(), order.getCoins());
            } catch (Exception e) {
                logger.error("更新钱包余额失败 - 用户ID: {}, 金币: {}, 错误: {}", 
                           order.getUserId(), order.getCoins(), e.getMessage(), e);
                throw new PaymentException(
                    PaymentException.ErrorCodes.WALLET_UPDATE_FAILED,
                    PaymentException.ErrorMessages.WALLET_UPDATE_FAILED,
                    order.getOrderId(),
                    e
                );
            }
        }
        
        rechargeOrderRepository.save(order);
        logger.info("订单状态更新完成 - 订单号: {}, 状态: {}", order.getOrderId(), status);
    }
    
    /**
     * 获取用户充值订单列表
     */
    public Page<RechargeOrder> getUserOrders(Long userId, Pageable pageable) {
        return rechargeOrderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    /**
     * 处理支付宝支付回调
     */
    @Transactional
    public boolean handleAlipayCallback(Map<String, String> params) {
        try {
            // 使用支付宝服务处理回调
            if (!alipayService.handleCallback(params)) {
                System.err.println("支付宝服务处理回调失败");
                return false;
            }
            
            String outTradeNo = params.get("out_trade_no"); // 商户订单号
            String tradeNo = params.get("trade_no"); // 支付宝交易号
            String tradeStatus = params.get("trade_status"); // 交易状态
            String totalAmount = params.get("total_amount"); // 交易金额
            
            System.out.println("处理支付宝回调 - 订单号: " + outTradeNo + ", 状态: " + tradeStatus);
            
            RechargeOrder order = rechargeOrderRepository.findById(outTradeNo).orElse(null);
            if (order == null) {
                System.err.println("订单不存在: " + outTradeNo);
                return false;
            }
            
            // 处理支付成功
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                return processPaymentSuccess(order, tradeNo, new BigDecimal(totalAmount));
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("处理支付宝回调异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 处理微信支付回调
     */
    @Transactional
    public boolean handleWechatCallback(String xmlData) {
        try {
            System.out.println("=== 微信支付回调 ===");
            System.out.println("回调数据: " + xmlData);
            
            // 使用微信支付服务处理回调
            return wechatPayService.handleCallback(xmlData);
            
        } catch (Exception e) {
            System.err.println("微信支付回调处理失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 取消订单
     */
    @Transactional
    public boolean cancelOrder(String orderId, Long userId) {
        try {
            RechargeOrder order = rechargeOrderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return false;
            }
            
            // 验证订单所有权
            if (!order.getUserId().equals(userId)) {
                return false;
            }
            
            // 只能取消待支付的订单
            if (!"PENDING".equals(order.getStatus())) {
                return false;
            }
            
            order.setStatus("CANCELLED");
            order.setUpdatedAt(LocalDateTime.now());
            rechargeOrderRepository.save(order);
            
            System.out.println("订单取消成功: " + orderId);
            return true;
            
        } catch (Exception e) {
            System.err.println("取消订单失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 处理支付成功
     */
    private boolean processPaymentSuccess(RechargeOrder order, String thirdPartyTransactionId, BigDecimal paidAmount) {
        try {
            // 检查订单状态
            if (!"PENDING".equals(order.getStatus())) {
                System.out.println("订单状态不是待支付: " + order.getStatus());
                return true; // 已处理过的订单返回成功
            }
            
            // 验证金额
            if (order.getAmount().compareTo(paidAmount) != 0) {
                System.err.println("支付金额不匹配: 订单金额=" + order.getAmount() + ", 支付金额=" + paidAmount);
                return false;
            }
            
            // 更新订单状态
            order.setStatus("SUCCESS");
            order.setThirdPartyTransactionId(thirdPartyTransactionId);
            order.setPaidAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            rechargeOrderRepository.save(order);
            
            // 给用户钱包充值
            boolean rechargeSuccess = walletService.recharge(
                order.getUserId(), 
                new BigDecimal(order.getCoins()), 
                "充值" + order.getCoins() + "金币"
            );
            
            if (!rechargeSuccess) {
                System.err.println("钱包充值失败，订单ID: " + order.getOrderId());
                // 回滚订单状态
                order.setStatus("FAILED");
                rechargeOrderRepository.save(order);
                return false;
            }
            
            // 更新用户财富值
            try {
                wealthLevelService.updateWealthValue(order.getUserId(), order.getAmount());
                System.out.println("财富值更新成功 - 用户: " + order.getUserId() + ", 金额: " + order.getAmount());
            } catch (Exception e) {
                System.err.println("财富值更新失败 - 用户: " + order.getUserId() + ", 错误: " + e.getMessage());
                // 财富值更新失败不影响充值成功，只记录日志
            }
            
            System.out.println("充值成功 - 订单: " + order.getOrderId() + ", 用户: " + order.getUserId() + ", 金币: " + order.getCoins());
            return true;
            
        } catch (Exception e) {
            System.err.println("处理支付成功回调异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderId() {
        return "RC" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
}
