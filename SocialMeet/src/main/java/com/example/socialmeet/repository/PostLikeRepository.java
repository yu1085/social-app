package com.example.socialmeet.repository;

import com.example.socialmeet.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 动态点赞记录Repository
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    /**
     * 检查用户是否已点赞某个动态
     */
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    
    /**
     * 根据动态ID和用户ID查找点赞记录
     */
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);
    
    /**
     * 删除用户对某个动态的点赞
     */
    void deleteByPostIdAndUserId(Long postId, Long userId);
    
    /**
     * 统计某个动态的点赞数量
     */
    long countByPostId(Long postId);
    
    /**
     * 获取某个动态的所有点赞记录
     */
    List<PostLike> findByPostIdOrderByCreatedAtDesc(Long postId);
    
    /**
     * 获取用户点赞的所有动态ID
     */
    @Query("SELECT pl.postId FROM PostLike pl WHERE pl.userId = :userId")
    List<Long> findPostIdsByUserId(@Param("userId") Long userId);
}
