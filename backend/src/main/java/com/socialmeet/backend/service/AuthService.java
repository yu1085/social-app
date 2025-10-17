package com.socialmeet.backend.service;

import com.socialmeet.backend.dto.LoginResponse;
import com.socialmeet.backend.dto.UserDTO;
import com.socialmeet.backend.entity.User;
import com.socialmeet.backend.entity.VerificationCode;
import com.socialmeet.backend.repository.UserRepository;
import com.socialmeet.backend.repository.VerificationCodeRepository;
import com.socialmeet.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 认证服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final JwtUtil jwtUtil;

    @Value("${verification.code-length:6}")
    private int codeLength;

    @Value("${verification.expire-minutes:5}")
    private int expireMinutes;

    @Value("${verification.test-mode:true}")
    private boolean testMode;

    @Value("${verification.test-code:123456}")
    private String testCode;

    /**
     * 发送验证码
     */
    @Transactional
    public String sendVerificationCode(String phone) {
        log.info("发送验证码到手机号: {}", phone);

        // 检查是否频繁发送（1分钟内只能发送一次）
        verificationCodeRepository.findFirstByPhoneOrderByCreatedAtDesc(phone)
                .ifPresent(lastCode -> {
                    if (lastCode.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
                        throw new RuntimeException("发送验证码过于频繁，请1分钟后再试");
                    }
                });

        // 生成验证码
        String code = testMode ? testCode : generateCode();

        // 保存验证码到数据库
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setPhone(phone);
        verificationCode.setCode(code);
        verificationCode.setType(VerificationCode.CodeType.LOGIN);
        verificationCode.setIsUsed(false);
        verificationCode.setExpiredAt(LocalDateTime.now().plusMinutes(expireMinutes));

        verificationCodeRepository.save(verificationCode);

        // 在测试模式下，直接返回验证码（实际生产中应发送短信）
        if (testMode) {
            log.info("测试模式：验证码 = {}", code);
            return "验证码已发送（测试模式）: " + code;
        }

        // TODO: 在生产环境中，调用短信服务发送验证码
        // smsService.sendCode(phone, code);

        log.info("验证码已发送到 {}", phone);
        return "验证码已发送";
    }

    /**
     * 验证码登录或注册
     */
    @Transactional
    public LoginResponse loginWithVerificationCode(String phone, String code) {
        log.info("验证码登录请求 - 手机号: {}", phone);

        // 验证验证码
        VerificationCode verificationCode = verificationCodeRepository
                .findByPhoneAndCodeAndIsUsedFalseAndExpiredAtAfter(phone, code, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("验证码无效或已过期"));

        // 标记验证码为已使用
        verificationCode.setIsUsed(true);
        verificationCodeRepository.save(verificationCode);

        // 查找或创建用户
        User user = userRepository.findByPhone(phone)
                .orElseGet(() -> createNewUser(phone));

        // 更新最后活跃时间
        user.setLastActiveAt(LocalDateTime.now());
        user.setIsOnline(true);
        userRepository.save(user);

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        log.info("用户 {} 登录成功", user.getUsername());

        return new LoginResponse(token, UserDTO.fromEntity(user));
    }

    /**
     * 创建新用户（自动注册）
     */
    private User createNewUser(String phone) {
        log.info("创建新用户 - 手机号: {}", phone);

        User user = new User();
        user.setPhone(phone);
        user.setUsername("user_" + phone.substring(phone.length() - 6));
        user.setNickname("用户" + phone.substring(phone.length() - 4));
        user.setIsVerified(false);
        user.setIsVip(false);
        user.setVipLevel(0);
        user.setWealthLevel(0);
        user.setIsOnline(true);
        user.setStatus(User.UserStatus.ACTIVE);

        return userRepository.save(user);
    }

    /**
     * 生成随机验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 获取用户信息
     */
    public UserDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return UserDTO.fromEntity(user);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserDTO updateUserProfile(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 更新允许修改的字段
        if (userDTO.getNickname() != null) {
            user.setNickname(userDTO.getNickname());
        }
        if (userDTO.getGender() != null) {
            user.setGender(User.Gender.valueOf(userDTO.getGender()));
        }
        if (userDTO.getBirthday() != null) {
            user.setBirthday(userDTO.getBirthday());
        }
        if (userDTO.getLocation() != null) {
            user.setLocation(userDTO.getLocation());
        }
        if (userDTO.getSignature() != null) {
            user.setSignature(userDTO.getSignature());
        }
        if (userDTO.getHeight() != null) {
            user.setHeight(userDTO.getHeight());
        }
        if (userDTO.getWeight() != null) {
            user.setWeight(userDTO.getWeight());
        }

        user = userRepository.save(user);
        return UserDTO.fromEntity(user);
    }

    /**
     * 更新用户的 JPush Registration ID
     */
    @Transactional
    public void updateRegistrationId(Long userId, String registrationId) {
        log.info("更新用户 Registration ID - userId: {}, registrationId: {}", userId, registrationId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setJpushRegistrationId(registrationId);
        userRepository.save(user);

        log.info("Registration ID 更新成功 - userId: {}", userId);
    }

    /**
     * 搜索用户
     */
    public java.util.List<UserDTO> searchUsers(
            String keyword,
            String gender,
            String location,
            Integer minAge,
            Integer maxAge,
            int page,
            int size) {

        log.info("搜索用户 - keyword: {}, gender: {}, location: {}, minAge: {}, maxAge: {}, page: {}, size: {}",
                keyword, gender, location, minAge, maxAge, page, size);

        // 获取所有用户
        java.util.List<User> allUsers = userRepository.findAll();
        java.util.stream.Stream<User> userStream = allUsers.stream();

        // 应用筛选条件
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            userStream = userStream.filter(user ->
                    (user.getNickname() != null && user.getNickname().toLowerCase().contains(lowerKeyword)) ||
                    (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerKeyword))
            );
        }

        if (gender != null && !gender.trim().isEmpty() && !"不限".equals(gender)) {
            try {
                User.Gender genderEnum = User.Gender.valueOf(gender.toUpperCase());
                userStream = userStream.filter(user -> genderEnum.equals(user.getGender()));
            } catch (IllegalArgumentException e) {
                log.warn("无效的性别参数: {}", gender);
            }
        }

        if (location != null && !location.trim().isEmpty() && !"不限".equals(location)) {
            userStream = userStream.filter(user ->
                    user.getLocation() != null && user.getLocation().contains(location)
            );
        }

        if (minAge != null || maxAge != null) {
            LocalDateTime now = LocalDateTime.now();
            userStream = userStream.filter(user -> {
                if (user.getBirthday() == null) return false;
                int age = now.getYear() - user.getBirthday().getYear();
                if (minAge != null && age < minAge) return false;
                if (maxAge != null && age > maxAge) return false;
                return true;
            });
        }

        // 转换为列表，并应用分页
        java.util.List<User> filteredUsers = userStream.collect(java.util.stream.Collectors.toList());

        int start = page * size;
        int end = Math.min(start + size, filteredUsers.size());

        if (start >= filteredUsers.size()) {
            return java.util.Collections.emptyList();
        }

        java.util.List<User> pagedUsers = filteredUsers.subList(start, end);

        return pagedUsers.stream()
                .map(UserDTO::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }
}
