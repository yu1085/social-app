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
            @RequestParam(required = false) User.Gender gender,
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
}
