package com.example.socialmeet.service;

import com.example.socialmeet.dto.GiftDTO;
import com.example.socialmeet.dto.GiftRecordDTO;
import com.example.socialmeet.entity.Gift;
import com.example.socialmeet.entity.GiftRecord;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.GiftRepository;
import com.example.socialmeet.repository.GiftRecordRepository;
import com.example.socialmeet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GiftService {
    
    @Autowired
    private GiftRepository giftRepository;
    
    @Autowired
    private GiftRecordRepository giftRecordRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WalletService walletService;
    
    public List<GiftDTO> getAllGifts() {
        List<Gift> gifts = giftRepository.findByIsActiveTrueOrderByPriceAsc();
        return gifts.stream().map(GiftDTO::new).collect(Collectors.toList());
    }
    
    public List<GiftDTO> getGiftsByCategory(Gift.GiftCategory category) {
        List<Gift> gifts = giftRepository.findByCategoryAndIsActiveTrueOrderByPriceAsc(category);
        return gifts.stream().map(GiftDTO::new).collect(Collectors.toList());
    }
    
    public List<GiftDTO> getGiftsByPriceRange(Double minPrice, Double maxPrice) {
        List<Gift> gifts = giftRepository.findByIsActiveTrueAndPriceBetweenOrderByPriceAsc(minPrice, maxPrice);
        return gifts.stream().map(GiftDTO::new).collect(Collectors.toList());
    }
    
    public GiftDTO getGiftById(Long id) {
        Optional<Gift> giftOpt = giftRepository.findById(id);
        if (giftOpt.isPresent()) {
            return new GiftDTO(giftOpt.get());
        }
        return null;
    }
    
    public GiftRecordDTO sendGift(Long senderId, Long receiverId, Long giftId, Integer quantity, String message) {
        Optional<Gift> giftOpt = giftRepository.findById(giftId);
        if (!giftOpt.isPresent()) {
            throw new RuntimeException("礼物不存在");
        }
        
        Gift gift = giftOpt.get();
        if (!gift.getIsActive()) {
            throw new RuntimeException("礼物已下架");
        }
        
        BigDecimal totalAmount = gift.getPrice().multiply(BigDecimal.valueOf(quantity));
        
        // 检查余额
        if (!walletService.consume(senderId, totalAmount, 
                                 "发送礼物: " + gift.getName() + " x" + quantity, giftId)) {
            throw new RuntimeException("余额不足");
        }
        
        // 创建礼物记录
        GiftRecord giftRecord = new GiftRecord(senderId, receiverId, giftId, quantity, totalAmount, message);
        giftRecord = giftRecordRepository.save(giftRecord);
        
        // 接收者获得收益（扣除平台费用）
        BigDecimal platformFee = totalAmount.multiply(BigDecimal.valueOf(0.1)); // 10%平台费
        BigDecimal receiverAmount = totalAmount.subtract(platformFee);
        walletService.earn(receiverId, receiverAmount, 
                          "收到礼物: " + gift.getName() + " x" + quantity, giftRecord.getId());
        
        // 构建返回DTO
        GiftRecordDTO dto = new GiftRecordDTO(giftRecord);
        dto.setGiftName(gift.getName());
        dto.setGiftImageUrl(gift.getImageUrl());
        
        // 获取用户信息
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> receiverOpt = userRepository.findById(receiverId);
        
        if (senderOpt.isPresent()) {
            User sender = senderOpt.get();
            dto.setSenderNickname(sender.getNickname());
            dto.setSenderAvatar(sender.getAvatarUrl());
        }
        
        if (receiverOpt.isPresent()) {
            User receiver = receiverOpt.get();
            dto.setReceiverNickname(receiver.getNickname());
            dto.setReceiverAvatar(receiver.getAvatarUrl());
        }
        
        return dto;
    }
    
    public Page<GiftRecordDTO> getSentGifts(Long userId, Pageable pageable) {
        Page<GiftRecord> records = giftRecordRepository.findBySenderIdOrderByCreatedAtDesc(userId, pageable);
        return records.map(this::convertToGiftRecordDTO);
    }
    
    public Page<GiftRecordDTO> getReceivedGifts(Long userId, Pageable pageable) {
        Page<GiftRecord> records = giftRecordRepository.findByReceiverIdOrderByCreatedAtDesc(userId, pageable);
        return records.map(this::convertToGiftRecordDTO);
    }
    
    public Page<GiftRecordDTO> getUserGiftHistory(Long userId, Pageable pageable) {
        Page<GiftRecord> records = giftRecordRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return records.map(this::convertToGiftRecordDTO);
    }
    
    public BigDecimal getTotalSentAmount(Long userId) {
        Double amount = giftRecordRepository.sumTotalAmountBySenderId(userId);
        return amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalReceivedAmount(Long userId) {
        Double amount = giftRecordRepository.sumTotalAmountByReceiverId(userId);
        return amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO;
    }
    
    private GiftRecordDTO convertToGiftRecordDTO(GiftRecord record) {
        GiftRecordDTO dto = new GiftRecordDTO(record);
        
        // 获取礼物信息
        Optional<Gift> giftOpt = giftRepository.findById(record.getGiftId());
        if (giftOpt.isPresent()) {
            Gift gift = giftOpt.get();
            dto.setGiftName(gift.getName());
            dto.setGiftImageUrl(gift.getImageUrl());
        }
        
        // 获取用户信息
        Optional<User> senderOpt = userRepository.findById(record.getSenderId());
        Optional<User> receiverOpt = userRepository.findById(record.getReceiverId());
        
        if (senderOpt.isPresent()) {
            User sender = senderOpt.get();
            dto.setSenderNickname(sender.getNickname());
            dto.setSenderAvatar(sender.getAvatarUrl());
        }
        
        if (receiverOpt.isPresent()) {
            User receiver = receiverOpt.get();
            dto.setReceiverNickname(receiver.getNickname());
            dto.setReceiverAvatar(receiver.getAvatarUrl());
        }
        
        return dto;
    }
}
