package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.PaymentOrderDTO;
import com.example.socialmeet.entity.PaymentOrder;
import com.example.socialmeet.service.PaymentService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PaymentOrderDTO>> createPaymentOrder(
            @RequestHeader("Authorization") String token,
            @RequestParam String type,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentMethod) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            PaymentOrder.OrderType orderType = PaymentOrder.OrderType.valueOf(type.toUpperCase());
            PaymentOrder.PaymentMethod method = PaymentOrder.PaymentMethod.valueOf(paymentMethod.toUpperCase());
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(ApiResponse.error("支付金额必须大于0"));
            }
            
            PaymentOrderDTO order = paymentService.createPaymentOrder(userId, orderType, amount, method);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("创建支付订单失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/order/{orderNo}")
    public ResponseEntity<ApiResponse<PaymentOrderDTO>> getPaymentOrderByOrderNo(@PathVariable String orderNo) {
        try {
            PaymentOrderDTO order = paymentService.getPaymentOrderByOrderNo(orderNo);
            if (order == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("支付订单不存在"));
            }
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取支付订单失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<PaymentOrderDTO>>> getUserPaymentOrders(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            List<PaymentOrderDTO> orders = paymentService.getUserPaymentOrders(userId);
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取支付订单失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/my-orders/status/{status}")
    public ResponseEntity<ApiResponse<List<PaymentOrderDTO>>> getUserPaymentOrdersByStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable String status) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            PaymentOrder.OrderStatus orderStatus = PaymentOrder.OrderStatus.valueOf(status.toUpperCase());
            List<PaymentOrderDTO> orders = paymentService.getUserPaymentOrdersByStatus(userId, orderStatus);
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取支付订单失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/success")
    public ResponseEntity<ApiResponse<String>> processPaymentSuccess(
            @RequestParam String orderNo,
            @RequestParam String paymentNo) {
        try {
            boolean success = paymentService.processPaymentSuccess(orderNo, paymentNo);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("支付成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("支付处理失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("支付处理失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/failed")
    public ResponseEntity<ApiResponse<String>> processPaymentFailed(
            @RequestParam String orderNo,
            @RequestParam(required = false) String reason) {
        try {
            boolean success = paymentService.processPaymentFailed(orderNo, reason);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("支付失败处理完成"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("支付失败处理失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("支付失败处理失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/cancel/{orderNo}")
    public ResponseEntity<ApiResponse<String>> cancelPaymentOrder(@PathVariable String orderNo) {
        try {
            boolean success = paymentService.cancelPaymentOrder(orderNo);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("取消支付成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("取消支付失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("取消支付失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/methods")
    public ResponseEntity<ApiResponse<List<String>>> getPaymentMethods() {
        try {
            List<String> methods = List.of("ALIPAY", "WECHAT");
            return ResponseEntity.ok(ApiResponse.success(methods));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取支付方式失败: " + e.getMessage()));
        }
    }
}
