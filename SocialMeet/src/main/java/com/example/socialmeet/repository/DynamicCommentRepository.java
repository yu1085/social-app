package com.example.socialmeet.repository;

import com.example.socialmeet.entity.DynamicComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 动态评论数据访问层
 */
@Repository
public interface DynamicCommentRepository extends JpaRepository<DynamicComment, Long> {
    
    /**
     * 根据动态ID查找评论，按创建时间正序
     */
    Page<DynamicComment> findByDynamicIdAndIsDeletedFalseOrderByCreatedAtAsc(Long dynamicId, Pageable pageable);
    
    /**
     * 根据动态ID统计评论数
     */
    long countByDynamicIdAndIsDeletedFalse(Long dynamicId);
    
    /**
     * 根据用户ID查找评论
     */
    Page<DynamicComment> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据父评论ID查找回复
     */
    List<DynamicComment> findByParentIdAndIsDeletedFalseOrderByCreatedAtAsc(Long parentId);
    
    /**
     * 根据动态ID删除所有评论
     */
    void deleteByDynamicId(Long dynamicId);
    
    /**
     * 统计用户评论数量
     */
    long countByUserIdAndIsDeletedFalse(Long userId);
}
