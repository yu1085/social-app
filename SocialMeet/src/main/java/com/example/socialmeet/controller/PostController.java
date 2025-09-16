package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.PostDTO;
import com.example.socialmeet.entity.Post;
import com.example.socialmeet.repository.PostRepository;
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
@CrossOrigin(origins = "*")
public class PostController {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostDTO>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> posts = postRepository.findByStatusOrderByCreatedAtDesc("PUBLISHED", pageable);
            
            List<PostDTO> postDTOs = posts.getContent().stream()
                    .map(PostDTO::new)
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
            post.setImages(postDTO.getImages());
            post.setLocation(postDTO.getLocation());
            post.setStatus("PUBLISHED");
            
            post = postRepository.save(post);
            
            return ResponseEntity.ok(ApiResponse.success(new PostDTO(post)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("发布动态失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> getPostById(@PathVariable Long id) {
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("动态不存在"));
            
            return ResponseEntity.ok(ApiResponse.success(new PostDTO(post)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取动态详情失败: " + e.getMessage()));
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
            post.setImages(postDTO.getImages());
            post.setLocation(postDTO.getLocation());
            
            post = postRepository.save(post);
            
            return ResponseEntity.ok(ApiResponse.success(new PostDTO(post)));
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
            
            // TODO: 实现点赞功能
            post.setLikeCount(post.getLikeCount() + 1);
            post = postRepository.save(post);
            
            return ResponseEntity.ok(ApiResponse.success(new PostDTO(post)));
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
            
            // TODO: 实现取消点赞功能
            if (post.getLikeCount() > 0) {
                post.setLikeCount(post.getLikeCount() - 1);
                post = postRepository.save(post);
            }
            
            return ResponseEntity.ok(ApiResponse.success(new PostDTO(post)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("取消点赞失败: " + e.getMessage()));
        }
    }
}
