package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.UserDTO;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users/profile")
@CrossOrigin(originPatterns = "*")
public class UserProfileController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 测试JWT token验证
     */
    @GetMapping("/test-token")
    public ResponseEntity<Map<String, Object>> testToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        
        System.out.println("=== 测试Token接口被调用 ===");
        System.out.println("Authorization Header: " + authHeader);
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.err.println("Authorization header缺失或格式错误");
                result.put("success", false);
                result.put("error", "Authorization header missing or invalid");
                return ResponseEntity.status(401).body(result);
            }
            
            String token = authHeader.substring(7);
            System.out.println("提取的Token: " + token);
            System.out.println("Token长度: " + token.length());
            System.out.println("Token包含点号数量: " + (token.split("\\.").length - 1));
            
            result.put("token", token);
            result.put("tokenLength", token.length());
            
            // 验证token
            System.out.println("开始验证Token...");
            boolean isValid = jwtUtil.validateToken(token);
            System.out.println("Token验证结果: " + isValid);
            result.put("isValid", isValid);
            
            if (isValid) {
                try {
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    String username = jwtUtil.getUsernameFromToken(token);
                    result.put("userId", userId);
                    result.put("username", username);
                    result.put("success", true);
                } catch (Exception e) {
                    result.put("success", false);
                    result.put("error", "Failed to parse token claims: " + e.getMessage());
                }
            } else {
                result.put("success", false);
                result.put("error", "Token is invalid or expired");
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Exception: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 更新用户完整资料
     * 支持多种路径: PUT /api/users/profile/{id}, PUT /api/users/profile
     */
    @PutMapping(value = {"/{id}", ""})
    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(
            @PathVariable(required = false) Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> profileData) {
        try {
            System.out.println("=== 更新用户资料接口被调用 ===");
            System.out.println("路径ID: " + id);
            System.out.println("Authorization: " + authHeader);
            System.out.println("请求数据: " + profileData);
            
            // 验证JWT token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.err.println("Authorization header缺失或格式错误");
                return ResponseEntity.status(401).body(ApiResponse.error("未授权访问"));
            }
            
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                System.err.println("Token验证失败");
                return ResponseEntity.status(401).body(ApiResponse.error("Token无效或已过期"));
            }
            
            // 从token中获取用户信息
            Long tokenUserId = jwtUtil.getUserIdFromToken(token);
            System.out.println("Token中的用户ID: " + tokenUserId);
            
            // 如果没有提供ID，使用token中的用户ID
            if (id == null) {
                id = tokenUserId;
            }
            
            // 验证用户ID是否匹配（只有用户本人可以修改自己的资料）
            if (!tokenUserId.equals(id)) {
                System.err.println("用户ID不匹配: token=" + tokenUserId + ", path=" + id);
                return ResponseEntity.status(403).body(ApiResponse.error("无权限访问此用户资源"));
            }
            
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            // 更新基本信息
            if (profileData.containsKey("nickname")) {
                user.setNickname((String) profileData.get("nickname"));
            }
            if (profileData.containsKey("bio")) {
                user.setBio((String) profileData.get("bio"));
            }
            if (profileData.containsKey("location")) {
                user.setLocation((String) profileData.get("location"));
            }
            if (profileData.containsKey("gender")) {
                user.setGender((String) profileData.get("gender"));
            }
            
            // 更新生日
            if (profileData.containsKey("birthday")) {
                String birthdayStr = (String) profileData.get("birthday");
                if (birthdayStr != null && !birthdayStr.isEmpty()) {
                    try {
                        LocalDateTime birthDate = LocalDateTime.parse(birthdayStr + "T00:00:00");
                        user.setBirthDate(birthDate);
                        // 自动计算年龄
                        int age = calculateAge(birthDate);
                        user.setAge(age);
                    } catch (Exception e) {
                        // 忽略日期解析错误
                    }
                }
            }
            
            // 更新身高体重
            if (profileData.containsKey("height")) {
                Object heightObj = profileData.get("height");
                if (heightObj instanceof Number) {
                    user.setHeight(((Number) heightObj).intValue());
                } else if (heightObj instanceof String) {
                    String heightStr = (String) heightObj;
                    heightStr = heightStr.replaceAll("[^0-9]", ""); // 移除非数字字符
                    if (!heightStr.isEmpty()) {
                        user.setHeight(Integer.parseInt(heightStr));
                    }
                }
            }
            if (profileData.containsKey("weight")) {
                Object weightObj = profileData.get("weight");
                if (weightObj instanceof Number) {
                    user.setWeight(((Number) weightObj).intValue());
                } else if (weightObj instanceof String) {
                    String weightStr = (String) weightObj;
                    weightStr = weightStr.replaceAll("[^0-9]", ""); // 移除非数字字符
                    if (!weightStr.isEmpty()) {
                        user.setWeight(Integer.parseInt(weightStr));
                    }
                }
            }
            
            // 更新教育背景和收入
            if (profileData.containsKey("education")) {
                user.setEducation((String) profileData.get("education"));
            }
            if (profileData.containsKey("income")) {
                user.setIncome((String) profileData.get("income"));
            }
            
            // 更新新增的社交应用字段
            if (profileData.containsKey("realName")) {
                user.setRealName((String) profileData.get("realName"));
            }
            if (profileData.containsKey("zodiacSign")) {
                user.setZodiacSign((String) profileData.get("zodiacSign"));
            }
            if (profileData.containsKey("occupation")) {
                user.setOccupation((String) profileData.get("occupation"));
            }
            if (profileData.containsKey("relationshipStatus")) {
                user.setRelationshipStatus((String) profileData.get("relationshipStatus"));
            }
            if (profileData.containsKey("residenceStatus")) {
                user.setResidenceStatus((String) profileData.get("residenceStatus"));
            }
            if (profileData.containsKey("houseOwnership")) {
                Object houseObj = profileData.get("houseOwnership");
                if (houseObj instanceof Boolean) {
                    user.setHouseOwnership((Boolean) houseObj);
                } else if (houseObj instanceof String) {
                    user.setHouseOwnership(Boolean.parseBoolean((String) houseObj));
                }
            }
            if (profileData.containsKey("carOwnership")) {
                Object carObj = profileData.get("carOwnership");
                if (carObj instanceof Boolean) {
                    user.setCarOwnership((Boolean) carObj);
                } else if (carObj instanceof String) {
                    user.setCarOwnership(Boolean.parseBoolean((String) carObj));
                }
            }
            if (profileData.containsKey("hobbies")) {
                user.setHobbies((String) profileData.get("hobbies"));
            }
            if (profileData.containsKey("languages")) {
                user.setLanguages((String) profileData.get("languages"));
            }
            if (profileData.containsKey("bloodType")) {
                user.setBloodType((String) profileData.get("bloodType"));
            }
            if (profileData.containsKey("smoking")) {
                Object smokingObj = profileData.get("smoking");
                if (smokingObj instanceof Boolean) {
                    user.setSmoking((Boolean) smokingObj);
                } else if (smokingObj instanceof String) {
                    user.setSmoking(Boolean.parseBoolean((String) smokingObj));
                }
            }
            if (profileData.containsKey("drinking")) {
                Object drinkingObj = profileData.get("drinking");
                if (drinkingObj instanceof Boolean) {
                    user.setDrinking((Boolean) drinkingObj);
                } else if (drinkingObj instanceof String) {
                    user.setDrinking(Boolean.parseBoolean((String) drinkingObj));
                }
            }
            if (profileData.containsKey("tags")) {
                user.setTags((String) profileData.get("tags"));
            }
            
            // 更新头像
            if (profileData.containsKey("avatarUrl")) {
                user.setAvatarUrl((String) profileData.get("avatarUrl"));
            }
            
            // 设置更新时间
            user.setUpdatedAt(LocalDateTime.now());
            
            User savedUser = userRepository.save(user);
            System.out.println("用户资料更新成功: ID=" + savedUser.getId());
            
            return ResponseEntity.ok(ApiResponse.success(new UserDTO(savedUser)));
            
        } catch (RuntimeException e) {
            System.err.println("更新用户资料失败 - 运行时异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("更新用户资料失败: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("更新用户资料失败 - 未知异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("服务器内部错误: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户资料（带认证）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(@RequestHeader("Authorization") String token) {
        System.out.println("=== 获取用户资料接口被调用 ===");
        System.out.println("Authorization Header: " + token);
        
        try {
            String jwt = token.replace("Bearer ", "");
            System.out.println("提取的JWT: " + jwt);
            System.out.println("JWT长度: " + jwt.length());
            System.out.println("JWT包含点号数量: " + (jwt.split("\\.").length - 1));
            
            System.out.println("开始从JWT中提取用户ID...");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            System.out.println("提取到的用户ID: " + userId);
            
            System.out.println("开始查询用户信息...");
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            System.out.println("找到用户: " + user.getUsername());
            
            return ResponseEntity.ok(ApiResponse.success(new UserDTO(user)));
        } catch (Exception e) {
            System.err.println("获取用户资料失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("获取用户信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            user.setStatus(status);
            userRepository.save(user);
            
            return ResponseEntity.ok(ApiResponse.success("状态更新成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新状态失败: " + e.getMessage()));
        }
    }
    
    /**
     * 计算年龄
     */
    private int calculateAge(LocalDateTime birthDate) {
        LocalDateTime now = LocalDateTime.now();
        int age = now.getYear() - birthDate.getYear();
        if (now.getMonthValue() < birthDate.getMonthValue() || 
            (now.getMonthValue() == birthDate.getMonthValue() && now.getDayOfMonth() < birthDate.getDayOfMonth())) {
            age--;
        }
        return age;
    }
}
