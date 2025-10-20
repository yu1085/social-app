package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.WealthLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 财富等级Repository
 */
@Repository
public interface WealthLevelRepository extends JpaRepository<WealthLevel, Long> {
    
    /**
     * 根据财富值查找对应的等级
     */
    @Query("SELECT wl FROM WealthLevel wl WHERE wl.minWealthValue <= :wealthValue AND (wl.maxWealthValue IS NULL OR wl.maxWealthValue >= :wealthValue) ORDER BY wl.minWealthValue DESC")
    Optional<WealthLevel> findByWealthValue(@Param("wealthValue") Integer wealthValue);
    
    /**
     * 查找下一个等级
     */
    @Query("SELECT wl FROM WealthLevel wl WHERE wl.minWealthValue > :wealthValue ORDER BY wl.minWealthValue ASC")
    Optional<WealthLevel> findNextLevel(@Param("wealthValue") Integer wealthValue);
    
    /**
     * 获取所有等级（按排序）
     */
    List<WealthLevel> findAllByOrderBySortOrderAsc();
    
    /**
     * 根据等级名称查找
     */
    Optional<WealthLevel> findByLevelName(String levelName);
    
    /**
     * 根据等级ID查找
     */
    Optional<WealthLevel> findByLevelId(Integer levelId);
}
