package com.example.socialmeet.service;

import com.example.socialmeet.dto.EnhancedPostDTO;
import com.example.socialmeet.entity.Post;
import com.example.socialmeet.entity.PostComment;
import com.example.socialmeet.entity.PostLike;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.PostCommentRepository;
import com.example.socialmeet.repository.PostLikeRepository;
import com.example.socialmeet.repository.PostRepository;
import com.example.socialmeet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增强的动态服务
 */
@Service
public class EnhancedPostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private PostLikeRepository postLikeRepository;
    
    @Autowired
    private PostCommentRepository postCommentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 获取动态列表（增强版）
     */
    public Page<EnhancedPostDTO> getEnhancedPosts(Long currentUserId, String filter, String sortBy, 
                                                 int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Page<Post> posts;
        
        // 根据筛选条件获取动态
        switch (filter.toLowerCase()) {
            case "nearby":
                posts = postRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
                break;
            case "latest":
                posts = postRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
                break;
            case "friends":
                // TODO: 实现好友动态逻辑
                posts = postRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
                break;
            case "like":
                // TODO: 实现喜欢的动态逻辑
                posts = postRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
                break;
            default:
                posts = postRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
        }
        
        return posts.map(post -> convertToEnhancedDTO(post, currentUserId));
    }
    
    /**
     * 点赞动态
     */
    @Transactional
    public EnhancedPostDTO likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("动态不存在"));
        
        // 检查是否已点赞
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            // 已点赞，取消点赞
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        } else {
            // 未点赞，添加点赞
            PostLike like = new PostLike(postId, userId);
            postLikeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
        }
        
        post = postRepository.save(post);
        return convertToEnhancedDTO(post, userId);
    }
    
    /**
     * 添加评论
     */
    @Transactional
    public EnhancedPostDTO addComment(Long postId, Long userId, String content, Long parentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("动态不存在"));
        
        PostComment comment = new PostComment(postId, userId, content);
        if (parentId != null) {
            comment.setParentId(parentId);
        }
        
        postCommentRepository.save(comment);
        
        // 更新评论数量
        long commentCount = postCommentRepository.countByPostIdAndIsActiveTrue(postId);
        post.setCommentCount((int) commentCount);
        post = postRepository.save(post);
        
        return convertToEnhancedDTO(post, userId);
    }
    
    /**
     * 获取动态评论
     */
    public List<EnhancedPostDTO.CommentDTO> getPostComments(Long postId, Long currentUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostComment> comments = postCommentRepository.findByPostIdAndIsActiveTrueOrderByCreatedAtDesc(postId, pageable);
        
        return comments.getContent().stream()
                .map(comment -> convertToCommentDTO(comment, currentUserId))
                .collect(Collectors.toList());
    }
    
    /**
     * 删除评论
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此评论");
        }
        
        comment.setIsActive(false);
        postCommentRepository.save(comment);
        
        // 更新动态评论数量
        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new RuntimeException("动态不存在"));
        long commentCount = postCommentRepository.countByPostIdAndIsActiveTrue(comment.getPostId());
        post.setCommentCount((int) commentCount);
        postRepository.save(post);
    }
    
    /**
     * 转换为增强DTO
     */
    private EnhancedPostDTO convertToEnhancedDTO(Post post, Long currentUserId) {
        EnhancedPostDTO dto = new EnhancedPostDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUserId());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setVideoUrl(post.getVideoUrl());
        dto.setLocation(post.getLocation());
        dto.setLikeCount(post.getLikeCount());
        dto.setCommentCount(post.getCommentCount());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        
        // 设置点赞状态
        dto.setIsLiked(postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId));
        
        // 设置用户信息
        User user = userRepository.findById(post.getUserId()).orElse(null);
        if (user != null) {
            dto.setUserName(user.getUsername());
            dto.setUserAvatar(user.getAvatarUrl());
            dto.setUserAge(user.getAge());
            dto.setDistance(0.0); // TODO: 计算实际距离
            dto.setUserStatus("在线"); // TODO: 获取实际状态
            dto.setIsOnline(true); // TODO: 获取实际在线状态
        }
        
        // 设置发布时间文本
        dto.setPublishTimeText(formatPublishTime(post.getCreatedAt()));
        
        // 设置免费1分钟标识（模拟）
        dto.setIsFreeMinute(Math.random() > 0.7);
        
        return dto;
    }
    
    /**
     * 转换为评论DTO
     */
    private EnhancedPostDTO.CommentDTO convertToCommentDTO(PostComment comment, Long currentUserId) {
        EnhancedPostDTO.CommentDTO dto = new EnhancedPostDTO.CommentDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setParentId(comment.getParentId());
        dto.setLikeCount(comment.getLikeCount());
        dto.setCreatedAt(comment.getCreatedAt());
        
        // 设置用户信息
        User user = userRepository.findById(comment.getUserId()).orElse(null);
        if (user != null) {
            dto.setUserName(user.getUsername());
            dto.setUserAvatar(user.getAvatarUrl());
        }
        
        // 设置点赞状态（TODO: 实现评论点赞功能）
        dto.setIsLiked(false);
        
        return dto;
    }
    
    /**
     * 格式化发布时间
     */
    private String formatPublishTime(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createdAt, now).toMinutes();
        
        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 1440) {
            return (minutes / 60) + "小时前";
        } else {
            return (minutes / 1440) + "天前";
        }
    }
}
