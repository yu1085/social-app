package com.socialmeet.backend.config;

import com.socialmeet.backend.entity.Wallet;
import com.socialmeet.backend.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包初始化器
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WalletInitializer implements CommandLineRunner {
    
    private final WalletRepository walletRepository;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化钱包数据...");
        
        // 给测试用户充值
        Long testUserId = 22491729L;
        Wallet wallet = walletRepository.findByUserId(testUserId).orElse(null);
        
        if (wallet != null) {
            // 如果钱包存在，给用户充值
            wallet.setBalance(new BigDecimal("100000"));
            wallet.setTotalRecharge(new BigDecimal("100000"));
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);
            log.info("用户 {} 钱包充值成功，余额: {}", testUserId, wallet.getBalance());
        } else {
            // 如果钱包不存在，创建新钱包并充值
            Wallet newWallet = new Wallet();
            newWallet.setUserId(testUserId);
            newWallet.setBalance(new BigDecimal("100000"));
            newWallet.setTotalRecharge(new BigDecimal("100000"));
            newWallet.setTotalConsume(BigDecimal.ZERO);
            newWallet.setTransactionCount(0);
            newWallet.setCreatedAt(LocalDateTime.now());
            newWallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(newWallet);
            log.info("用户 {} 新钱包创建并充值成功，余额: {}", testUserId, newWallet.getBalance());
        }
    }
}
