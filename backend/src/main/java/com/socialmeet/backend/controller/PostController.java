package com.socialmeet.backend.controller;

import com.socialmeet.backend.dto.ApiResponse;
import com.socialmeet.backend.dto.PostDTO;
import com.socialmeet.backend.dto.UserDTO;
import com.socialmeet.backend.entity.Post;
import com.socialmeet.backend.entity.User;
import com.socialmeet.backend.repository.PostRepository;
import com.socialmeet.backend.repository.UserRepository;
import com.socialmeet.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 动态控制器
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * 创建动态
     */
    @PostMapping
    @Transactional
    public ApiResponse<Map<String, Object>> createPost(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> postData) {
        try {
            log.info("═══════════════════════════════════════");
            log.info("收到创建动态请求");
            log.info("Authorization头: {}", authHeader);
            log.info("动态数据: {}", postData);
            log.info("═══════════════════════════════════════");

            // 解析token获取用户ID
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            log.info("用户ID: {}", userId);

            // 提取动态内容
            String content = postData.get("content");
            String location = postData.get("location");
            String imageUrl = postData.get("imageUrl");

            if (content == null || content.trim().isEmpty()) {
                return ApiResponse.error("动态内容不能为空");
            }

            // 创建动态实体
            Post post = Post.builder()
                    .userId(userId)
                    .content(content)
                    .location(location)
                    .images(imageUrl != null && !imageUrl.trim().isEmpty() ? "[\"" + imageUrl + "\"]" : null)
                    .likeCount(0)
                    .commentCount(0)
                    .isFreeMinute(false)
                    .status(Post.PostStatus.PUBLISHED)
                    .build();

            post.setCreatedAt(LocalDateTime.now());
            post.setUpdatedAt(LocalDateTime.now());

            // 保存到数据库
            post = postRepository.save(post);

            log.info("✅ 动态创建成功 - postId: {}, userId: {}, content: {}",
                    post.getId(), userId, content);

            // 返回响应
            Map<String, Object> response = new HashMap<>();
            response.put("id", post.getId());
            response.put("userId", userId);
            response.put("content", content);
            response.put("location", location);
            response.put("createdAt", post.getCreatedAt().toString());

            return ApiResponse.success("动态发布成功", response);

        } catch (Exception e) {
            log.error("创建动态失败", e);
            return ApiResponse.error("创建动态失败: " + e.getMessage());
        }
    }

    /**
     * 获取增强的动态列表（Android端使用）
     */
    @GetMapping("/enhanced")
    public ApiResponse<Map<String, Object>> getEnhancedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nearby") String filter,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            log.info("获取增强动态列表 - page: {}, size: {}, filter: {}, sortBy: {}", page, size, filter, sortBy);

            // 调用基础的getPosts方法
            return getPosts(page, size, filter, authHeader);

        } catch (Exception e) {
            log.error("获取增强动态列表失败", e);
            return ApiResponse.error("获取动态列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取动态列表
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filter,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            log.info("获取动态列表 - page: {}, size: {}, filter: {}", page, size, filter);

            Pageable pageable = PageRequest.of(page, size);
            Page<Post> postsPage;

            // 根据筛选条件查询
            if ("freeMinute".equals(filter)) {
                postsPage = postRepository.findByIsFreeMinuteAndStatusOrderByCreatedAtDesc(
                        true, Post.PostStatus.PUBLISHED, pageable);
            } else if ("hot".equals(filter)) {
                postsPage = postRepository.findHotPosts(Post.PostStatus.PUBLISHED, pageable);
            } else {
                postsPage = postRepository.findByStatusOrderByCreatedAtDesc(
                        Post.PostStatus.PUBLISHED, pageable);
            }

            // 转换为DTO并填充用户信息
            List<PostDTO> postDTOs = postsPage.getContent().stream()
                    .map(post -> {
                        PostDTO dto = PostDTO.fromEntity(post);
                        // 填充用户信息
                        userRepository.findById(post.getUserId()).ifPresent(user -> {
                            dto.setUserNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
                            dto.setUserAvatar(user.getAvatarUrl());
                            dto.setUserGender(user.getGender() != null ? user.getGender().name() : null);
                            dto.setUserAge(user.getAge());
                            dto.setUserLocation(user.getLocation());
                            dto.setUserIsVerified(user.getIsVerified());
                            dto.setUserIsVip(user.getIsVip());
                        });
                        return dto;
                    })
                    .collect(Collectors.toList());

            // 构建分页响应
            Map<String, Object> response = new HashMap<>();
            response.put("content", postDTOs);
            response.put("totalElements", postsPage.getTotalElements());
            response.put("totalPages", postsPage.getTotalPages());
            response.put("size", postsPage.getSize());
            response.put("number", postsPage.getNumber());
            response.put("first", postsPage.isFirst());
            response.put("last", postsPage.isLast());

            log.info("动态列表获取成功 - 返回 {} 条动态", postDTOs.size());
            return ApiResponse.success(response);

        } catch (Exception e) {
            log.error("获取动态列表失败", e);
            return ApiResponse.error("获取动态列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定动态详情
     */
    @GetMapping("/{id}")
    public ApiResponse<PostDTO> getPost(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            log.info("获取动态详情 - postId: {}", id);

            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("动态不存在"));

            PostDTO dto = PostDTO.fromEntity(post);

            // 填充用户信息
            userRepository.findById(post.getUserId()).ifPresent(user -> {
                dto.setUserNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
                dto.setUserAvatar(user.getAvatarUrl());
                dto.setUserGender(user.getGender() != null ? user.getGender().name() : null);
                dto.setUserAge(user.getAge());
                dto.setUserLocation(user.getLocation());
                dto.setUserIsVerified(user.getIsVerified());
                dto.setUserIsVip(user.getIsVip());
            });

            return ApiResponse.success(dto);

        } catch (Exception e) {
            log.error("获取动态详情失败 - postId: {}", id, e);
            return ApiResponse.error("获取动态详情失败: " + e.getMessage());
        }
    }

    /**
     * 删除动态
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ApiResponse<String> deletePost(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("删除动态 - postId: {}", id);

            // 解析token获取用户ID
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);

            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("动态不存在"));

            // 验证权限
            if (!post.getUserId().equals(userId)) {
                return ApiResponse.error("无权限删除此动态");
            }

            // 软删除
            post.setStatus(Post.PostStatus.DELETED);
            postRepository.save(post);

            log.info("✅ 动态删除成功 - postId: {}", id);
            return ApiResponse.success("动态删除成功");

        } catch (Exception e) {
            log.error("删除动态失败 - postId: {}", id, e);
            return ApiResponse.error("删除动态失败: " + e.getMessage());
        }
    }
}
