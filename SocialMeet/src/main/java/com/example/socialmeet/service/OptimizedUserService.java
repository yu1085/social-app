package com.example.socialmeet.service;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.UserDTO;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.OptimizedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 优化后的用户服务
 */
@Service
public class OptimizedUserService {
    
    @Autowired
    private OptimizedUserRepository userRepository;
    
    /**
     * 获取用户详情 - 带缓存
     */
    @Cacheable(value = "users", key = "#userId")
    public ApiResponse<UserDTO> getUserById(Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ApiResponse.error("用户不存在");
            }
            
            User user = userOpt.get();
            UserDTO userDTO = new UserDTO(user);
            
            return ApiResponse.success(userDTO);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户卡片列表 - 带缓存
     */
    @Cacheable(value = "userCards", key = "#gender + '_' + #category + '_' + #page + '_' + #size")
    public ApiResponse<List<UserDTO>> getUserCards(String gender, String category, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastSeen"));
            
            Page<User> usersPage;
            switch (category.toLowerCase()) {
                case "online":
                    usersPage = userRepository.findByGenderAndIsOnlineAndStatusOrderByLastSeenDesc(gender, true, "ONLINE", pageable);
                    break;
                case "nearby":
                    // 这里可以根据位置筛选附近用户
                    usersPage = userRepository.findByGenderAndIsOnlineOrderByLastSeenDesc(gender, true, pageable);
                    break;
                case "new":
                    // 新用户 - 按创建时间排序
                    usersPage = userRepository.findByGenderOrderByCreatedAtDesc(gender, pageable);
                    break;
                case "hot":
                    // 热门用户 - 按关注数排序
                    usersPage = userRepository.findByGenderOrderByFollowerCountDesc(gender, pageable);
                    break;
                default:
                    usersPage = userRepository.findByGenderAndIsOnlineOrderByLastSeenDesc(gender, true, pageable);
            }
            
            List<UserDTO> userDTOs = usersPage.getContent().stream()
                    .map(UserDTO::new)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(userDTOs);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索用户 - 带缓存
     */
    @Cacheable(value = "users", key = "#keyword + '_' + #location + '_' + #gender + '_' + #page + '_' + #size")
    public ApiResponse<List<UserDTO>> searchUsers(String keyword, String location, String gender, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastSeen"));
            Page<User> usersPage;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                usersPage = userRepository.findByNicknameContainingAndGender(keyword.trim(), gender, pageable);
            } else if (location != null && !location.trim().isEmpty()) {
                usersPage = userRepository.findByLocationContainingAndGender(location.trim(), gender, pageable);
            } else if (gender != null) {
                usersPage = userRepository.findByGenderOrderByLastSeenDesc(gender, pageable);
            } else {
                usersPage = userRepository.findActiveUsers(LocalDateTime.now().minusDays(7), pageable);
            }
            
            List<UserDTO> userDTOs = usersPage.getContent().stream()
                    .map(UserDTO::new)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(userDTOs);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("搜索用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户信息 - 带缓存失效
     */
    @Transactional
    @CacheEvict(value = {"users", "userCards"}, allEntries = true)
    public ApiResponse<UserDTO> updateUser(Long userId, UserDTO userDTO) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ApiResponse.error("用户不存在");
            }
            
            User user = userOpt.get();
            
            // 更新用户信息
            if (userDTO.getNickname() != null) {
                user.setNickname(userDTO.getNickname());
            }
            if (userDTO.getAvatarUrl() != null) {
                user.setAvatarUrl(userDTO.getAvatarUrl());
            }
            if (userDTO.getLocation() != null) {
                user.setLocation(userDTO.getLocation());
            }
            if (userDTO.getBio() != null) {
                user.setBio(userDTO.getBio());
            }
            if (userDTO.getGender() != null) {
                user.setGender(userDTO.getGender());
            }
            
            user.setUpdatedAt(LocalDateTime.now());
            User savedUser = userRepository.save(user);
            
            UserDTO updatedUserDTO = new UserDTO(savedUser);
            return ApiResponse.success(updatedUserDTO);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户在线状态 - 带缓存失效
     */
    @Transactional
    @CacheEvict(value = {"users", "userCards"}, allEntries = true)
    public ApiResponse<String> updateUserStatus(Long userId, String status) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ApiResponse.error("用户不存在");
            }
            
            User user = userOpt.get();
            user.setStatus(status);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
            
            return ApiResponse.success("状态更新成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 关注用户 - 带缓存失效
     */
    @Transactional
    @CacheEvict(value = {"users", "userCards"}, allEntries = true)
    public ApiResponse<String> followUser(Long followerId, Long followingId) {
        try {
            // 这里需要实现关注逻辑
            // 可以创建Follow实体和Repository
            
            // 异步更新统计
            updateUserStatsAsync(followingId);
            
            return ApiResponse.success("关注成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("关注失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消关注用户 - 带缓存失效
     */
    @Transactional
    @CacheEvict(value = {"users", "userCards"}, allEntries = true)
    public ApiResponse<String> unfollowUser(Long followerId, Long followingId) {
        try {
            // 这里需要实现取消关注逻辑
            
            // 异步更新统计
            updateUserStatsAsync(followingId);
            
            return ApiResponse.success("取消关注成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("取消关注失败: " + e.getMessage());
        }
    }
    
    /**
     * 异步更新用户统计
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> updateUserStatsAsync(Long userId) {
        try {
            // 更新用户关注数、粉丝数等统计信息
            // 这里可以添加统计逻辑
            Thread.sleep(100); // 模拟异步处理
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * 获取在线用户数量
     */
    @Cacheable(value = "users", key = "'online_count'")
    public ApiResponse<Long> getOnlineUserCount() {
        try {
            long count = userRepository.countByIsOnlineAndStatus(true, "ONLINE");
            return ApiResponse.success(count);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取在线用户数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户统计信息
     */
    @Cacheable(value = "users", key = "#userId + '_stats'")
    public ApiResponse<Object> getUserStats(Long userId) {
        try {
            // 这里可以返回用户的详细统计信息
            // 如动态数、关注数、粉丝数等
            return ApiResponse.success("用户统计信息");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取用户统计失败: " + e.getMessage());
        }
    }
}
