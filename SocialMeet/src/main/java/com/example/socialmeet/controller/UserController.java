package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.UserDTO;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.UserRepository;
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
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            return ResponseEntity.ok(ApiResponse.success(new UserDTO(user)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户信息失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserDTO>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String gender,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            List<User> users;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                users = userRepository.findByNicknameContaining(keyword.trim());
            } else if (location != null && !location.trim().isEmpty()) {
                users = userRepository.findByLocationContaining(location.trim());
            } else if (gender != null) {
                users = userRepository.findByGender(gender);
            } else {
                users = userRepository.findActiveUsers();
            }
            
            List<UserDTO> userDTOs = users.stream()
                    .map(UserDTO::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(userDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("搜索用户失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            return ResponseEntity.ok(ApiResponse.success(new UserDTO(user)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户信息失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/follow/{userId}")
    public ResponseEntity<ApiResponse<String>> followUser(@RequestHeader("Authorization") String authHeader, 
                                                         @PathVariable Long userId) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long followerId = jwtUtil.getUserIdFromToken(jwt);
            
            // TODO: 实现关注功能
            return ResponseEntity.ok(ApiResponse.success("关注成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("关注失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/follow/{userId}")
    public ResponseEntity<ApiResponse<String>> unfollowUser(@RequestHeader("Authorization") String authHeader, 
                                                           @PathVariable Long userId) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long followerId = jwtUtil.getUserIdFromToken(jwt);
            
            // TODO: 实现取消关注功能
            return ResponseEntity.ok(ApiResponse.success("取消关注成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("取消关注失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/following")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getFollowing(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            // TODO: 实现获取关注列表功能
            return ResponseEntity.ok(ApiResponse.success(List.of()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取关注列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getFollowers(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            // TODO: 实现获取粉丝列表功能
            return ResponseEntity.ok(ApiResponse.success(List.of()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取粉丝列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取首页用户卡片列表 - 包含用户状态和价格信息
     */
    @GetMapping("/home-cards")
    public ResponseEntity<ApiResponse<List<Object>>> getHomeUserCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // 获取活跃用户
            List<User> users = userRepository.findActiveUsers();
            
            // 限制返回数量
            int start = page * size;
            int end = Math.min(start + size, users.size());
            List<User> pagedUsers = users.subList(start, end);
            
            // 构建用户卡片数据
            List<Object> userCards = pagedUsers.stream().map(user -> {
                java.util.Map<String, Object> card = new java.util.HashMap<>();
                card.put("id", user.getId());
                card.put("nickname", user.getNickname());
                card.put("avatar", user.getAvatarUrl());
                card.put("age", user.getAge());
                card.put("location", user.getLocation());
                card.put("bio", user.getBio());
                card.put("isOnline", user.getIsOnline());
                
                // 根据在线状态设置状态和价格
                if (user.getIsOnline()) {
                    card.put("status", "空闲");
                    card.put("statusColor", "green");
                    card.put("callPrice", 300);
                } else {
                    card.put("status", "离线");
                    card.put("statusColor", "gray");
                    card.put("callPrice", 200);
                }
                
                return card;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(userCards));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户卡片失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户详情 - 包含状态和价格信息
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<Object>> getUserDetail(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            java.util.Map<String, Object> userDetail = new java.util.HashMap<>();
            userDetail.put("id", user.getId());
            userDetail.put("nickname", user.getNickname());
            userDetail.put("avatar", user.getAvatarUrl());
            userDetail.put("age", user.getAge());
            userDetail.put("location", user.getLocation());
            userDetail.put("bio", user.getBio());
            userDetail.put("isOnline", user.getIsOnline());
            userDetail.put("gender", user.getGender());
            userDetail.put("createdAt", user.getCreatedAt());
            
            // 根据在线状态设置状态和价格
            if (user.getIsOnline()) {
                userDetail.put("status", "空闲");
                userDetail.put("statusColor", "green");
                userDetail.put("callPrice", 300);
                userDetail.put("messagePrice", 10);
            } else {
                userDetail.put("status", "离线");
                userDetail.put("statusColor", "gray");
                userDetail.put("callPrice", 200);
                userDetail.put("messagePrice", 8);
            }
            
            return ResponseEntity.ok(ApiResponse.success(userDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户详情失败: " + e.getMessage()));
        }
    }
}
