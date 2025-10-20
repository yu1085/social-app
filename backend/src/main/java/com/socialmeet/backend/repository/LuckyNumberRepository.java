package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.LuckyNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 靓号Repository
 */
@Repository
public interface LuckyNumberRepository extends JpaRepository<LuckyNumber, Long> {
    
    /**
     * 根据靓号数字查找
     */
    Optional<LuckyNumber> findByNumber(String number);
    
    /**
     * 根据状态查找靓号
     */
    Page<LuckyNumber> findByStatus(LuckyNumber.LuckyNumberStatus status, Pageable pageable);
    
    /**
     * 根据等级查找靓号
     */
    Page<LuckyNumber> findByTier(LuckyNumber.LuckyNumberTier tier, Pageable pageable);
    
    /**
     * 根据拥有者ID查找靓号
     */
    List<LuckyNumber> findByOwnerIdOrderByPurchaseTimeDesc(Long ownerId);
    
    /**
     * 查找可购买的靓号
     */
    @Query("SELECT l FROM LuckyNumber l WHERE l.status = 'AVAILABLE' ORDER BY l.tier, l.price ASC")
    Page<LuckyNumber> findAvailableLuckyNumbers(Pageable pageable);
    
    /**
     * 根据价格范围查找靓号
     */
    @Query("SELECT l FROM LuckyNumber l WHERE l.status = 'AVAILABLE' AND l.price BETWEEN :minPrice AND :maxPrice ORDER BY l.price ASC")
    Page<LuckyNumber> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice, 
                                      @Param("maxPrice") java.math.BigDecimal maxPrice, 
                                      Pageable pageable);
    
    /**
     * 查找特殊靓号
     */
    @Query("SELECT l FROM LuckyNumber l WHERE l.status = 'AVAILABLE' AND l.isSpecial = true ORDER BY l.price ASC")
    Page<LuckyNumber> findSpecialLuckyNumbers(Pageable pageable);
    
    /**
     * 统计用户拥有的靓号数量
     */
    long countByOwnerId(Long ownerId);
    
    /**
     * 查找即将过期的靓号
     */
    @Query("SELECT l FROM LuckyNumber l WHERE l.ownerId = :ownerId AND l.expireTime IS NOT NULL AND l.expireTime <= :expireTime")
    List<LuckyNumber> findExpiringLuckyNumbers(@Param("ownerId") Long ownerId, 
                                              @Param("expireTime") java.time.LocalDateTime expireTime);
}
