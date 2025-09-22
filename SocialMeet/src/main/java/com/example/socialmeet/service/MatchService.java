package com.example.socialmeet.service;

import com.example.socialmeet.entity.MatchRequest;
import com.example.socialmeet.entity.MatchSession;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.entity.CallSettings;
import com.example.socialmeet.repository.MatchRequestRepository;
import com.example.socialmeet.repository.MatchSessionRepository;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.repository.CallSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class MatchService {
    
    @Autowired
    private MatchRequestRepository matchRequestRepository;
    
    @Autowired
    private MatchSessionRepository matchSessionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CallSettingsRepository callSettingsRepository;
    
    /**
     * 创建匹配请求
     */
    @Transactional
    public MatchRequest createMatchRequest(Long userId, String matchType, Integer preferenceLevel) {
        // 取消用户之前的待匹配请求
        matchRequestRepository.cancelUserPendingRequests(userId);
        
        // 根据偏好等级确定价格范围
        Double[] priceRange = getPriceRangeByPreference(preferenceLevel);
        
        // 创建新的匹配请求
        MatchRequest matchRequest = new MatchRequest(
            userId, 
            matchType, 
            preferenceLevel, 
            priceRange[0], 
            priceRange[1]
        );
        
        return matchRequestRepository.save(matchRequest);
    }
    
    /**
     * 尝试匹配用户
     */
    @Transactional
    public MatchSession tryMatch(Long userId) {
        // 查找用户的待匹配请求
        Optional<MatchRequest> userRequestOpt = matchRequestRepository.findPendingRequestByUserId(userId);
        if (!userRequestOpt.isPresent()) {
            throw new RuntimeException("用户没有待匹配的请求");
        }
        
        MatchRequest userRequest = userRequestOpt.get();
        
        // 根据价格区间查找可匹配的女性用户
        List<User> availableUsers = findAvailableFemaleUsers(
            userRequest.getMinPrice(), 
            userRequest.getMaxPrice(),
            userId
        );
        
        if (availableUsers.isEmpty()) {
            return null; // 没有可匹配的用户
        }
        
        // 随机选择一个女性用户
        Random random = new Random();
        User matchedUser = availableUsers.get(random.nextInt(availableUsers.size()));
        
        // 获取匹配用户的价格设置
        Double callPrice = getCallPriceForUser(matchedUser.getId());
        
        // 创建匹配会话
        String sessionId = generateSessionId();
        MatchSession matchSession = new MatchSession(
            sessionId,
            userId,
            matchedUser.getId(),
            userRequest.getMatchType(),
            callPrice
        );
        
        // 更新请求状态
        userRequest.setStatus("MATCHED");
        userRequest.setMatchedUserId(matchedUser.getId());
        userRequest.setMatchedAt(LocalDateTime.now());
        
        // 保存数据
        matchRequestRepository.save(userRequest);
        matchSessionRepository.save(matchSession);
        
        System.out.println("=== 匹配成功 ===");
        System.out.println("匹配用户ID: " + matchedUser.getId());
        System.out.println("匹配用户昵称: " + matchedUser.getNickname());
        System.out.println("通话价格: " + callPrice);
        
        return matchSession;
    }
    
    /**
     * 获取匹配结果
     */
    public MatchSession getMatchResult(Long userId) {
        return matchSessionRepository.findActiveSessionByUserId(userId).orElse(null);
    }
    
    /**
     * 结束匹配会话
     */
    @Transactional
    public void endMatchSession(String sessionId) {
        matchSessionRepository.endSession(sessionId);
    }
    
    /**
     * 清理过期的匹配请求
     */
    @Transactional
    public void cleanupExpiredRequests() {
        List<MatchRequest> expiredRequests = matchRequestRepository.findExpiredRequests(LocalDateTime.now());
        for (MatchRequest request : expiredRequests) {
            request.setStatus("EXPIRED");
            matchRequestRepository.save(request);
        }
    }
    
    /**
     * 根据偏好等级获取价格范围
     */
    private Double[] getPriceRangeByPreference(Integer preferenceLevel) {
        switch (preferenceLevel) {
            case 1: // 活跃女生
                return new Double[]{100.0, 200.0};
            case 2: // 人气女生
                return new Double[]{200.0, 350.0};
            case 3: // 高颜女生
                return new Double[]{350.0, 500.0};
            default:
                return new Double[]{100.0, 200.0};
        }
    }
    
    /**
     * 获取用户的通话价格
     */
    private Double getCallPriceForUser(Long userId) {
        Optional<CallSettings> settingsOpt = callSettingsRepository.findByUserId(userId);
        if (settingsOpt.isPresent()) {
            return settingsOpt.get().getVideoCallPrice();
        }
        return 100.0; // 默认价格
    }
    
    /**
     * 查找可匹配的女性用户
     * 只匹配在线且空闲的用户
     */
    private List<User> findAvailableFemaleUsers(Double minPrice, Double maxPrice, Long excludeUserId) {
        System.out.println("=== 查找可匹配的女性用户 ===");
        System.out.println("价格区间: " + minPrice + " - " + maxPrice);
        System.out.println("排除用户ID: " + excludeUserId);
        
        // 查找所有在线且空闲的女性用户
        List<User> femaleUsers = userRepository.findByGenderAndIsOnlineAndStatus("FEMALE", true, "ONLINE");
        System.out.println("找到在线且空闲的女性用户数量: " + femaleUsers.size());
        
        for (User user : femaleUsers) {
            System.out.println("在线空闲女性用户: ID=" + user.getId() + ", 昵称=" + user.getNickname() + 
                             ", 性别=" + user.getGender() + ", 在线=" + user.getIsOnline() + ", 状态=" + user.getStatus());
        }
        
        // 过滤出价格在区间内且不是自己的用户
        List<User> availableUsers = femaleUsers.stream()
            .filter(user -> {
                // 排除自己
                if (user.getId().equals(excludeUserId)) {
                    System.out.println("排除自己: " + user.getId());
                    return false;
                }
                // 检查价格区间
                Double userPrice = getCallPriceForUser(user.getId());
                boolean priceMatch = userPrice >= minPrice && userPrice <= maxPrice;
                System.out.println("用户ID: " + user.getId() + ", 价格: " + userPrice + ", 价格匹配: " + priceMatch);
                return priceMatch;
            })
            .collect(java.util.stream.Collectors.toList());
            
        System.out.println("最终可匹配用户数量: " + availableUsers.size());
        for (User user : availableUsers) {
            System.out.println("可匹配用户: ID=" + user.getId() + ", 昵称=" + user.getNickname() + ", 状态=" + user.getStatus());
        }
        
        return availableUsers;
    }
    
    /**
     * 生成会话ID
     */
    private String generateSessionId() {
        return "match_" + System.currentTimeMillis() + "_" + (new Random().nextInt(1000));
    }
}
