package com.example.socialmeet.repository;

import com.example.socialmeet.entity.WealthLevelRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WealthLevelRuleRepository extends JpaRepository<WealthLevelRule, Long> {
    
    /**
     * 根据财富值查找匹配的等级规则
     */
    @Query("SELECT r FROM WealthLevelRule r WHERE r.minWealthValue <= :wealthValue AND (r.maxWealthValue IS NULL OR r.maxWealthValue >= :wealthValue) ORDER BY r.minWealthValue DESC")
    Optional<WealthLevelRule> findMatchingRule(@Param("wealthValue") Integer wealthValue);
    
    /**
     * 查找下一等级规则
     */
    @Query("SELECT r FROM WealthLevelRule r WHERE r.minWealthValue > :wealthValue ORDER BY r.minWealthValue ASC")
    Optional<WealthLevelRule> findNextLevelRule(@Param("wealthValue") Integer wealthValue);
    
    /**
     * 获取所有等级规则，按最小财富值排序
     */
    @Query("SELECT r FROM WealthLevelRule r ORDER BY r.minWealthValue ASC")
    List<WealthLevelRule> findAllOrderByMinWealthValue();
    
    /**
     * 查找指定等级名称的规则
     */
    Optional<WealthLevelRule> findByLevelName(String levelName);
    
    /**
     * 查找最高等级规则
     */
    @Query("SELECT r FROM WealthLevelRule r WHERE r.maxWealthValue IS NULL ORDER BY r.minWealthValue DESC")
    Optional<WealthLevelRule> findHighestLevelRule();
}
