package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.LoginRequest;
import com.example.socialmeet.dto.LoginResponse;
import com.example.socialmeet.dto.UserDTO;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.entity.CallSettings;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.repository.CallSettingsRepository;
import com.example.socialmeet.service.AuthService;
import com.example.socialmeet.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CallSettingsRepository callSettingsRepository;
    
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(@RequestParam String phone) {
        try {
            System.out.println("=== 发送验证码请求 ===");
            System.out.println("手机号: " + phone);
            
            String code = authService.sendVerificationCode(phone);
            
            return ResponseEntity.ok(ApiResponse.success("验证码发送成功", code));
        } catch (Exception e) {
            System.out.println("发送验证码失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("发送验证码失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/login-with-code")
    public ResponseEntity<ApiResponse<LoginResponse>> loginWithCode(
            @RequestParam String phone,
            @RequestParam String code,
            @RequestParam(required = false) String gender) {
        try {
            System.out.println("=== 登录请求 ===");
            System.out.println("手机号: " + phone);
            System.out.println("验证码: " + code);
            System.out.println("性别: " + gender);
            
            LoginResponse response = authService.loginWithVerificationCode(phone, code, gender);
            System.out.println("登录成功，用户ID: " + response.getUser().getId());
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            System.out.println("登录失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("登录失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@RequestBody Map<String, Object> requestData) {
        try {
            System.out.println("=== 用户注册请求 ===");
            System.out.println("请求数据: " + requestData);
            
            String username = (String) requestData.get("username");
            String password = (String) requestData.get("password");
            String phone = (String) requestData.get("phone");
            String gender = (String) requestData.get("gender");
            
            if (username == null || password == null || phone == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户名、密码和手机号不能为空"));
            }
            
            // 检查用户名是否已存在
            if (userRepository.findByUsername(username).isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户名已存在"));
            }
            
            // 检查手机号是否已存在
            if (userRepository.findByPhone(phone).isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("手机号已注册"));
            }
            
            // 创建新用户
            User user = new User();
            Long userId = generateUserId();
            user.setId(userId);
            user.setUsername(username);
            user.setPassword(password); // 注意：实际项目中应该加密密码
            user.setPhone(phone);
            user.setGender(gender != null ? gender : "MALE");
            user.setNickname(username);
            user.setIsActive(true);
            user.setIsOnline(true);
            user.setStatus("ONLINE");
            user.setLastSeen(LocalDateTime.now());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            // 生成基本信息
            generateBasicInfo(user);
            
            // 保存用户
            User savedUser = userRepository.save(user);
            
            // 创建默认通话设置
            createDefaultCallSettings(savedUser.getId());
            
            // 生成JWT token
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());
            
            System.out.println("用户注册成功，用户ID: " + savedUser.getId());
            
            return ResponseEntity.ok(ApiResponse.success(new LoginResponse(token, new UserDTO(savedUser))));
        } catch (Exception e) {
            System.out.println("用户注册失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("注册失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody Map<String, Object> requestData) {
        try {
            System.out.println("=== 用户登录请求 ===");
            System.out.println("请求数据: " + requestData);
            
            String username = (String) requestData.get("username");
            String password = (String) requestData.get("password");
            
            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户名和密码不能为空"));
            }
            
            // 查找用户
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            // 验证密码（注意：实际项目中应该使用加密密码比较）
            if (!password.equals(user.getPassword())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("密码错误"));
            }
            
            // 更新用户在线状态
            user.setIsOnline(true);
            user.setStatus("ONLINE");
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
            
            // 生成JWT token
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());
            
            System.out.println("用户登录成功，用户ID: " + user.getId());
            
            return ResponseEntity.ok(ApiResponse.success(new LoginResponse(token, new UserDTO(user))));
        } catch (Exception e) {
            System.out.println("用户登录失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("登录失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("认证服务正常"));
    }
    
    /**
     * 生成用户ID
     */
    private Long generateUserId() {
        Random random = new Random();
        Long userId;
        int attempts = 0;
        int maxAttempts = 100;
        
        do {
            userId = 10000000L + random.nextLong() % 90000000L;
            if (userId < 0) {
                userId = Math.abs(userId);
            }
            attempts++;
        } while (userRepository.existsById(userId) && attempts < maxAttempts);
        
        if (attempts >= maxAttempts) {
            userId = System.currentTimeMillis() % 100000000L;
        }
        
        return userId;
    }
    
    /**
     * 生成用户基本信息
     */
    private void generateBasicInfo(User user) {
        Random random = new Random();
        
        // 生成年龄 (18-35岁)
        int age = random.nextInt(18) + 18;
        user.setAge(age);
        
        // 生成出生日期
        LocalDateTime now = LocalDateTime.now();
        user.setBirthDate(now.minusYears(age).minusDays(random.nextInt(365)));
        
        // 生成位置信息
        String[] cities = {
            "北京市", "上海市", "广州市", "深圳市", "杭州市", "南京市", 
            "武汉市", "成都市", "西安市", "重庆市", "天津市", "苏州市"
        };
        String city = cities[random.nextInt(cities.length)];
        user.setLocation(city);
        
        // 生成坐标
        user.setLatitude(generateLatitude(city));
        user.setLongitude(generateLongitude(city));
        
        // 生成头像URL
        user.setAvatarUrl("https://via.placeholder.com/200x200/4A90E2/FFFFFF?text=头像");
        
        // 生成个人简介
        user.setBio(generateBio(user.getGender()));
        
        // 生成身高体重
        if (user.getGender().equals("MALE")) {
            user.setHeight(165 + random.nextInt(20));
            user.setWeight(60 + random.nextInt(30));
        } else {
            user.setHeight(155 + random.nextInt(15));
            user.setWeight(45 + random.nextInt(25));
        }
        
        // 生成学历
        String[] educations = {"高中", "大专", "本科", "硕士", "博士"};
        user.setEducation(educations[random.nextInt(educations.length)]);
        
        // 生成收入
        String[] incomes = {"3K以下", "3-5K", "5-8K", "8-12K", "12-20K", "20K以上"};
        user.setIncome(incomes[random.nextInt(incomes.length)]);
    }
    
    private Double generateLatitude(String city) {
        switch (city) {
            case "北京市": return 39.9042;
            case "上海市": return 31.2304;
            case "广州市": return 23.1291;
            case "深圳市": return 22.5431;
            case "杭州市": return 30.2741;
            case "南京市": return 32.0603;
            case "武汉市": return 30.5928;
            case "成都市": return 30.5728;
            case "西安市": return 34.3416;
            case "重庆市": return 29.4316;
            case "天津市": return 39.3434;
            case "苏州市": return 31.2989;
            default: return 39.9042;
        }
    }
    
    private Double generateLongitude(String city) {
        switch (city) {
            case "北京市": return 116.4074;
            case "上海市": return 121.4737;
            case "广州市": return 113.2644;
            case "深圳市": return 114.0579;
            case "杭州市": return 120.1551;
            case "南京市": return 118.7969;
            case "武汉市": return 114.3055;
            case "成都市": return 104.0668;
            case "西安市": return 108.9398;
            case "重庆市": return 106.9123;
            case "天津市": return 117.3616;
            case "苏州市": return 120.5853;
            default: return 116.4074;
        }
    }
    
    private String generateBio(String gender) {
        String[] maleBios = {
            "阳光开朗的男孩，喜欢运动和旅行",
            "热爱生活，积极向上，寻找志同道合的朋友",
            "工作认真，生活有趣，希望能遇到有趣的你",
            "喜欢音乐和电影，享受简单快乐的生活",
            "健身爱好者，追求健康的生活方式"
        };
        
        String[] femaleBios = {
            "温柔可爱的女孩，喜欢读书和美食",
            "热爱生活，喜欢旅行和摄影",
            "工作努力，生活精致，期待美好的相遇",
            "喜欢音乐和艺术，享受安静美好的时光",
            "瑜伽爱好者，追求内心的平静与美好"
        };
        
        Random random = new Random();
        if (gender.equals("MALE")) {
            return maleBios[random.nextInt(maleBios.length)];
        } else {
            return femaleBios[random.nextInt(femaleBios.length)];
        }
    }
    
    /**
     * 为新用户创建默认通话设置
     */
    private void createDefaultCallSettings(Long userId) {
        try {
            CallSettings callSettings = new CallSettings(userId);
            callSettingsRepository.save(callSettings);
        } catch (Exception e) {
            System.err.println("创建默认通话设置失败: " + e.getMessage());
        }
    }
}
