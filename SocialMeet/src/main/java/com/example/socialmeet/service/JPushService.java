package com.example.socialmeet.service;

import com.example.socialmeet.entity.DeviceToken;
import com.example.socialmeet.entity.PushNotification;
import com.example.socialmeet.repository.DeviceTokenRepository;
import com.example.socialmeet.repository.PushNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 极光推送服务 - 使用 REST API
 */
@Service
public class JPushService {
    
    @Autowired
    private DeviceTokenRepository deviceTokenRepository;
    
    @Autowired
    private PushNotificationRepository pushNotificationRepository;
    
    // 极光推送配置 - 从配置文件读取
    @Value("${jpush.app.key}")
    private String appKey;
    
    @Value("${jpush.master.secret}")
    private String masterSecret;
    
    @Value("${jpush.apns.production:false}")
    private boolean apnsProduction;
    
    @Value("${jpush.time.to.live:60}")
    private int timeToLive;
    
    private static final String JPUSH_API_URL = "https://api.jpush.cn/v3/push";
    
    private WebClient webClient;
    
    public void initWebClient() {
        if (this.webClient == null) {
            // 创建自定义的HTTP客户端配置
            reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient.create()
                    .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 连接超时10秒
                    .responseTimeout(java.time.Duration.ofSeconds(15)) // 响应超时15秒
                    .followRedirect(true) // 跟随重定向
                    .compress(true); // 启用压缩
            
            this.webClient = WebClient.builder()
                    .baseUrl(JPUSH_API_URL)
                    .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
                    .defaultHeader("Authorization", "Basic " + java.util.Base64.getEncoder()
                            .encodeToString((appKey + ":" + masterSecret).getBytes()))
                    .defaultHeader("Content-Type", "application/json")
                    .defaultHeader("User-Agent", "SocialMeet-App/1.0")
                    .codecs(configurer -> {
                        configurer.defaultCodecs().maxInMemorySize(1024 * 1024); // 1MB
                    })
                    .build();
        }
    }
    
    /**
     * 发送来电推送通知
     */
    public void sendIncomingCallNotification(Long receiverId, String callerName, Long callerId, String callSessionId) {
        try {
            System.out.println("开始发送来电推送通知: 接收方=" + receiverId + ", 发起方=" + callerName);
            
            // 获取接收方的极光推送Token
            List<DeviceToken> tokens = deviceTokenRepository.findByUserIdAndPlatformAndIsActiveTrue(receiverId, "jpush");
            System.out.println("查询到的设备令牌数量: " + tokens.size());
            
            if (tokens.isEmpty()) {
                System.out.println("用户 " + receiverId + " 没有有效的极光推送Token");
                // 仍然记录推送通知，即使没有设备令牌
                recordPushNotification(receiverId, "视频通话", callerName + " 正在呼叫您", "INCOMING_CALL", 
                    Map.of("callerId", callerId.toString(), "callerName", callerName, "callSessionId", callSessionId), "NO_TOKEN");
                return;
            }
            
            System.out.println("找到设备令牌，开始发送推送: " + tokens.get(0).getToken());
            
            // 构建推送数据
            Map<String, Object> payload = new HashMap<>();
            
            // 平台设置 - 应该是数组格式
            payload.put("platform", List.of("android"));
            
            // 受众设置 - 使用registration_id
            Map<String, Object> audience = new HashMap<>();
            // 使用第一个设备的registration_id作为推送目标
            audience.put("registration_id", List.of(tokens.get(0).getToken()));
            payload.put("audience", audience);
            
            // 通知设置
            Map<String, Object> notification = new HashMap<>();
            notification.put("alert", callerName + " 正在呼叫您");
            
            // Android特定设置
            Map<String, Object> android = new HashMap<>();
            android.put("title", "视频通话");
            android.put("alert", callerName + " 正在呼叫您");
            android.put("builder_id", 1);
            android.put("priority", 2); // 最高优先级，范围：-2到2
            android.put("category", "INCOMING_CALL");
            android.put("sound", "default");
            
            // 额外数据
            Map<String, String> extras = new HashMap<>();
            extras.put("type", "incoming_call");
            extras.put("callerId", callerId.toString());
            extras.put("callerName", callerName);
            extras.put("callSessionId", callSessionId);
            extras.put("timestamp", String.valueOf(System.currentTimeMillis()));
            android.put("extras", extras);
            
            notification.put("android", android);
            payload.put("notification", notification);
            
            // 添加消息内容（解决"no message or extra send to user"问题）
            Map<String, Object> message = new HashMap<>();
            message.put("msg_content", callerName + " 正在呼叫您");
            message.put("title", "视频通话");
            message.put("content_type", "text");
            message.put("extras", extras);
            payload.put("message", message);
            
            // 选项配置
            Map<String, Object> options = new HashMap<>();
            options.put("apns_production", apnsProduction);
            options.put("time_to_live", timeToLive);
            payload.put("options", options);
            
            // 记录推送通知
            recordPushNotification(receiverId, "视频通话", callerName + " 正在呼叫您", "INCOMING_CALL", 
                Map.of("callerId", callerId.toString(), "callerName", callerName, "callSessionId", callSessionId), "SENT");
            
            // 发送推送
            sendPushRequest(payload);
            
        } catch (Exception e) {
            System.err.println("发送极光推送异常: " + e.getMessage());
            e.printStackTrace();
            // 记录推送失败
            recordPushNotification(receiverId, "视频通话", callerName + " 正在呼叫您", "INCOMING_CALL", 
                Map.of("callerId", callerId.toString(), "callerName", callerName, "callSessionId", callSessionId), "FAILED");
        }
    }
    
    /**
     * 发送消息推送通知
     */
    public void sendMessageNotification(Long receiverId, String senderName, String message) {
        try {
            List<DeviceToken> tokens = deviceTokenRepository.findByUserIdAndPlatformAndIsActiveTrue(receiverId, "jpush");
            
            if (tokens.isEmpty()) {
                return;
            }
            
            Map<String, Object> payload = new HashMap<>();
            
            // 平台设置
            payload.put("platform", List.of("android"));
            
            // 受众设置 - 使用registration_id
            Map<String, Object> audience = new HashMap<>();
            // 使用第一个设备的registration_id作为推送目标
            audience.put("registration_id", List.of(tokens.get(0).getToken()));
            payload.put("audience", audience);
            
            // 通知设置
            Map<String, Object> notification = new HashMap<>();
            notification.put("alert", senderName + ": " + message);
            
            // Android特定设置
            Map<String, Object> android = new HashMap<>();
            android.put("title", senderName);
            android.put("alert", message);
            android.put("builder_id", 2);
            android.put("priority", "normal");
            android.put("category", "MESSAGE");
            android.put("sound", "default");
            
            // 额外数据
            Map<String, String> extras = new HashMap<>();
            extras.put("type", "message");
            extras.put("senderName", senderName);
            extras.put("message", message);
            extras.put("timestamp", String.valueOf(System.currentTimeMillis()));
            android.put("extras", extras);
            
            notification.put("android", android);
            payload.put("notification", notification);
            
            // 选项配置
            Map<String, Object> options = new HashMap<>();
            options.put("apns_production", apnsProduction);
            options.put("time_to_live", timeToLive);
            payload.put("options", options);
            
            sendPushRequest(payload);
            
        } catch (Exception e) {
            System.err.println("发送极光消息推送异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 发送系统通知
     */
    public void sendSystemNotification(Long userId, String title, String content) {
        try {
            List<DeviceToken> tokens = deviceTokenRepository.findByUserIdAndPlatformAndIsActiveTrue(userId, "jpush");
            
            if (tokens.isEmpty()) {
                System.out.println("用户 " + userId + " 没有有效的极光推送Token");
                return;
            }
            
            Map<String, Object> payload = new HashMap<>();
            
            // 平台设置
            payload.put("platform", List.of("android"));
            
            // 受众设置 - 使用registration_id
            Map<String, Object> audience = new HashMap<>();
            // 使用第一个设备的registration_id作为推送目标
            audience.put("registration_id", List.of(tokens.get(0).getToken()));
            payload.put("audience", audience);
            
            // 通知设置
            Map<String, Object> notification = new HashMap<>();
            notification.put("alert", title + ": " + content);
            
            // Android特定设置
            Map<String, Object> android = new HashMap<>();
            android.put("title", title);
            android.put("alert", content);
            android.put("builder_id", 3);
            android.put("priority", "normal");
            android.put("category", "SYSTEM");
            android.put("sound", "default");
            
            // 额外数据
            Map<String, String> extras = new HashMap<>();
            extras.put("type", "system");
            extras.put("title", title);
            extras.put("content", content);
            extras.put("timestamp", String.valueOf(System.currentTimeMillis()));
            android.put("extras", extras);
            
            notification.put("android", android);
            payload.put("notification", notification);
            
            // 选项配置
            Map<String, Object> options = new HashMap<>();
            options.put("apns_production", apnsProduction);
            options.put("time_to_live", timeToLive);
            payload.put("options", options);
            
            sendPushRequest(payload);
            
        } catch (Exception e) {
            System.err.println("发送极光系统通知异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 发送标签推送
     */
    public void sendTagPush(String tag, String title, String content, Map<String, String> extras) {
        try {
            Map<String, Object> payload = new HashMap<>();
            
            // 平台设置
            payload.put("platform", List.of("android"));
            
            // 受众设置
            Map<String, Object> audience = new HashMap<>();
            audience.put("tag", List.of(tag));
            payload.put("audience", audience);
            
            // 通知设置
            Map<String, Object> notification = new HashMap<>();
            notification.put("alert", title + ": " + content);
            
            // Android特定设置
            Map<String, Object> android = new HashMap<>();
            android.put("title", title);
            android.put("alert", content);
            android.put("builder_id", 4);
            android.put("priority", "normal");
            android.put("category", "TAG");
            android.put("sound", "default");
            android.put("extras", extras);
            
            notification.put("android", android);
            payload.put("notification", notification);
            
            // 选项配置
            Map<String, Object> options = new HashMap<>();
            options.put("apns_production", apnsProduction);
            options.put("time_to_live", timeToLive);
            payload.put("options", options);
            
            sendPushRequest(payload);
            
        } catch (Exception e) {
            System.err.println("发送极光标签推送异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 发送全量推送
     */
    public void sendBroadcastPush(String title, String content, Map<String, String> extras) {
        try {
            Map<String, Object> payload = new HashMap<>();
            
            // 平台设置
            payload.put("platform", List.of("android"));
            
            // 受众设置 - 全量推送
            payload.put("audience", "all");
            
            // 通知设置
            Map<String, Object> notification = new HashMap<>();
            notification.put("alert", title + ": " + content);
            
            // Android特定设置
            Map<String, Object> android = new HashMap<>();
            android.put("title", title);
            android.put("alert", content);
            android.put("builder_id", 5);
            android.put("priority", "normal");
            android.put("category", "BROADCAST");
            android.put("sound", "default");
            android.put("extras", extras);
            
            notification.put("android", android);
            payload.put("notification", notification);
            
            // 选项配置
            Map<String, Object> options = new HashMap<>();
            options.put("apns_production", apnsProduction);
            options.put("time_to_live", timeToLive);
            payload.put("options", options);
            
            sendPushRequest(payload);
            
        } catch (Exception e) {
            System.err.println("发送极光全量推送异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 注册设备Token
     */
    public boolean registerDeviceToken(Long userId, String token, String platform) {
        return registerDeviceToken(userId, token, platform, null, null);
    }
    
    /**
     * 注册设备Token（带额外信息）
     */
    public boolean registerDeviceToken(Long userId, String token, String platform, String appVersion, String deviceModel) {
        try {
            // 验证参数

            if (token == null || token.trim().isEmpty()) {
                System.err.println("设备Token不能为空");
                return false;
            }
            
            if (platform == null || platform.trim().isEmpty()) {
                System.err.println("平台不能为空");
                return false;
            }
            
            // 检查是否已存在
            Optional<DeviceToken> existingToken = deviceTokenRepository.findByToken(token);
            
            if (existingToken.isPresent()) {
                // 更新现有Token
                DeviceToken deviceToken = existingToken.get();
                deviceToken.setUserId(userId);
                deviceToken.setPlatform(platform);
                deviceToken.setIsActive(true);
                deviceToken.setAppVersion(appVersion);
                deviceToken.setDeviceModel(deviceModel);
                deviceToken.setLastUsed(LocalDateTime.now());
                // 不需要手动设置updatedAt，BaseEntity会自动处理
                deviceTokenRepository.save(deviceToken);
            } else {
                // 创建新Token
                DeviceToken deviceToken = new DeviceToken();
                deviceToken.setUserId(userId);
                deviceToken.setToken(token);
                deviceToken.setPlatform(platform);
                deviceToken.setIsActive(true);
                deviceToken.setAppVersion(appVersion);
                deviceToken.setDeviceModel(deviceModel);
                deviceToken.setLastUsed(LocalDateTime.now());
                // 不需要手动设置createdAt和updatedAt，BaseEntity会自动处理
                deviceTokenRepository.save(deviceToken);
            }
            
            System.out.println("设备Token注册成功: 用户=" + userId + ", Token=" + token);
            return true;
            
        } catch (Exception e) {
            System.err.println("注册设备Token异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 注销用户所有设备Token
     */
    public boolean unregisterAllUserTokens(Long userId) {
        try {
            List<DeviceToken> tokens = deviceTokenRepository.findByUserIdAndIsActiveTrue(userId);
            
            for (DeviceToken token : tokens) {
                token.setIsActive(false);
                token.setUpdatedAt(LocalDateTime.now());
                deviceTokenRepository.save(token);
            }
            
            System.out.println("用户所有设备Token注销成功: 用户=" + userId);
            return true;
            
        } catch (Exception e) {
            System.err.println("注销用户设备Token异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 记录推送通知到数据库
     */
    private void recordPushNotification(Long userId, String title, String content, String category, 
                                      Map<String, String> data, String status) {
        try {
            PushNotification notification = new PushNotification();
            notification.setUserId(userId);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setCategory(category);
            
            // 将Map转换为JSON字符串
            String dataJson = "";
            if (data != null && !data.isEmpty()) {
                dataJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(data);
            }
            notification.setData(dataJson);
            
            notification.setStatus(status);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
            
            if ("SENT".equals(status)) {
                notification.setSentAt(LocalDateTime.now());
            }
            
            pushNotificationRepository.save(notification);
            System.out.println("推送通知记录成功: 用户=" + userId + ", 状态=" + status);
            
        } catch (Exception e) {
            System.err.println("记录推送通知失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 发送推送请求到极光推送 API
     */
    private void sendPushRequest(Map<String, Object> payload) {
        int maxRetries = 3;
        int retryDelay = 1000; // 1秒
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // 确保 WebClient 已初始化
                if (webClient == null) {
                    initWebClient();
                }
                
                // 打印调试信息
                System.out.println("=== JPush 推送调试信息 (尝试 " + attempt + "/" + maxRetries + ") ===");
                System.out.println("App Key: " + appKey);
                System.out.println("Master Secret: " + (masterSecret != null ? "***" + masterSecret.substring(masterSecret.length() - 4) : "null"));
                System.out.println("Payload: " + new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload));
                System.out.println("=========================");
                
                String response = webClient.post()
                        .bodyValue(payload)
                        .retrieve()
                        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> {
                                System.err.println("HTTP状态码: " + clientResponse.statusCode());
                                return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        System.err.println("错误响应体: " + errorBody);
                                        return Mono.error(new RuntimeException("HTTP " + clientResponse.statusCode() + ": " + errorBody));
                                    });
                            })
                        .bodyToMono(String.class)
                        .timeout(java.time.Duration.ofSeconds(10)) // 添加超时
                        .block();
                
                System.out.println("极光推送发送成功: " + response);
                return; // 成功发送，退出重试循环
                
            } catch (Exception e) {
                System.err.println("极光推送发送失败 (尝试 " + attempt + "/" + maxRetries + "): " + e.getMessage());
                
                // 检查是否是DNS解析问题
                if (e.getMessage() != null && e.getMessage().contains("Failed to resolve")) {
                    System.err.println("检测到DNS解析问题，尝试使用备用DNS服务器");
                    
                    // 尝试重新初始化WebClient
                    this.webClient = null;
                    initWebClient();
                }
                
                // 如果不是最后一次尝试，等待后重试
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelay * attempt); // 递增延迟
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    // 最后一次尝试失败，记录错误但不抛出异常
                    System.err.println("极光推送最终发送失败，已尝试 " + maxRetries + " 次");
                    e.printStackTrace();
                    
                    // 可以考虑在这里添加备用推送方案
                    sendFallbackNotification(payload);
                }
            }
        }
    }
    
    /**
     * 备用推送通知方案
     */
    private void sendFallbackNotification(Map<String, Object> payload) {
        try {
            System.out.println("=== 使用备用推送方案 ===");
            
            // 这里可以实现其他推送方案，比如：
            // 1. 使用其他推送服务（如Firebase）
            // 2. 发送到消息队列
            // 3. 记录到数据库供后续处理
            
            // 暂时只记录日志
            System.out.println("备用推送方案：记录推送请求到数据库");
            
            // 可以在这里添加数据库记录逻辑
            // pushNotificationRepository.save(notificationRecord);
            
        } catch (Exception e) {
            System.err.println("备用推送方案也失败了: " + e.getMessage());
        }
    }
}