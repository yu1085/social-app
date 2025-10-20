package com.socialmeet.backend.service;

import com.socialmeet.backend.dto.LuckyNumberDTO;
import com.socialmeet.backend.dto.PurchaseRequest;
import com.socialmeet.backend.entity.LuckyNumber;
import com.socialmeet.backend.entity.Transaction;
import com.socialmeet.backend.entity.Wallet;
import com.socialmeet.backend.repository.LuckyNumberRepository;
import com.socialmeet.backend.repository.TransactionRepository;
import com.socialmeet.backend.repository.WalletRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 道具商城服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PropMallService {
    
    private final LuckyNumberRepository luckyNumberRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WealthService wealthService;
    
    /**
     * 获取可购买的靓号列表
     */
    public Page<LuckyNumberDTO> getAvailableLuckyNumbers(int page, int size) {
        log.info("获取可购买的靓号列表 - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LuckyNumber> luckyNumbers = luckyNumberRepository.findAvailableLuckyNumbers(pageable);
        
        return luckyNumbers.map(this::convertToDTO);
    }
    
    /**
     * 根据等级获取靓号列表
     */
    public Page<LuckyNumberDTO> getLuckyNumbersByTier(String tier, int page, int size) {
        log.info("根据等级获取靓号列表 - tier: {}, page: {}, size: {}", tier, page, size);
        
        LuckyNumber.LuckyNumberTier tierEnum = LuckyNumber.LuckyNumberTier.valueOf(tier.toUpperCase());
        Pageable pageable = PageRequest.of(page, size);
        Page<LuckyNumber> luckyNumbers = luckyNumberRepository.findByTier(tierEnum, pageable);
        
        return luckyNumbers.map(this::convertToDTO);
    }
    
    /**
     * 根据价格范围获取靓号列表
     */
    public Page<LuckyNumberDTO> getLuckyNumbersByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        log.info("根据价格范围获取靓号列表 - minPrice: {}, maxPrice: {}, page: {}, size: {}", minPrice, maxPrice, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LuckyNumber> luckyNumbers = luckyNumberRepository.findByPriceRange(minPrice, maxPrice, pageable);
        
        return luckyNumbers.map(this::convertToDTO);
    }
    
    /**
     * 获取特殊靓号列表
     */
    public Page<LuckyNumberDTO> getSpecialLuckyNumbers(int page, int size) {
        log.info("获取特殊靓号列表 - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LuckyNumber> luckyNumbers = luckyNumberRepository.findSpecialLuckyNumbers(pageable);
        
        return luckyNumbers.map(this::convertToDTO);
    }
    
    /**
     * 购买靓号
     */
    @Transactional
    public LuckyNumberDTO purchaseLuckyNumber(Long userId, PurchaseRequest request) {
        log.info("用户购买靓号 - userId: {}, luckyNumberId: {}", userId, request.getLuckyNumberId());
        
        // 查找靓号
        LuckyNumber luckyNumber = luckyNumberRepository.findById(request.getLuckyNumberId())
                .orElseThrow(() -> new RuntimeException("靓号不存在"));
        
        // 检查靓号状态
        if (luckyNumber.getStatus() != LuckyNumber.LuckyNumberStatus.AVAILABLE) {
            throw new RuntimeException("靓号不可购买");
        }
        
        // 检查用户钱包余额，如果不存在则创建
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("用户 {} 钱包不存在，创建新钱包", userId);
                    Wallet newWallet = new Wallet();
                    newWallet.setUserId(userId);
                    newWallet.setBalance(BigDecimal.ZERO);
                    newWallet.setTotalRecharge(BigDecimal.ZERO);
                    newWallet.setTotalConsume(BigDecimal.ZERO);
                    newWallet.setTransactionCount(0);
                    newWallet.setCreatedAt(LocalDateTime.now());
                    newWallet.setUpdatedAt(LocalDateTime.now());
                    return walletRepository.save(newWallet);
                });
        
        BigDecimal totalPrice = luckyNumber.getPrice();
        if (wallet.getBalance().compareTo(totalPrice) < 0) {
            throw new RuntimeException("余额不足");
        }
        
        // 扣除余额
        wallet.setBalance(wallet.getBalance().subtract(totalPrice));
        wallet.setTotalConsume(wallet.getTotalConsume().add(totalPrice));
        wallet.setTransactionCount(wallet.getTransactionCount() + 1);
        walletRepository.save(wallet);
        
        // 更新靓号状态
        luckyNumber.setStatus(LuckyNumber.LuckyNumberStatus.SOLD);
        luckyNumber.setOwnerId(userId);
        luckyNumber.setPurchaseTime(LocalDateTime.now());
        
        // 设置有效期
        Integer validityDays = request.getValidityDays() != null ? request.getValidityDays() : 30;
        luckyNumber.setValidityDays(validityDays);
        luckyNumber.setExpireTime(LocalDateTime.now().plusDays(validityDays));
        
        luckyNumberRepository.save(luckyNumber);
        
        // 记录交易
        wealthService.recordTransaction(
            userId,
            Transaction.TransactionType.PURCHASE,
            Transaction.CoinSource.PURCHASED,
            totalPrice,
            totalPrice.intValue(),
            "购买靓号: " + luckyNumber.getNumber(),
            "LUCKY_NUMBER_" + luckyNumber.getId()
        );
        
        log.info("靓号购买成功 - userId: {}, luckyNumber: {}, price: {}", userId, luckyNumber.getNumber(), totalPrice);
        
        return convertToDTO(luckyNumber);
    }
    
    /**
     * 获取用户拥有的靓号
     */
    public List<LuckyNumberDTO> getUserLuckyNumbers(Long userId) {
        log.info("获取用户拥有的靓号 - userId: {}", userId);
        
        List<LuckyNumber> luckyNumbers = luckyNumberRepository.findByOwnerIdOrderByPurchaseTimeDesc(userId);
        
        return luckyNumbers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取靓号详情
     */
    public LuckyNumberDTO getLuckyNumberDetail(Long id) {
        log.info("获取靓号详情 - id: {}", id);
        
        LuckyNumber luckyNumber = luckyNumberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("靓号不存在"));
        
        return convertToDTO(luckyNumber);
    }
    
    /**
     * 检查靓号是否可用
     */
    public boolean isLuckyNumberAvailable(String number) {
        log.info("检查靓号是否可用 - number: {}", number);
        
        Optional<LuckyNumber> luckyNumber = luckyNumberRepository.findByNumber(number);
        return luckyNumber.isEmpty() || luckyNumber.get().getStatus() == LuckyNumber.LuckyNumberStatus.AVAILABLE;
    }
    
    /**
     * 获取用户靓号统计
     */
    public Object getUserLuckyNumberStats(Long userId) {
        log.info("获取用户靓号统计 - userId: {}", userId);
        
        long totalCount = luckyNumberRepository.countByOwnerId(userId);
        List<LuckyNumber> expiringNumbers = luckyNumberRepository.findExpiringLuckyNumbers(userId, LocalDateTime.now().plusDays(7));
        
        return new Object() {
            public final long totalCount = PropMallService.this.luckyNumberRepository.countByOwnerId(userId);
            public final int expiringCount = expiringNumbers.size();
            public final boolean hasExpiring = expiringCount > 0;
        };
    }
    
    /**
     * 转换为DTO
     */
    private LuckyNumberDTO convertToDTO(LuckyNumber luckyNumber) {
        return new LuckyNumberDTO(
            luckyNumber.getId(),
            luckyNumber.getNumber(),
            luckyNumber.getPrice(),
            luckyNumber.getTier().name(),
            luckyNumber.getTier().getDisplayName(),
            luckyNumber.getStatus().name(),
            luckyNumber.getStatus().getDisplayName(),
            luckyNumber.getOwnerId(),
            luckyNumber.getPurchaseTime(),
            luckyNumber.getValidityDays(),
            luckyNumber.getExpireTime(),
            luckyNumber.getDescription(),
            luckyNumber.getIsSpecial(),
            luckyNumber.getCreatedAt(),
            luckyNumber.getUpdatedAt()
        );
    }
}
