package com.socialmeet.backend.service;

import com.socialmeet.backend.dto.*;
import com.socialmeet.backend.entity.*;
import com.socialmeet.backend.repository.PaymentOrderRepository;
import com.socialmeet.backend.repository.WalletRepository;
import com.socialmeet.backend.util.RequestIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 支付服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class
PaymentService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final WalletRepository walletRepository;
    private final AlipayService alipayService;

    /**
     * 创建支付订单 - 优化版本
     */
    @Transactional
    public AlipayOrderResponse createOrder(Long userId, CreateOrderRequest request, String requestId) {
        try {
            log.info("创建支付订单 - userId: {}, request: {}, requestId: {}", userId, request, requestId);

            // 生成订单ID
            String orderId = RequestIdGenerator.generateOrderId();

            // 创建支付订单
            PaymentOrder order = new PaymentOrder();
            order.setOrderId(orderId);
            order.setUserId(userId);
            order.setPackageId(request.getPackageId());
            order.setCoins(request.getCoins());
            order.setAmount(request.getAmount());
            order.setPaymentMethod(PaymentMethod.fromCode(request.getPaymentMethod()));
            order.setStatus(OrderStatus.PENDING);
            order.setDescription(request.getDescription());
            order.setExpiredAt(LocalDateTime.now().plusMinutes(30)); // 30分钟过期

            // 保存订单
            order = paymentOrderRepository.save(order);

            // 根据支付方式创建支付信息
            AlipayOrderResponse response;
            if (order.getPaymentMethod() == PaymentMethod.ALIPAY) {
                response = alipayService.createOrder(order);
            } else {
                throw new IllegalArgumentException("不支持的支付方式: " + request.getPaymentMethod());
            }

            log.info("支付订单创建成功 - orderId: {}", orderId);
            return response;

        } catch (Exception e) {
            log.error("创建支付订单失败 - userId: {}, request: {}", userId, request, e);
            throw new RuntimeException("创建支付订单失败: " + e.getMessage());
        }
    }

    /**
     * 处理支付宝回调 - 优化版本
     */
    @Transactional
    public void handleAlipayCallback(Map<String, String> params, String requestId) {
        try {
            log.info("处理支付宝回调 - requestId: {}, params: {}", requestId, params);

            // 验证签名
            if (!alipayService.verifyCallback(params)) {
                log.error("支付宝回调签名验证失败 - requestId: {}", requestId);
                return;
            }

            String alipayOutTradeNo = params.get("out_trade_no");
            String tradeStatus = params.get("trade_status");
            String alipayTradeNo = params.get("trade_no");

            // 查找订单
            PaymentOrder order = paymentOrderRepository.findByAlipayOutTradeNo(alipayOutTradeNo)
                    .orElseThrow(() -> new RuntimeException("订单不存在: " + alipayOutTradeNo));

            // 更新订单状态
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                handlePaymentSuccess(order, alipayTradeNo, params);
            } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                handlePaymentClosed(order);
            }

            log.info("支付宝回调处理完成 - orderId: {}", order.getOrderId());

        } catch (Exception e) {
            log.error("处理支付宝回调失败", e);
            throw new RuntimeException("处理支付宝回调失败: " + e.getMessage());
        }
    }

    /**
     * 处理支付成功
     */
    @Transactional
    public void handlePaymentSuccess(PaymentOrder order, String alipayTradeNo, Map<String, String> callbackData) {
        try {
            log.info("处理支付成功 - orderId: {}, alipayTradeNo: {}", order.getOrderId(), alipayTradeNo);

            // 检查订单状态
            if (order.getStatus() != OrderStatus.PENDING) {
                log.warn("订单状态不是待支付，跳过处理 - orderId: {}, status: {}", order.getOrderId(), order.getStatus());
                return;
            }

            // 更新订单状态
            order.setStatus(OrderStatus.SUCCESS);
            order.setAlipayTradeNo(alipayTradeNo);
            order.setPaidAt(LocalDateTime.now());
            order.setCallbackData(mapToString(callbackData));
            paymentOrderRepository.save(order);

            // 更新用户钱包余额
            updateWalletBalance(order);

            log.info("支付成功处理完成 - orderId: {}", order.getOrderId());

        } catch (Exception e) {
            log.error("处理支付成功失败 - orderId: {}", order.getOrderId(), e);
            throw new RuntimeException("处理支付成功失败: " + e.getMessage());
        }
    }

    /**
     * 处理支付关闭
     */
    @Transactional
    public void handlePaymentClosed(PaymentOrder order) {
        try {
            log.info("处理支付关闭 - orderId: {}", order.getOrderId());

            order.setStatus(OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());
            paymentOrderRepository.save(order);

            log.info("支付关闭处理完成 - orderId: {}", order.getOrderId());

        } catch (Exception e) {
            log.error("处理支付关闭失败 - orderId: {}", order.getOrderId(), e);
            throw new RuntimeException("处理支付关闭失败: " + e.getMessage());
        }
    }

    /**
     * 更新钱包余额
     */
    @Transactional
    public void updateWalletBalance(PaymentOrder order) {
        try {
            log.info("更新钱包余额 - userId: {}, coins: {}", order.getUserId(), order.getCoins());

            // 查找或创建钱包
            Wallet wallet = walletRepository.findByUserId(order.getUserId())
                    .orElseGet(() -> createWallet(order.getUserId()));

            // 更新余额
            BigDecimal newBalance = wallet.getBalance().add(BigDecimal.valueOf(order.getCoins()));
            wallet.setBalance(newBalance);
            wallet.setTotalRecharge(wallet.getTotalRecharge().add(order.getAmount()));
            wallet.setTransactionCount(wallet.getTransactionCount() + 1);
            wallet.setLastTransactionAt(LocalDateTime.now());

            walletRepository.save(wallet);

            log.info("钱包余额更新成功 - userId: {}, newBalance: {}", order.getUserId(), newBalance);

        } catch (Exception e) {
            log.error("更新钱包余额失败 - userId: {}, coins: {}", order.getUserId(), order.getCoins(), e);
            throw new RuntimeException("更新钱包余额失败: " + e.getMessage());
        }
    }

    /**
     * 创建钱包
     */
    private Wallet createWallet(Long userId) {
        log.info("创建用户钱包 - userId: {}", userId);

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setTotalRecharge(BigDecimal.ZERO);
        wallet.setTotalConsume(BigDecimal.ZERO);
        wallet.setTransactionCount(0);

        return walletRepository.save(wallet);
    }

    /**
     * 查询订单列表 - 优化版本（支持分页）
     */
    public List<PaymentOrderDTO> getOrderList(Long userId, OrderStatus status, int page, int size) {
        try {
            log.info("查询订单列表 - userId: {}, status: {}, page: {}, size: {}", userId, status, page, size);

            Pageable pageable = PageRequest.of(page, size);
            Page<PaymentOrder> orderPage;
            
            if (status != null) {
                orderPage = paymentOrderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
            } else {
                orderPage = paymentOrderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
            }
            
            List<PaymentOrder> orders = orderPage.getContent();

            return orders.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("查询订单列表失败 - userId: {}, status: {}", userId, status, e);
            throw new RuntimeException("查询订单列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询订单列表（兼容旧版本）
     */
    public List<PaymentOrderDTO> getOrderList(Long userId, OrderStatus status) {
        try {
            List<PaymentOrder> orders;
            if (status != null) {
                orders = paymentOrderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
            } else {
                orders = paymentOrderRepository.findByUserIdOrderByCreatedAtDesc(userId);
            }

            return orders.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("查询订单列表失败 - userId: {}, status: {}", userId, status, e);
            throw new RuntimeException("查询订单列表失败: " + e.getMessage());
        }
    }

    /**
     * 查询订单详情
     */
    public PaymentOrderDTO getOrderDetail(String orderId) {
        try {
            PaymentOrder order = paymentOrderRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("订单不存在: " + orderId));

            return convertToDTO(order);

        } catch (Exception e) {
            log.error("查询订单详情失败 - orderId: {}", orderId, e);
            throw new RuntimeException("查询订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(String orderId) {
        try {
            log.info("取消订单 - orderId: {}", orderId);

            PaymentOrder order = paymentOrderRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("订单不存在: " + orderId));

            if (order.getStatus() != OrderStatus.PENDING) {
                throw new RuntimeException("订单状态不允许取消: " + order.getStatus());
            }

            order.setStatus(OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());
            paymentOrderRepository.save(order);

            log.info("订单取消成功 - orderId: {}", orderId);

        } catch (Exception e) {
            log.error("取消订单失败 - orderId: {}", orderId, e);
            throw new RuntimeException("取消订单失败: " + e.getMessage());
        }
    }

    /**
     * 生成订单ID
     */
    private String generateOrderId() {
        return "ORDER_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 转换为DTO
     */
    private PaymentOrderDTO convertToDTO(PaymentOrder order) {
        PaymentOrderDTO dto = new PaymentOrderDTO();
        dto.setId(order.getId());
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUserId());
        dto.setPackageId(order.getPackageId());
        dto.setCoins(order.getCoins());
        dto.setAmount(order.getAmount());
        dto.setPaymentMethod(order.getPaymentMethod().getCode());
        dto.setAlipayTradeNo(order.getAlipayTradeNo());
        dto.setAlipayOutTradeNo(order.getAlipayOutTradeNo());
        dto.setStatus(order.getStatus().getCode());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setPaidAt(order.getPaidAt());
        dto.setExpiredAt(order.getExpiredAt());
        dto.setDescription(order.getDescription());
        dto.setTransactionId(order.getTransactionId());
        dto.setFailureReason(order.getFailureReason());
        return dto;
    }

    /**
     * Map转字符串
     */
    private String mapToString(Map<String, String> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
    }

    /**
     * 根据订单ID获取订单详情
     */
    public PaymentOrderDTO getOrderById(Long orderId) {
        PaymentOrder order = paymentOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }
}
