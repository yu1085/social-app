package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 动态数据访问层
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 根据用户ID查询动态列表
     */
    Page<Post> findByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId,
            Post.PostStatus status,
            Pageable pageable);

    /**
     * 查询所有已发布的动态，按创建时间倒序
     */
    Page<Post> findByStatusOrderByCreatedAtDesc(
            Post.PostStatus status,
            Pageable pageable);

    /**
     * 根据位置查询附近的动态
     */
    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.location LIKE %:location% ORDER BY p.createdAt DESC")
    Page<Post> findByLocationContaining(
            @Param("status") Post.PostStatus status,
            @Param("location") String location,
            Pageable pageable);

    /**
     * 查询一分钟免费的动态
     */
    Page<Post> findByIsFreeMinuteAndStatusOrderByCreatedAtDesc(
            Boolean isFreeMinute,
            Post.PostStatus status,
            Pageable pageable);

    /**
     * 统计用户发布的动态数量
     */
    Long countByUserIdAndStatus(Long userId, Post.PostStatus status);

    /**
     * 获取热门动态（按点赞数排序）
     */
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.likeCount DESC, p.createdAt DESC")
    Page<Post> findHotPosts(
            @Param("status") Post.PostStatus status,
            Pageable pageable);
}
