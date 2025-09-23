package com.example.socialmeet.repository;

import com.example.socialmeet.entity.GiftEffect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 礼物特效Repository
 */
@Repository
public interface GiftEffectRepository extends JpaRepository<GiftEffect, Long> {
    
    /**
     * 根据礼物ID查找特效
     */
    List<GiftEffect> findByGiftIdAndIsActiveTrueOrderByPriorityDesc(Long giftId);
    
    /**
     * 根据特效类型查找
     */
    List<GiftEffect> findByEffectTypeAndIsActiveTrueOrderByPriorityDesc(String effectType);
    
    /**
     * 根据礼物ID和特效类型查找
     */
    List<GiftEffect> findByGiftIdAndEffectTypeAndIsActiveTrueOrderByPriorityDesc(Long giftId, String effectType);
    
    /**
     * 查找满足触发条件的特效
     */
    @Query("SELECT ge FROM GiftEffect ge WHERE ge.giftId = :giftId AND ge.isActive = true AND " +
           "(:quantity IS NULL OR ge.triggerCondition IS NULL OR " +
           "CASE WHEN ge.triggerCondition LIKE '%>=%' THEN " +
           "CAST(SUBSTRING(ge.triggerCondition, LOCATE('>=', ge.triggerCondition) + 2) AS INTEGER) <= :quantity " +
           "ELSE TRUE END) " +
           "ORDER BY ge.priority DESC")
    List<GiftEffect> findActiveEffectsByGiftIdAndQuantity(@Param("giftId") Long giftId, @Param("quantity") Integer quantity);
}
