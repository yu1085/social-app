package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.VipLevelDTO;
import com.example.socialmeet.dto.VipSubscriptionDTO;
import com.example.socialmeet.dto.PaymentOrderDTO;
import com.example.socialmeet.dto.VipPaymentOrderDTO;
import com.example.socialmeet.service.VipService;
import com.example.socialmeet.service.PaymentService;
import com.example.socialmeet.service.AlipayService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/vip")
@CrossOrigin(originPatterns = "*")
public class VipController {
    
    @Autowired
    private VipService vipService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private AlipayService alipayService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/levels")
    public ResponseEntity<ApiResponse<List<VipLevelDTO>>> getVipLevels() {
        try {
            List<VipLevelDTO> levels = vipService.getAllVipLevels();
            return ResponseEntity.ok(ApiResponse.success(levels));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取VIP等级失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/levels/{id}")
    public ResponseEntity<ApiResponse<VipLevelDTO>> getVipLevelById(@PathVariable Long id) {
        try {
            VipLevelDTO level = vipService.getVipLevelById(id);
            if (level == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("VIP等级不存在"));
            }
            return ResponseEntity.ok(ApiResponse.success(level));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取VIP等级失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<VipSubscriptionDTO>> subscribeVip(
            @RequestHeader("Authorization") String token,
            @RequestParam Long vipLevelId) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            VipSubscriptionDTO subscription = vipService.subscribeVip(userId, vipLevelId);
            return ResponseEntity.ok(ApiResponse.success(subscription));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("订阅VIP失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<VipSubscriptionDTO>> getCurrentVipSubscription(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            VipSubscriptionDTO subscription = vipService.getCurrentVipSubscription(userId);
            return ResponseEntity.ok(ApiResponse.success(subscription));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取VIP订阅失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<VipSubscriptionDTO>>> getVipHistory(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            List<VipSubscriptionDTO> history = vipService.getUserVipHistory(userId);
            return ResponseEntity.ok(ApiResponse.success(history));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取VIP历史失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkVipStatus(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            boolean isVip = vipService.isVipUser(userId);
            return ResponseEntity.ok(ApiResponse.success(isVip));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("检查VIP状态失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/level")
    public ResponseEntity<ApiResponse<Integer>> getVipLevel(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Integer level = vipService.getVipLevel(userId);
            return ResponseEntity.ok(ApiResponse.success(level));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取VIP等级失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/create-payment-order")
    public ResponseEntity<ApiResponse<VipPaymentOrderDTO>> createVipPaymentOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody java.util.Map<String, Object> request) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Long vipLevelId = Long.valueOf(request.get("vipLevelId").toString());
            String paymentMethod = request.get("paymentMethod").toString();
            Double amount = Double.valueOf(request.get("amount").toString());
            
            // 创建VIP支付订单
            PaymentOrderDTO order = paymentService.createPaymentOrder(
                userId, 
                "VIP_" + vipLevelId, 
                BigDecimal.valueOf(amount), 
                paymentMethod
            );
            
            // 获取VIP等级信息
            VipLevelDTO vipLevel = vipService.getVipLevelById(vipLevelId);
            String vipLevelName = vipLevel != null ? vipLevel.getName() : "VIP等级" + vipLevelId;
            
            // 生成支付字符串
            String orderInfo = "";
            if ("ALIPAY".equals(paymentMethod)) {
                try {
                    // 创建临时的充值订单用于生成支付宝支付字符串
                    com.example.socialmeet.entity.RechargeOrder rechargeOrder = new com.example.socialmeet.entity.RechargeOrder();
                    rechargeOrder.setOrderId(order.getOrderNo());
                    rechargeOrder.setAmount(order.getAmount());
                    rechargeOrder.setCoins(0L); // VIP订单不需要金币
                    rechargeOrder.setDescription("VIP会员订阅 - " + vipLevelName);
                    
                    System.out.println("开始生成支付宝支付字符串，订单号: " + order.getOrderNo());
                    orderInfo = alipayService.createPaymentOrder(rechargeOrder);
                    System.out.println("支付宝支付字符串生成成功，长度: " + (orderInfo != null ? orderInfo.length() : 0));
                } catch (Exception e) {
                    System.err.println("生成支付宝支付字符串失败: " + e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.badRequest().body(ApiResponse.error("生成支付宝支付字符串失败: " + e.getMessage()));
                }
            } else {
                orderInfo = "微信支付暂不支持";
            }
            
            // 创建VIP支付订单DTO
            VipPaymentOrderDTO vipOrder = new VipPaymentOrderDTO(
                order.getOrderNo(),
                order.getAmount(),
                order.getPaymentMethod(),
                orderInfo,
                vipLevelId,
                vipLevelName
            );
            
            return ResponseEntity.ok(ApiResponse.success(vipOrder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("创建VIP支付订单失败: " + e.getMessage()));
        }
    }
}
