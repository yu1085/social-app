package com.example.socialmeet.repository;

import com.example.socialmeet.entity.LuckyNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LuckyNumberRepository extends JpaRepository<LuckyNumber, Long> {
    
    /**
     * 根据等级查找靓号
     */
    List<LuckyNumber> findByTierOrderByPriceAsc(LuckyNumber.LuckyNumberTier tier);
    
    /**
     * 查找可用的靓号
     */
    List<LuckyNumber> findByIsAvailableTrueOrderByTierAscPriceAsc();
    
    /**
     * 根据等级和可用性查找靓号
     */
    List<LuckyNumber> findByTierAndIsAvailableTrueOrderByPriceAsc(LuckyNumber.LuckyNumberTier tier);
    
    /**
     * 查找限量靓号
     */
    List<LuckyNumber> findByIsLimitedTrueOrderByTierAscPriceAsc();
    
    /**
     * 根据号码查找靓号
     */
    Optional<LuckyNumber> findByNumber(String number);
    
    /**
     * 按等级排序查找所有靓号
     */
    @Query("SELECT ln FROM LuckyNumber ln ORDER BY " +
           "CASE ln.tier " +
           "WHEN 'LIMITED' THEN 1 " +
           "WHEN 'TOP' THEN 2 " +
           "WHEN 'SUPER' THEN 3 " +
           "WHEN 'NORMAL' THEN 4 " +
           "END ASC, ln.price ASC")
    List<LuckyNumber> findAllOrderByTierAndPrice();
    
    /**
     * 按价格升序查找靓号
     */
    @Query("SELECT ln FROM LuckyNumber ln WHERE ln.isAvailable = true ORDER BY ln.price ASC")
    List<LuckyNumber> findAllAvailableOrderByPriceAsc();
    
    /**
     * 按价格降序查找靓号
     */
    @Query("SELECT ln FROM LuckyNumber ln WHERE ln.isAvailable = true ORDER BY ln.price DESC")
    List<LuckyNumber> findAllAvailableOrderByPriceDesc();
    
    /**
     * 按号码升序查找靓号
     */
    @Query("SELECT ln FROM LuckyNumber ln WHERE ln.isAvailable = true ORDER BY ln.number ASC")
    List<LuckyNumber> findAllAvailableOrderByNumberAsc();
    
    /**
     * 按号码降序查找靓号
     */
    @Query("SELECT ln FROM LuckyNumber ln WHERE ln.isAvailable = true ORDER BY ln.number DESC")
    List<LuckyNumber> findAllAvailableOrderByNumberDesc();
    
    /**
     * 根据价格范围查找靓号
     */
    @Query("SELECT ln FROM LuckyNumber ln WHERE ln.isAvailable = true AND ln.price BETWEEN :minPrice AND :maxPrice ORDER BY " +
           "CASE ln.tier " +
           "WHEN 'LIMITED' THEN 1 " +
           "WHEN 'TOP' THEN 2 " +
           "WHEN 'SUPER' THEN 3 " +
           "WHEN 'NORMAL' THEN 4 " +
           "END ASC, ln.price ASC")
    List<LuckyNumber> findByPriceRange(@Param("minPrice") Long minPrice, @Param("maxPrice") Long maxPrice);
    
    /**
     * 统计各等级靓号数量
     */
    @Query("SELECT ln.tier, COUNT(ln) FROM LuckyNumber ln WHERE ln.isAvailable = true GROUP BY ln.tier ORDER BY " +
           "CASE ln.tier " +
           "WHEN 'LIMITED' THEN 1 " +
           "WHEN 'TOP' THEN 2 " +
           "WHEN 'SUPER' THEN 3 " +
           "WHEN 'NORMAL' THEN 4 " +
           "END ASC")
    List<Object[]> countByTier();
}
