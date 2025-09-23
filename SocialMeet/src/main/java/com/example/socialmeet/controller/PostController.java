package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.PostDTO;
import com.example.socialmeet.dto.EnhancedPostDTO;
import com.example.socialmeet.entity.Post;
import com.example.socialmeet.repository.PostRepository;
import com.example.socialmeet.service.EnhancedPostService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(originPatterns = "*")
public class PostController {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EnhancedPostService enhancedPostService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostDTO>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> posts = postRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
            
            List<PostDTO> postDTOs = posts.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(postDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取动态列表失败: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<PostDTO>> createPost(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PostDTO postDTO) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Post post = new Post();
            post.setUserId(userId);
            post.setContent(postDTO.getContent());
            post.setImageUrl(postDTO.getImageUrl());
            post.setVideoUrl(postDTO.getVideoUrl());
            post.setLocation(postDTO.getLocation());
            post.setStatus("PUBLISHED");
            
            post = postRepository.save(post);
            
            return ResponseEntity.ok(ApiResponse.success(convertToDTO(post)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("发布动态失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> getPostById(@PathVariable Long id) {
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("动态不存在"));
            
            return ResponseEntity.ok(ApiResponse.success(convertToDTO(post)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取动态失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> updatePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody PostDTO postDTO) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("动态不存在"));
            
            if (!post.getUserId().equals(userId)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("无权限修改此动态"));
            }
            
            post.setContent(postDTO.getContent());
            post.setImageUrl(postDTO.getImageUrl());
            post.setVideoUrl(postDTO.getVideoUrl());
            post.setLocation(postDTO.getLocation());
            
            post = postRepository.save(post);
            
            return ResponseEntity.ok(ApiResponse.success(convertToDTO(post)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新动态失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("动态不存在"));
            
            if (!post.getUserId().equals(userId)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("无权限删除此动态"));
            }
            
            post.setStatus("DELETED");
            post.setIsActive(false);
            postRepository.save(post);
            
            return ResponseEntity.ok(ApiResponse.success("删除动态成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除动态失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<PostDTO>> likePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("动态不存在"));
            
            // TODO: 实现点赞逻辑
            post.setLikeCount(post.getLikeCount() + 1);
            post = postRepository.save(post);
            
            return ResponseEntity.ok(ApiResponse.success(convertToDTO(post)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("点赞失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/unlike")
    public ResponseEntity<ApiResponse<PostDTO>> unlikePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("动态不存在"));
            
            // TODO: 实现取消点赞逻辑
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            post = postRepository.save(post);
            
            return ResponseEntity.ok(ApiResponse.success(convertToDTO(post)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("取消点赞失败: " + e.getMessage()));
        }
    }
    
    // ==================== 增强接口 ====================
    
    /**
     * 获取增强的动态列表
     */
    @GetMapping("/enhanced")
    public ResponseEntity<ApiResponse<Page<EnhancedPostDTO>>> getEnhancedPosts(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "nearby") String filter,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Page<EnhancedPostDTO> posts = enhancedPostService.getEnhancedPosts(userId, filter, sortBy, page, size);
            return ResponseEntity.ok(ApiResponse.success(posts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取动态列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 点赞/取消点赞动态
     */
    @PostMapping("/{id}/toggle-like")
    public ResponseEntity<ApiResponse<EnhancedPostDTO>> toggleLikePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            EnhancedPostDTO post = enhancedPostService.likePost(id, userId);
            return ResponseEntity.ok(ApiResponse.success(post));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("操作失败: " + e.getMessage()));
        }
    }
    
    /**
     * 添加评论
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<EnhancedPostDTO>> addComment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestParam String content,
            @RequestParam(required = false) Long parentId) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            EnhancedPostDTO post = enhancedPostService.addComment(id, userId, content, parentId);
            return ResponseEntity.ok(ApiResponse.success(post));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("添加评论失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取动态评论
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<List<EnhancedPostDTO.CommentDTO>>> getPostComments(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            List<EnhancedPostDTO.CommentDTO> comments = enhancedPostService.getPostComments(id, userId, page, size);
            return ResponseEntity.ok(ApiResponse.success(comments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取评论失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long commentId) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            enhancedPostService.deleteComment(commentId, userId);
            return ResponseEntity.ok(ApiResponse.success("删除评论成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除评论失败: " + e.getMessage()));
        }
    }
    
    private PostDTO convertToDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUserId());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setVideoUrl(post.getVideoUrl());
        dto.setLocation(post.getLocation());
        dto.setLikeCount(post.getLikeCount());
        dto.setCommentCount(post.getCommentCount());
        dto.setIsLiked(false); // TODO: 实现点赞状态检查
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        return dto;
    }
}