package com.example.socialmeet.repository;

import com.example.socialmeet.entity.VirtualCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 虚拟货币Repository
 */
@Repository
public interface VirtualCurrencyRepository extends JpaRepository<VirtualCurrency, Long> {
    
    /**
     * 根据用户ID和货币类型查找
     */
    Optional<VirtualCurrency> findByUserIdAndCurrencyType(Long userId, String currencyType);
    
    /**
     * 根据用户ID查找所有货币
     */
    List<VirtualCurrency> findByUserId(Long userId);
    
    /**
     * 根据货币类型查找所有用户
     */
    List<VirtualCurrency> findByCurrencyType(String currencyType);
    
    /**
     * 查找余额大于指定金额的用户
     */
    @Query("SELECT vc FROM VirtualCurrency vc WHERE vc.currencyType = :currencyType AND vc.balance > :amount ORDER BY vc.balance DESC")
    List<VirtualCurrency> findUsersWithBalanceGreaterThan(@Param("currencyType") String currencyType, @Param("amount") java.math.BigDecimal amount);
    
    /**
     * 统计用户总数
     */
    @Query("SELECT COUNT(DISTINCT vc.userId) FROM VirtualCurrency vc WHERE vc.currencyType = :currencyType")
    Long countUsersByCurrencyType(@Param("currencyType") String currencyType);
    
    /**
     * 统计总余额
     */
    @Query("SELECT SUM(vc.balance) FROM VirtualCurrency vc WHERE vc.currencyType = :currencyType")
    java.math.BigDecimal getTotalBalanceByCurrencyType(@Param("currencyType") String currencyType);
}
