package com.example.socialmeet.repository;

import com.example.socialmeet.entity.WealthLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WealthLevelRepository extends JpaRepository<WealthLevel, Long> {
    
    /**
     * 根据用户ID查找财富等级
     */
    Optional<WealthLevel> findByUserId(Long userId);
    
    /**
     * 查找指定财富值范围内的用户
     */
    @Query("SELECT w FROM WealthLevel w WHERE w.wealthValue >= :minValue AND w.wealthValue <= :maxValue ORDER BY w.wealthValue DESC")
    List<WealthLevel> findByWealthValueRange(@Param("minValue") Integer minValue, @Param("maxValue") Integer maxValue);
    
    /**
     * 查找指定等级名称的用户
     */
    List<WealthLevel> findByLevelNameOrderByWealthValueDesc(String levelName);
    
    /**
     * 查找财富值大于等于指定值的用户
     */
    @Query("SELECT w FROM WealthLevel w WHERE w.wealthValue >= :minValue ORDER BY w.wealthValue DESC")
    List<WealthLevel> findByWealthValueGreaterThanEqual(@Param("minValue") Integer minValue);
    
    /**
     * 统计指定等级的用户数量
     */
    long countByLevelName(String levelName);
    
    /**
     * 统计财富值大于等于指定值的用户数量
     */
    long countByWealthValueGreaterThanEqual(Integer minValue);
    
    /**
     * 获取财富排行榜（前N名）
     */
    @Query("SELECT w FROM WealthLevel w ORDER BY w.wealthValue DESC")
    List<WealthLevel> findTopNByOrderByWealthValueDesc(@Param("limit") int limit);
    
    /**
     * 获取用户在当前等级中的排名
     */
    @Query("SELECT COUNT(w) + 1 FROM WealthLevel w WHERE w.wealthValue > :wealthValue")
    Long getUserRank(@Param("wealthValue") Integer wealthValue);
    
    /**
     * 获取指定等级的所有特权用户
     */
    @Query("SELECT w FROM WealthLevel w WHERE w.wealthValue >= :minValue ORDER BY w.wealthValue DESC")
    List<WealthLevel> findPrivilegedUsers(@Param("minValue") Integer minValue);
}