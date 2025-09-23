package com.example.socialmeet.service;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.entity.CurrencyTransaction;
import com.example.socialmeet.entity.VirtualCurrency;
import com.example.socialmeet.repository.CurrencyTransactionRepository;
import com.example.socialmeet.repository.VirtualCurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 虚拟货币服务
 */
@Service
@Slf4j
@Transactional
public class VirtualCurrencyService {
    
    @Autowired
    private VirtualCurrencyRepository virtualCurrencyRepository;
    
    @Autowired
    private CurrencyTransactionRepository currencyTransactionRepository;
    
    /**
     * 获取用户货币余额
     */
    public BigDecimal getUserBalance(Long userId, String currencyType) {
        Optional<VirtualCurrency> currencyOpt = virtualCurrencyRepository.findByUserIdAndCurrencyType(userId, currencyType);
        if (currencyOpt.isPresent()) {
            return currencyOpt.get().getAvailableBalance();
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 获取用户所有货币
     */
    public List<VirtualCurrency> getUserCurrencies(Long userId) {
        return virtualCurrencyRepository.findByUserId(userId);
    }
    
    /**
     * 添加货币
     */
    public boolean addCurrency(Long userId, String currencyType, BigDecimal amount, String description, Long relatedId, String relatedType) {
        try {
            VirtualCurrency currency = getOrCreateCurrency(userId, currencyType);
            BigDecimal balanceBefore = currency.getBalance();
            
            currency.addBalance(amount);
            virtualCurrencyRepository.save(currency);
            
            // 记录交易
            CurrencyTransaction transaction = new CurrencyTransaction(
                userId, currencyType, "EARN", amount, balanceBefore, 
                currency.getBalance(), description, relatedId, relatedType
            );
            currencyTransactionRepository.save(transaction);
            
            log.info("用户 {} 获得 {} {} 货币", userId, amount, currencyType);
            return true;
        } catch (Exception e) {
            log.error("添加货币失败: userId={}, currencyType={}, amount={}", userId, currencyType, amount, e);
            return false;
        }
    }
    
    /**
     * 消费货币
     */
    public boolean consumeCurrency(Long userId, String currencyType, BigDecimal amount, String description, Long relatedId, String relatedType) {
        try {
            VirtualCurrency currency = getOrCreateCurrency(userId, currencyType);
            
            if (!currency.hasEnoughBalance(amount)) {
                log.warn("用户 {} 余额不足: 需要 {}, 可用 {}", userId, amount, currency.getAvailableBalance());
                return false;
            }
            
            BigDecimal balanceBefore = currency.getBalance();
            currency.deductBalance(amount);
            virtualCurrencyRepository.save(currency);
            
            // 记录交易
            CurrencyTransaction transaction = new CurrencyTransaction(
                userId, currencyType, "SPEND", amount, balanceBefore, 
                currency.getBalance(), description, relatedId, relatedType
            );
            currencyTransactionRepository.save(transaction);
            
            log.info("用户 {} 消费 {} {} 货币", userId, amount, currencyType);
            return true;
        } catch (Exception e) {
            log.error("消费货币失败: userId={}, currencyType={}, amount={}", userId, currencyType, amount, e);
            return false;
        }
    }
    
    /**
     * 冻结货币
     */
    public boolean freezeCurrency(Long userId, String currencyType, BigDecimal amount, String description) {
        try {
            VirtualCurrency currency = getOrCreateCurrency(userId, currencyType);
            
            if (!currency.hasEnoughBalance(amount)) {
                return false;
            }
            
            BigDecimal balanceBefore = currency.getBalance();
            currency.freezeAmount(amount);
            virtualCurrencyRepository.save(currency);
            
            // 记录交易
            CurrencyTransaction transaction = new CurrencyTransaction(
                userId, currencyType, "FREEZE", amount, balanceBefore, 
                currency.getBalance(), description, null, null
            );
            currencyTransactionRepository.save(transaction);
            
            return true;
        } catch (Exception e) {
            log.error("冻结货币失败: userId={}, currencyType={}, amount={}", userId, currencyType, amount, e);
            return false;
        }
    }
    
    /**
     * 解冻货币
     */
    public boolean unfreezeCurrency(Long userId, String currencyType, BigDecimal amount, String description) {
        try {
            VirtualCurrency currency = getOrCreateCurrency(userId, currencyType);
            BigDecimal balanceBefore = currency.getBalance();
            
            currency.unfreezeAmount(amount);
            virtualCurrencyRepository.save(currency);
            
            // 记录交易
            CurrencyTransaction transaction = new CurrencyTransaction(
                userId, currencyType, "UNFREEZE", amount, balanceBefore, 
                currency.getBalance(), description, null, null
            );
            currencyTransactionRepository.save(transaction);
            
            return true;
        } catch (Exception e) {
            log.error("解冻货币失败: userId={}, currencyType={}, amount={}", userId, currencyType, amount, e);
            return false;
        }
    }
    
    /**
     * 转账
     */
    public boolean transferCurrency(Long fromUserId, Long toUserId, String currencyType, BigDecimal amount, String description) {
        try {
            // 检查发送方余额
            if (!consumeCurrency(fromUserId, currencyType, amount, "转账给用户" + toUserId, toUserId, "TRANSFER")) {
                return false;
            }
            
            // 给接收方添加货币
            if (!addCurrency(toUserId, currencyType, amount, "收到用户" + fromUserId + "的转账", fromUserId, "TRANSFER")) {
                // 如果添加失败，需要回滚
                addCurrency(fromUserId, currencyType, amount, "转账失败回滚", toUserId, "REFUND");
                return false;
            }
            
            log.info("用户 {} 向用户 {} 转账 {} {}", fromUserId, toUserId, amount, currencyType);
            return true;
        } catch (Exception e) {
            log.error("转账失败: fromUserId={}, toUserId={}, currencyType={}, amount={}", fromUserId, toUserId, currencyType, amount, e);
            return false;
        }
    }
    
    /**
     * 获取用户交易记录
     */
    public Page<CurrencyTransaction> getUserTransactions(Long userId, Pageable pageable) {
        return currencyTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    /**
     * 获取用户指定货币类型的交易记录
     */
    public Page<CurrencyTransaction> getUserTransactionsByCurrency(Long userId, String currencyType, Pageable pageable) {
        return currencyTransactionRepository.findByUserIdAndCurrencyTypeOrderByCreatedAtDesc(userId, currencyType, pageable);
    }
    
    /**
     * 获取或创建货币账户
     */
    private VirtualCurrency getOrCreateCurrency(Long userId, String currencyType) {
        Optional<VirtualCurrency> currencyOpt = virtualCurrencyRepository.findByUserIdAndCurrencyType(userId, currencyType);
        if (currencyOpt.isPresent()) {
            return currencyOpt.get();
        } else {
            VirtualCurrency currency = new VirtualCurrency(userId, currencyType);
            return virtualCurrencyRepository.save(currency);
        }
    }
    
    /**
     * 获取用户统计信息
     */
    public java.util.Map<String, Object> getUserStats(Long userId) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        // 总消费
        BigDecimal totalSpent = currencyTransactionRepository.getTotalSpentByUserId(userId);
        stats.put("totalSpent", totalSpent != null ? totalSpent : BigDecimal.ZERO);
        
        // 总收入
        BigDecimal totalEarned = currencyTransactionRepository.getTotalEarnedByUserId(userId);
        stats.put("totalEarned", totalEarned != null ? totalEarned : BigDecimal.ZERO);
        
        // 今日交易次数
        Long todayTransactions = currencyTransactionRepository.countTodayTransactions(userId, LocalDateTime.now().toLocalDate().atStartOfDay());
        stats.put("todayTransactions", todayTransactions);
        
        return stats;
    }
}
