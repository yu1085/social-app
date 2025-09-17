package com.example.socialmeet.service;

import com.example.socialmeet.dto.PaymentOrderDTO;
import com.example.socialmeet.entity.PaymentOrder;
import com.example.socialmeet.repository.PaymentOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {
    
    @Autowired
    private PaymentOrderRepository paymentOrderRepository;
    
    @Autowired
    private WalletService walletService;
    
    public PaymentOrderDTO createPaymentOrder(Long userId, String type, 
                                            BigDecimal amount, String paymentMethod) {
        PaymentOrder order = new PaymentOrder(userId, type, amount, paymentMethod);
        order = paymentOrderRepository.save(order);
        
        return new PaymentOrderDTO(order);
    }
    
    public PaymentOrderDTO getPaymentOrderByOrderNo(String orderNo) {
        Optional<PaymentOrder> orderOpt = paymentOrderRepository.findByOrderNo(orderNo);
        if (orderOpt.isPresent()) {
            return new PaymentOrderDTO(orderOpt.get());
        }
        return null;
    }
    
    public List<PaymentOrderDTO> getUserPaymentOrders(Long userId) {
        List<PaymentOrder> orders = paymentOrderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream().map(PaymentOrderDTO::new).collect(Collectors.toList());
    }
    
    public List<PaymentOrderDTO> getUserPaymentOrdersByStatus(Long userId, String status) {
        List<PaymentOrder> orders = paymentOrderRepository.findByUserIdAndStatus(userId, status);
        return orders.stream().map(PaymentOrderDTO::new).collect(Collectors.toList());
    }
    
    public boolean processPaymentSuccess(String orderNo, String paymentNo) {
        Optional<PaymentOrder> orderOpt = paymentOrderRepository.findByOrderNo(orderNo);
        if (!orderOpt.isPresent()) {
            return false;
        }
        
        PaymentOrder order = orderOpt.get();
        if (!order.getStatus().equals("PENDING")) {
            return false;
        }
        
        order.setStatus("SUCCESS");
        order.setPaymentNo(paymentNo);
        order = paymentOrderRepository.save(order);
        
        // 处理支付成功后的业务逻辑
        if (order.getType().equals("RECHARGE")) {
            // 充值到钱包
            walletService.recharge(order.getUserId(), order.getAmount(), 
                                 "支付充值: " + order.getOrderNo());
        }
        // 其他类型的支付处理逻辑可以在这里添加
        
        return true;
    }
    
    public boolean processPaymentFailed(String orderNo, String reason) {
        Optional<PaymentOrder> orderOpt = paymentOrderRepository.findByOrderNo(orderNo);
        if (!orderOpt.isPresent()) {
            return false;
        }
        
        PaymentOrder order = orderOpt.get();
        if (!order.getStatus().equals("PENDING")) {
            return false;
        }
        
        order.setStatus("FAILED");
        order = paymentOrderRepository.save(order);
        
        return true;
    }
    
    public boolean cancelPaymentOrder(String orderNo) {
        Optional<PaymentOrder> orderOpt = paymentOrderRepository.findByOrderNo(orderNo);
        if (!orderOpt.isPresent()) {
            return false;
        }
        
        PaymentOrder order = orderOpt.get();
        if (!order.getStatus().equals("PENDING")) {
            return false;
        }
        
        order.setStatus("CANCELLED");
        order = paymentOrderRepository.save(order);
        
        return true;
    }
    
    public void processExpiredOrders() {
        LocalDateTime expiredTime = LocalDateTime.now().minusHours(24); // 24小时前创建的订单
        List<PaymentOrder> expiredOrders = paymentOrderRepository.findExpiredPendingOrders(expiredTime);
        
        for (PaymentOrder order : expiredOrders) {
            order.setStatus("CANCELLED");
            paymentOrderRepository.save(order);
        }
    }
}
