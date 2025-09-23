package com.example.socialmeet.repository;

import com.example.socialmeet.entity.RechargePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 充值套餐Repository
 */
@Repository
public interface RechargePackageRepository extends JpaRepository<RechargePackage, String> {
    
    /**
     * 查询所有可用的充值套餐，按排序顺序
     */
    List<RechargePackage> findByIsActiveTrueOrderBySortOrder();
    
    /**
     * 查询推荐的充值套餐
     */
    List<RechargePackage> findByIsRecommendedTrueAndIsActiveTrueOrderBySortOrder();
    
    /**
     * 查询热门的充值套餐
     */
    List<RechargePackage> findByIsPopularTrueAndIsActiveTrueOrderBySortOrder();
}
