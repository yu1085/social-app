package com.example.socialmeet.repository;

import com.example.socialmeet.entity.DynamicLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 动态点赞数据访问层
 */
@Repository
public interface DynamicLikeRepository extends JpaRepository<DynamicLike, Long> {
    
    /**
     * 根据动态ID和用户ID查找点赞记录
     */
    Optional<DynamicLike> findByDynamicIdAndUserId(Long dynamicId, Long userId);
    
    /**
     * 检查用户是否已点赞动态
     */
    boolean existsByDynamicIdAndUserId(Long dynamicId, Long userId);
    
    /**
     * 根据动态ID统计点赞数
     */
    long countByDynamicId(Long dynamicId);
    
    /**
     * 根据用户ID查找所有点赞记录
     */
    List<DynamicLike> findByUserId(Long userId);
    
    /**
     * 根据动态ID删除所有点赞记录
     */
    void deleteByDynamicId(Long dynamicId);
    
    /**
     * 根据用户ID和动态ID删除点赞记录
     */
    void deleteByDynamicIdAndUserId(Long dynamicId, Long userId);
    
    /**
     * 查找用户点赞的动态ID列表
     */
    @Query("SELECT dl.dynamicId FROM DynamicLike dl WHERE dl.userId = :userId")
    List<Long> findDynamicIdsByUserId(@Param("userId") Long userId);
}
