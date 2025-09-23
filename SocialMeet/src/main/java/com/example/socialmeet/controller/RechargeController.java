package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.entity.RechargeOrder;
import com.example.socialmeet.service.RechargeService;
import com.example.socialmeet.exception.PaymentException;
import com.example.socialmeet.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 充值订单控制器
 */
@RestController
@RequestMapping("/api/recharge")
@CrossOrigin(originPatterns = "*")
public class RechargeController {
    
    private static final Logger logger = LoggerFactory.getLogger(RechargeController.class);
    
    @Autowired
    private RechargeService rechargeService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 创建充值订单
     */
    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> orderData) {
        try {
            System.out.println("=== 创建充值订单请求 ===");
            System.out.println("Authorization: " + authHeader);
            System.out.println("订单数据: " + orderData);
            
            // 验证JWT token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(ApiResponse.error("未授权访问"));
            }
            
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(ApiResponse.error("Token无效或已过期"));
            }
            
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            // 提取订单参数
            String packageId = (String) orderData.get("packageId");
            Object coinsObj = orderData.get("coins");
            Object amountObj = orderData.get("amount");
            String paymentMethod = (String) orderData.get("paymentMethod");
            String description = (String) orderData.get("description");
            
            if (packageId == null || coinsObj == null || amountObj == null || paymentMethod == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("订单参数不完整"));
            }
            
            Long coins = Long.valueOf(coinsObj.toString());
            BigDecimal amount = new BigDecimal(amountObj.toString());
            
            // 验证支付方式
            if (!paymentMethod.equals("ALIPAY") && !paymentMethod.equals("WECHAT")) {
                return ResponseEntity.badRequest().body(ApiResponse.error("不支持的支付方式"));
            }
            
            // 创建订单
            Map<String, Object> result = rechargeService.createOrder(
                userId, packageId, coins, amount, paymentMethod, description
            );
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            System.err.println("创建充值订单失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("创建订单失败: " + e.getMessage()));
        }
    }
    
    /**
     * 查询订单状态（增强版，包含状态同步）
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String orderId) {
        try {
            logger.info("查询订单请求 - 订单号: {}", orderId);
            
            // 验证JWT token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(ApiResponse.error("未授权访问"));
            }
            
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(ApiResponse.error("Token无效或已过期"));
            }
            
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            // 查询订单状态（包含状态同步）
            Map<String, Object> orderStatus = rechargeService.queryOrderStatus(orderId);
            
            // 验证订单所有权
            Long orderUserId = (Long) orderStatus.get("userId");
            if (!orderUserId.equals(userId)) {
                logger.warn("用户尝试访问其他用户的订单 - 用户ID: {}, 订单号: {}", userId, orderId);
                return ResponseEntity.status(403).body(ApiResponse.error("无权访问此订单"));
            }
            
            logger.info("订单查询成功 - 订单号: {}, 状态: {}", orderId, orderStatus.get("status"));
            return ResponseEntity.ok(ApiResponse.success(orderStatus));
            
        } catch (PaymentException e) {
            logger.warn("查询订单失败 - 订单号: {}, 错误: {}", orderId, e.getMessage());
            
            if (PaymentException.ErrorCodes.ORDER_NOT_FOUND.equals(e.getErrorCode())) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
            
        } catch (Exception e) {
            logger.error("查询订单异常 - 订单号: {}, 错误: {}", orderId, e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error("查询订单失败: " + e.getMessage()));
        }
    }
    
    /**
     * 查询订单基本信息（轻量版）
     */
    @GetMapping("/order/{orderId}/basic")
    public ResponseEntity<ApiResponse<RechargeOrder>> getOrderBasic(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String orderId) {
        try {
            logger.info("查询订单基本信息 - 订单号: {}", orderId);
            
            // 验证JWT token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(ApiResponse.error("未授权访问"));
            }
            
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(ApiResponse.error("Token无效或已过期"));
            }
            
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            RechargeOrder order = rechargeService.getOrderById(orderId);
            
            // 验证订单所有权
            if (!order.getUserId().equals(userId)) {
                logger.warn("用户尝试访问其他用户的订单 - 用户ID: {}, 订单号: {}", userId, orderId);
                return ResponseEntity.status(403).body(ApiResponse.error("无权访问此订单"));
            }
            
            logger.info("订单基本信息查询成功 - 订单号: {}, 状态: {}", orderId, order.getStatus());
            return ResponseEntity.ok(ApiResponse.success(order));
            
        } catch (PaymentException e) {
            logger.warn("查询订单基本信息失败 - 订单号: {}, 错误: {}", orderId, e.getMessage());
            
            if (PaymentException.ErrorCodes.ORDER_NOT_FOUND.equals(e.getErrorCode())) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
            
        } catch (Exception e) {
            logger.error("查询订单基本信息异常 - 订单号: {}, 错误: {}", orderId, e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error("查询订单失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户充值订单列表
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<RechargeOrder>>> getUserOrders(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            String token = authHeader.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<RechargeOrder> orders = rechargeService.getUserOrders(userId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(orders));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取订单列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 支付宝支付回调
     */
    @PostMapping("/callback/alipay")
    public String alipayCallback(@RequestParam Map<String, String> params) {
        try {
            System.out.println("=== 支付宝支付回调 ===");
            System.out.println("回调参数: " + params);
            
            boolean success = rechargeService.handleAlipayCallback(params);
            
            if (success) {
                return "success";
            } else {
                return "fail";
            }
            
        } catch (Exception e) {
            System.err.println("支付宝回调处理失败: " + e.getMessage());
            e.printStackTrace();
            return "fail";
        }
    }
    
    /**
     * 微信支付回调
     */
    @PostMapping("/callback/wechat")
    public String wechatCallback(@RequestBody String xmlData) {
        try {
            System.out.println("=== 微信支付回调 ===");
            System.out.println("回调数据: " + xmlData);
            
            boolean success = rechargeService.handleWechatCallback(xmlData);
            
            if (success) {
                return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            } else {
                return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
            }
            
        } catch (Exception e) {
            System.err.println("微信支付回调处理失败: " + e.getMessage());
            e.printStackTrace();
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[系统异常]]></return_msg></xml>";
        }
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponse<String>> cancelOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String orderId) {
        try {
            String token = authHeader.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            boolean success = rechargeService.cancelOrder(orderId, userId);
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("订单已取消"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("取消订单失败"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("取消订单失败: " + e.getMessage()));
        }
    }
}
