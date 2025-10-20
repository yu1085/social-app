package com.socialmeet.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 靓号竞拍DTO
 */
public class LuckyNumberAuctionDTO {
    
    private Long id;
    private Long luckyNumberId;
    private String luckyNumber;
    private BigDecimal startPrice;
    private BigDecimal currentPrice;
    private BigDecimal reservePrice; // 保留价
    private Long currentBidderId;
    private String currentBidderName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // AUCTIONING, ENDED, CANCELLED
    private String statusDisplayName;
    private Integer bidCount;
    private List<BidHistoryDTO> bidHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public LuckyNumberAuctionDTO() {}
    
    public LuckyNumberAuctionDTO(Long id, Long luckyNumberId, String luckyNumber, 
                                 BigDecimal startPrice, BigDecimal currentPrice, 
                                 BigDecimal reservePrice, Long currentBidderId, 
                                 String currentBidderName, LocalDateTime startTime, 
                                 LocalDateTime endTime, String status, String statusDisplayName,
                                 Integer bidCount, List<BidHistoryDTO> bidHistory,
                                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.luckyNumberId = luckyNumberId;
        this.luckyNumber = luckyNumber;
        this.startPrice = startPrice;
        this.currentPrice = currentPrice;
        this.reservePrice = reservePrice;
        this.currentBidderId = currentBidderId;
        this.currentBidderName = currentBidderName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.statusDisplayName = statusDisplayName;
        this.bidCount = bidCount;
        this.bidHistory = bidHistory;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getLuckyNumberId() { return luckyNumberId; }
    public void setLuckyNumberId(Long luckyNumberId) { this.luckyNumberId = luckyNumberId; }
    
    public String getLuckyNumber() { return luckyNumber; }
    public void setLuckyNumber(String luckyNumber) { this.luckyNumber = luckyNumber; }
    
    public BigDecimal getStartPrice() { return startPrice; }
    public void setStartPrice(BigDecimal startPrice) { this.startPrice = startPrice; }
    
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    
    public BigDecimal getReservePrice() { return reservePrice; }
    public void setReservePrice(BigDecimal reservePrice) { this.reservePrice = reservePrice; }
    
    public Long getCurrentBidderId() { return currentBidderId; }
    public void setCurrentBidderId(Long currentBidderId) { this.currentBidderId = currentBidderId; }
    
    public String getCurrentBidderName() { return currentBidderName; }
    public void setCurrentBidderName(String currentBidderName) { this.currentBidderName = currentBidderName; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getStatusDisplayName() { return statusDisplayName; }
    public void setStatusDisplayName(String statusDisplayName) { this.statusDisplayName = statusDisplayName; }
    
    public Integer getBidCount() { return bidCount; }
    public void setBidCount(Integer bidCount) { this.bidCount = bidCount; }
    
    public List<BidHistoryDTO> getBidHistory() { return bidHistory; }
    public void setBidHistory(List<BidHistoryDTO> bidHistory) { this.bidHistory = bidHistory; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    /**
     * 出价历史DTO
     */
    public static class BidHistoryDTO {
        private Long id;
        private Long auctionId;
        private Long bidderId;
        private String bidderName;
        private BigDecimal bidAmount;
        private LocalDateTime bidTime;
        private String status; // ACTIVE, OUTBID, WON, LOST
        
        // 构造函数
        public BidHistoryDTO() {}
        
        public BidHistoryDTO(Long id, Long auctionId, Long bidderId, String bidderName,
                            BigDecimal bidAmount, LocalDateTime bidTime, String status) {
            this.id = id;
            this.auctionId = auctionId;
            this.bidderId = bidderId;
            this.bidderName = bidderName;
            this.bidAmount = bidAmount;
            this.bidTime = bidTime;
            this.status = status;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getAuctionId() { return auctionId; }
        public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }
        
        public Long getBidderId() { return bidderId; }
        public void setBidderId(Long bidderId) { this.bidderId = bidderId; }
        
        public String getBidderName() { return bidderName; }
        public void setBidderName(String bidderName) { this.bidderName = bidderName; }
        
        public BigDecimal getBidAmount() { return bidAmount; }
        public void setBidAmount(BigDecimal bidAmount) { this.bidAmount = bidAmount; }
        
        public LocalDateTime getBidTime() { return bidTime; }
        public void setBidTime(LocalDateTime bidTime) { this.bidTime = bidTime; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
