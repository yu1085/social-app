package com.example.socialmeet.repository;

import com.example.socialmeet.entity.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 动态评论Repository
 */
@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    
    /**
     * 获取某个动态的评论列表（分页）
     */
    Page<PostComment> findByPostIdAndIsActiveTrueOrderByCreatedAtDesc(Long postId, Pageable pageable);
    
    /**
     * 获取某个动态的评论列表（不分页）
     */
    List<PostComment> findByPostIdAndIsActiveTrueOrderByCreatedAtDesc(Long postId);
    
    /**
     * 统计某个动态的评论数量
     */
    long countByPostIdAndIsActiveTrue(Long postId);
    
    /**
     * 获取某个动态的顶级评论（非回复）
     */
    List<PostComment> findByPostIdAndParentIdIsNullAndIsActiveTrueOrderByCreatedAtDesc(Long postId);
    
    /**
     * 获取某个评论的回复列表
     */
    List<PostComment> findByParentIdAndIsActiveTrueOrderByCreatedAtAsc(Long parentId);
    
    /**
     * 获取用户的所有评论
     */
    Page<PostComment> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
