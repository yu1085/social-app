package com.example.myapplication.dto;

/**
 * 靓号统计DTO
 */
public class LuckyNumberStatsDTO {
    
    private long totalCount;
    private int expiringCount;
    private boolean hasExpiring;
    private int availableCount;
    private int soldCount;
    private double totalValue;
    
    // 构造函数
    public LuckyNumberStatsDTO() {}
    
    public LuckyNumberStatsDTO(long totalCount, int expiringCount, boolean hasExpiring,
                              int availableCount, int soldCount, double totalValue) {
        this.totalCount = totalCount;
        this.expiringCount = expiringCount;
        this.hasExpiring = hasExpiring;
        this.availableCount = availableCount;
        this.soldCount = soldCount;
        this.totalValue = totalValue;
    }
    
    // Getters and Setters
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    
    public int getExpiringCount() { return expiringCount; }
    public void setExpiringCount(int expiringCount) { this.expiringCount = expiringCount; }
    
    public boolean isHasExpiring() { return hasExpiring; }
    public void setHasExpiring(boolean hasExpiring) { this.hasExpiring = hasExpiring; }
    
    public int getAvailableCount() { return availableCount; }
    public void setAvailableCount(int availableCount) { this.availableCount = availableCount; }
    
    public int getSoldCount() { return soldCount; }
    public void setSoldCount(int soldCount) { this.soldCount = soldCount; }
    
    public double getTotalValue() { return totalValue; }
    public void setTotalValue(double totalValue) { this.totalValue = totalValue; }
}
