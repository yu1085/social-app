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
        if (walletRepository.existsByUserId(userId)) {
            return getWalletByUserId(userId);
        }
        
        Wallet wallet = new Wallet(userId);
        wallet = walletRepository.save(wallet);
        return new WalletDTO(wallet.getId(), wallet.getUserId(), wallet.getBalance(), 
                           wallet.getFrozenAmount(), wallet.getCurrency());
    }
    
    public boolean recharge(Long userId, BigDecimal amount, String description) {
        Wallet wallet = getOrCreateWallet(userId);
        
        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
        
        // 记录交易
        Transaction transaction = new Transaction(userId, Transaction.TransactionType.RECHARGE, 
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
        Transaction transaction = new Transaction(userId, Transaction.TransactionType.CONSUME, 
                                               amount.negate(), newBalance, description);
        transaction.setRelatedId(relatedId);
        transactionRepository.save(transaction);
        
        return true;
    }
    
    public boolean earn(Long userId, BigDecimal amount, String description, Long relatedId) {
        Wallet wallet = getOrCreateWallet(userId);
        
        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
        
        // 记录交易
        Transaction transaction = new Transaction(userId, Transaction.TransactionType.EARN, 
                                               amount, newBalance, description);
        transaction.setRelatedId(relatedId);
        transactionRepository.save(transaction);
        
        return true;
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
