package com.example.socialmeet.service;

import com.example.socialmeet.dto.WalletDTO;
import com.example.socialmeet.entity.Transaction;
import com.example.socialmeet.entity.Wallet;
import com.example.socialmeet.repository.TransactionRepository;
import com.example.socialmeet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class WalletService {
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    public WalletDTO getWalletByUserId(Long userId) {
        Optional<Wallet> walletOpt = walletRepository.findByUserId(userId);
        if (walletOpt.isPresent()) {
            Wallet wallet = walletOpt.get();
            return new WalletDTO(wallet.getId(), wallet.getUserId(), wallet.getBalance(), 
                               wallet.getFrozenAmount(), wallet.getCurrency());
        }
        return null;
    }
    
    public WalletDTO createWallet(Long userId) {
        // 先检查是否已存在
        Optional<Wallet> existingWallet = walletRepository.findByUserId(userId);
        if (existingWallet.isPresent()) {
            Wallet wallet = existingWallet.get();
            return new WalletDTO(wallet.getId(), wallet.getUserId(), wallet.getBalance(), 
                               wallet.getFrozenAmount(), wallet.getCurrency());
        }
        
        try {
            // 尝试创建新钱包
            Wallet wallet = new Wallet(userId);
            wallet = walletRepository.save(wallet);
            return new WalletDTO(wallet.getId(), wallet.getUserId(), wallet.getBalance(), 
                               wallet.getFrozenAmount(), wallet.getCurrency());
        } catch (Exception e) {
            // 如果创建失败（可能是并发创建），再次尝试获取
            System.err.println("创建钱包失败，尝试获取现有钱包: " + e.getMessage());
            Optional<Wallet> walletOpt = walletRepository.findByUserId(userId);
            if (walletOpt.isPresent()) {
                Wallet wallet = walletOpt.get();
                return new WalletDTO(wallet.getId(), wallet.getUserId(), wallet.getBalance(), 
                                   wallet.getFrozenAmount(), wallet.getCurrency());
            }
            throw new RuntimeException("创建钱包失败: " + e.getMessage());
        }
    }
    
    public boolean recharge(Long userId, BigDecimal amount, String description) {
        Wallet wallet = getOrCreateWallet(userId);
        
        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
        
        // 记录交易
        Transaction transaction = new Transaction(userId, "RECHARGE", 
                                               amount, newBalance, description);
        transactionRepository.save(transaction);
        
        return true;
    }
    
    public boolean consume(Long userId, BigDecimal amount, String description, Long relatedId) {
        Wallet wallet = getOrCreateWallet(userId);
        
        if (!wallet.hasEnoughBalance(amount)) {
            return false;
        }
        
        BigDecimal newBalance = wallet.getBalance().subtract(amount);
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
        
        // 记录交易
        Transaction transaction = new Transaction(userId, "CONSUME", 
                                               amount.negate(), newBalance, description);
        transaction.setRelatedId(relatedId);
        transactionRepository.save(transaction);
        
        return true;
    }
    
    /**
     * 消费金币（Long类型金额）
     */
    public boolean consume(Long userId, Long amount, String description, Long relatedId) {
        BigDecimal bigDecimalAmount = new BigDecimal(amount);
        return consume(userId, bigDecimalAmount, description, relatedId);
    }
    
    public boolean earn(Long userId, BigDecimal amount, String description, Long relatedId) {
        Wallet wallet = getOrCreateWallet(userId);
        
        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
        
        // 记录交易
        Transaction transaction = new Transaction(userId, "EARN", 
                                               amount, newBalance, description);
        transaction.setRelatedId(relatedId);
        transactionRepository.save(transaction);
        
        return true;
    }
    
    /**
     * 添加余额（用于充值成功后的余额更新）
     */
    public boolean addBalance(Long userId, Long coins) {
        try {
            BigDecimal amount = new BigDecimal(coins);
            return recharge(userId, amount, "充值成功");
        } catch (Exception e) {
            throw new RuntimeException("添加余额失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查用户余额是否足够
     */
    public boolean hasEnoughBalance(Long userId, Long amount) {
        Wallet wallet = getOrCreateWallet(userId);
        BigDecimal requiredAmount = new BigDecimal(amount);
        return wallet.hasEnoughBalance(requiredAmount);
    }
    
    private Wallet getOrCreateWallet(Long userId) {
        Optional<Wallet> walletOpt = walletRepository.findByUserId(userId);
        if (walletOpt.isPresent()) {
            return walletOpt.get();
        }
        
        Wallet wallet = new Wallet(userId);
        return walletRepository.save(wallet);
    }
}
