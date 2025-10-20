package com.socialmeet.backend.controller;

import com.socialmeet.backend.dto.*;
import com.socialmeet.backend.entity.OrderStatus;
import com.socialmeet.backend.enums.PaymentErrorCode;
import com.socialmeet.backend.security.JwtUtil;
import com.socialmeet.backend.service.PaymentService;
import com.socialmeet.backend.util.RateLimiter;
import com.socialmeet.backend.util.ReplayAttackPrevention;
import com.socialmeet.backend.util.RequestIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 支付控制器 - 优化版本
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtUtil jwtUtil;
    private final RateLimiter rateLimiter;
    private final ReplayAttackPrevention replayAttackPrevention;

    /**
     * 创建支付订单 - 优化版本
     */
    @PostMapping("/orders")
    public PaymentApiResponse<AlipayOrderResponse> createPaymentOrder(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest) {
        
        String requestId = RequestIdGenerator.generateRequestId();
        log.info("创建支付订单请求 - requestId: {}, request: {}", requestId, request);
        
        try {
            // 1. 验证用户身份
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                log.warn("未提供有效的认证令牌 - requestId: {}", requestId);
                return PaymentApiResponse.error(PaymentErrorCode.INVALID_TOKEN, requestId);
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                log.warn("用户ID无效 - requestId: {}", requestId);
                return PaymentApiResponse.error(PaymentErrorCode.USER_NOT_FOUND, requestId);
            }

            // 2. 频率限制检查
            if (rateLimiter.isOrderCreateRateLimited(userId)) {
                log.warn("订单创建频率限制 - userId: {}, requestId: {}", userId, requestId);
                return PaymentApiResponse.error(PaymentErrorCode.RATE_LIMIT_EXCEEDED, requestId);
            }

            // 3. 防重放攻击检查
            String clientIp = getClientIp(httpRequest);
            if (!replayAttackPrevention.validateRequest(userId, request.toString(), 
                    request.getTimestamp(), request.getSignature())) {
                log.warn("防重放攻击验证失败 - userId: {}, requestId: {}", userId, requestId);
                return PaymentApiResponse.error(PaymentErrorCode.DUPLICATE_REQUEST, requestId);
            }

            // 4. 创建订单
            AlipayOrderResponse response = paymentService.createOrder(userId, request, requestId);

            log.info("支付订单创建成功 - userId: {}, orderId: {}, requestId: {}", 
                    userId, response.getOrderId(), requestId);
            return PaymentApiResponse.success(response, requestId);

        } catch (IllegalArgumentException e) {
            log.warn("参数错误 - requestId: {}, error: {}", requestId, e.getMessage());
            return PaymentApiResponse.error(PaymentErrorCode.INVALID_PARAMETER, e.getMessage(), requestId);
        } catch (Exception e) {
            log.error("创建支付订单失败 - requestId: {}", requestId, e);
            return PaymentApiResponse.error(PaymentErrorCode.ORDER_CREATE_FAILED, requestId);
        }
    }

    /**
     * 创建支付宝订单 - 兼容Android端API
     */
    @PostMapping("/alipay/create")
    public PaymentApiResponse<AlipayOrderResponse> createAlipayOrder(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest) {
        
        String requestId = RequestIdGenerator.generateRequestId();
        log.info("创建支付宝订单请求 - requestId: {}, request: {}", requestId, request);
        
        try {
            // 1. 验证用户身份
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                log.warn("未提供有效的认证令牌 - requestId: {}", requestId);
                return PaymentApiResponse.error(PaymentErrorCode.INVALID_TOKEN, requestId);
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                log.warn("用户ID无效 - requestId: {}", requestId);
                return PaymentApiResponse.error(PaymentErrorCode.USER_NOT_FOUND, requestId);
            }

            // 2. 频率限制检查
            if (rateLimiter.isOrderCreateRateLimited(userId)) {
                log.warn("订单创建频率限制 - userId: {}, requestId: {}", userId, requestId);
                return PaymentApiResponse.error(PaymentErrorCode.RATE_LIMIT_EXCEEDED, requestId);
            }

            // 3. 防重放攻击检查
            String clientIp = getClientIp(httpRequest);
            if (!replayAttackPrevention.validateRequest(userId, request.toString(), 
                    request.getTimestamp(), request.getSignature())) {
                log.warn("防重放攻击验证失败 - userId: {}, requestId: {}", userId, requestId);
                return PaymentApiResponse.error(PaymentErrorCode.DUPLICATE_REQUEST, requestId);
            }

            // 4. 创建订单
            AlipayOrderResponse response = paymentService.createOrder(userId, request, requestId);

            log.info("支付宝订单创建成功 - userId: {}, orderId: {}, requestId: {}", 
                    userId, response.getOrderId(), requestId);
            return PaymentApiResponse.success(response, requestId);

        } catch (IllegalArgumentException e) {
            log.warn("参数错误 - requestId: {}, error: {}", requestId, e.getMessage());
            return PaymentApiResponse.error(PaymentErrorCode.INVALID_PARAMETER, e.getMessage(), requestId);
        } catch (Exception e) {
            log.error("创建支付宝订单失败 - requestId: {}", requestId, e);
            return PaymentApiResponse.error(PaymentErrorCode.ORDER_CREATE_FAILED, requestId);
        }
    }

    /**
     * 支付宝回调接口 - 优化版本
     */
    @PostMapping("/alipay/callback")
    public String handleAlipayCallback(@RequestBody Map<String, String> params, 
                                     HttpServletRequest request) {
        String requestId = RequestIdGenerator.generateRequestId();
        log.info("收到支付宝回调 - requestId: {}, params: {}", requestId, params);
        
        try {
            // 1. 验证回调数据
            if (params == null || params.isEmpty()) {
                log.warn("支付宝回调数据为空 - requestId: {}", requestId);
                return "fail";
            }

            // 2. 记录客户端IP
            String clientIp = getClientIp(request);
            log.info("支付宝回调来源IP: {} - requestId: {}", clientIp, requestId);

            // 3. 处理回调
            paymentService.handleAlipayCallback(params, requestId);

            log.info("支付宝回调处理成功 - requestId: {}", requestId);
            return "success";

        } catch (Exception e) {
            log.error("处理支付宝回调失败 - requestId: {}", requestId, e);
            return "fail";
        }
    }

    /**
     * 支付宝同步返回接口 - 优化版本
     */
    @GetMapping("/alipay/return")
    public String handleAlipayReturn(@RequestParam Map<String, String> params,
                                   HttpServletRequest request) {
        String requestId = RequestIdGenerator.generateRequestId();
        log.info("收到支付宝同步返回 - requestId: {}, params: {}", requestId, params);
        
        try {
            // 1. 验证参数
            if (params == null || params.isEmpty()) {
                log.warn("支付宝同步返回参数为空 - requestId: {}", requestId);
                return "支付处理失败";
            }

            // 2. 记录客户端IP
            String clientIp = getClientIp(request);
            log.info("支付宝同步返回来源IP: {} - requestId: {}", clientIp, requestId);

            // 3. 处理同步返回
            paymentService.handleAlipayCallback(params, requestId);

            log.info("支付宝同步返回处理成功 - requestId: {}", requestId);
            return "支付处理完成";

        } catch (Exception e) {
            log.error("处理支付宝同步返回失败 - requestId: {}", requestId, e);
            return "支付处理失败";
        }
    }

    /**
     * 查询订单列表 - 优化版本
     */
    @GetMapping("/orders")
    public PaymentApiResponse<List<PaymentOrderDTO>> getOrderList(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        String requestId = RequestIdGenerator.generateRequestId();
        log.info("查询订单列表请求 - requestId: {}, status: {}, page: {}, size: {}", 
                requestId, status, page, size);
        
        try {
            // 1. 验证用户身份
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                log.warn("未提供有效的认证令牌 - requestId: {}", requestId);
                return PaymentApiResponse.error(PaymentErrorCode.INVALID_TOKEN, requestId);
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                log.warn("用户ID无效 - requestId: {}", requestId);
                return PaymentApiResponse.error(PaymentErrorCode.USER_NOT_FOUND, requestId);
            }

            // 2. 参数验证
            if (page < 0 || size <= 0 || size > 100) {
                log.warn("分页参数无效 - requestId: {}, page: {}, size: {}", requestId, page, size);
                return PaymentApiResponse.error(PaymentErrorCode.INVALID_PARAMETER, 
                        "分页参数无效", requestId);
            }

            // 3. 解析订单状态
            OrderStatus orderStatus = null;
            if (status != null && !status.isEmpty()) {
                try {
                    orderStatus = OrderStatus.fromCode(status);
                } catch (IllegalArgumentException e) {
                    log.warn("无效的订单状态 - requestId: {}, status: {}", requestId, status);
                    return PaymentApiResponse.error(PaymentErrorCode.INVALID_PARAMETER, 
                            "无效的订单状态: " + status, requestId);
                }
            }

            // 4. 查询订单列表
            List<PaymentOrderDTO> orders = paymentService.getOrderList(userId, orderStatus, page, size);

            log.info("查询订单列表成功 - userId: {}, count: {}, requestId: {}", 
                    userId, orders.size(), requestId);
            return PaymentApiResponse.success(orders, requestId);

        } catch (Exception e) {
            log.error("查询订单列表失败 - requestId: {}", requestId, e);
            return PaymentApiResponse.error(PaymentErrorCode.SYSTEM_ERROR, requestId);
        }
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/orders/{orderId}")
    public ApiResponse<PaymentOrderDTO> getOrderDetail(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String orderId) {
        try {
            log.info("查询订单详情 - orderId: {}", orderId);

            // 验证用户身份
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户ID无效");
            }

            // 查询订单详情
            PaymentOrderDTO order = paymentService.getOrderDetail(orderId);

            // 验证订单归属
            if (!order.getUserId().equals(userId)) {
                return ApiResponse.error("无权访问该订单");
            }

            log.info("查询订单详情成功 - orderId: {}", orderId);
            return ApiResponse.success(order);

        } catch (Exception e) {
            log.error("查询订单详情失败 - orderId: {}", orderId, e);
            return ApiResponse.error("查询订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/orders/{orderId}/cancel")
    public ApiResponse<String> cancelOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String orderId) {
        try {
            log.info("取消订单 - orderId: {}", orderId);

            // 验证用户身份
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户ID无效");
            }

            // 验证订单归属
            PaymentOrderDTO order = paymentService.getOrderDetail(orderId);
            if (!order.getUserId().equals(userId)) {
                return ApiResponse.error("无权操作该订单");
            }

            // 取消订单
            paymentService.cancelOrder(orderId);

            log.info("订单取消成功 - orderId: {}", orderId);
            return ApiResponse.success("订单取消成功");

        } catch (Exception e) {
            log.error("取消订单失败 - orderId: {}", orderId, e);
            return ApiResponse.error("取消订单失败: " + e.getMessage());
        }
    }

    /**
     * 查询支付统计
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getPaymentStatistics(
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("查询支付统计");

            // 验证用户身份
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户ID无效");
            }

            // 查询统计信息
            List<PaymentOrderDTO> allOrders = paymentService.getOrderList(userId, null);
            List<PaymentOrderDTO> successOrders = paymentService.getOrderList(userId, OrderStatus.SUCCESS);

            long totalAmount = successOrders.stream()
                    .mapToLong(order -> order.getAmount().longValue())
                    .sum();

            long totalCoins = successOrders.stream()
                    .mapToLong(PaymentOrderDTO::getCoins)
                    .sum();

            Map<String, Object> statistics = Map.of(
                    "totalOrders", allOrders.size(),
                    "successOrders", successOrders.size(),
                    "totalAmount", totalAmount,
                    "totalCoins", totalCoins,
                    "successRate", allOrders.isEmpty() ? 0.0 : (double) successOrders.size() / allOrders.size() * 100
            );

            log.info("查询支付统计成功 - userId: {}", userId);
            return ApiResponse.success(statistics);

        } catch (Exception e) {
            log.error("查询支付统计失败", e);
            return ApiResponse.error("查询支付统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
