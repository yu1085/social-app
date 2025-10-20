package com.socialmeet.backend.service;

import cn.jiguang.sdk.api.PushApi;
import cn.jiguang.sdk.bean.push.PushSendParam;
import cn.jiguang.sdk.bean.push.PushSendResult;
import cn.jiguang.sdk.bean.push.audience.Audience;
import cn.jiguang.sdk.bean.push.message.notification.NotificationMessage;
import cn.jiguang.sdk.bean.push.options.Options;
import cn.jiguang.sdk.enums.platform.Platform;
import com.socialmeet.backend.entity.User;
import com.socialmeet.backend.repository.UserRepository;
// import com.socialmeet.backend.service.UserDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JPush 推送服务
 * 使用极光推送最新SDK 5.2.9
 * 用于发送通话通知和其他推送消息
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JPushService {

    private static final String APP_KEY = "ff90a2867fcf541a3f3e8ed4";
    private static final String MASTER_SECRET = "112ee5a04324ae703d2d6b3d";

    private final PushApi pushApi;
    private final UserRepository userRepository;
    // private final UserDeviceService userDeviceService;

    /**
     * 发送来电通知
     *
     * @param receiverId   接收方用户ID
     * @param callerId     发起方用户ID
     * @param callerName   发起方昵称
     * @param callerAvatar 发起方头像URL
     * @param sessionId    通话会话ID
     * @param callType     通话类型 (VIDEO/VOICE)
     * @return 是否发送成功
     */
    public boolean sendCallNotification(Long receiverId, Long callerId, String callerName,
                                        String callerAvatar, String sessionId, String callType) {
        try {
            log.info("═══════════════════════════════════════");
            log.info("发送来电通知 - receiverId: {}, callerId: {}, callType: {}, sessionId: {}",
                    receiverId, callerId, callType, sessionId);

            // 获取接收方的Registration ID（单设备模式）
            User receiver = userRepository.findById(receiverId).orElse(null);
            if (receiver == null || receiver.getJpushRegistrationId() == null) {
                log.warn("接收方不存在或没有Registration ID - receiverId: {}", receiverId);
                return false;
            }
            List<String> registrationIds = Arrays.asList(receiver.getJpushRegistrationId());
            log.info("接收方设备 - receiverId: {}, registrationId: {}", receiverId, receiver.getJpushRegistrationId());

            log.info("向 {} 个设备发送推送 - receiverId: {}, registrationIds: {}",
                    registrationIds.size(), receiverId, registrationIds);

            // 构建自定义数据
            Map<String, Object> extras = new HashMap<>();
            extras.put("type", "INCOMING_CALL");
            extras.put("sessionId", sessionId);
            extras.put("callerId", String.valueOf(callerId));
            extras.put("callerName", callerName);
            extras.put("callerAvatar", callerAvatar != null ? callerAvatar : "");
            extras.put("callType", callType);
            extras.put("timestamp", String.valueOf(System.currentTimeMillis()));

            // 构建通知标题和内容
            String title = "VIDEO".equals(callType) ? "视频通话" : "语音通话";
            String content = callerName + " 向您发起" + ("VIDEO".equals(callType) ? "视频" : "语音") + "通话";

            log.info("推送内容 - 标题: {}, 内容: {}", title, content);
            log.info("自定义数据: {}", extras);

            // 构建 Android 通知 - 确保extras正确传递
            NotificationMessage.Android android = new NotificationMessage.Android();
            android.setAlert(content);
            android.setTitle(title);
            android.setExtras(extras);
            android.setStyle(1); // 大文本样式
            android.setPriority(2); // 高优先级
            android.setCategory("call"); // 通话类别
            android.setSmallIcon("ic_call"); // 对应AndroidManifest.xml中配置的图标
            // 添加更多Android特定设置
            android.setBuilderId(1); // 自定义通知样式
            android.setAlertType(1); // 通知类型
            
            // 调试：打印extras内容
            log.info("Android通知extras内容: {}", android.getExtras());

            // 构建通知消息
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setAlert(content);
            notificationMessage.setAndroid(android);

            // 构建推送参数
            PushSendParam param = new PushSendParam();

            // 设置目标受众 - 使用多个 Registration ID
            Audience audience = new Audience();
            audience.setRegistrationIdList(registrationIds);
            param.setAudience(audience);

            // 设置平台 - 只推送Android
            param.setPlatform(Arrays.asList(Platform.android));

            // 只设置通知消息 - 确保extras正确传递
            param.setNotification(notificationMessage);

            // 设置选项
            Options options = new Options();
            options.setApnsProduction(false); // 开发环境
            options.setTimeToLive(30L); // 消息保留30秒
            param.setOptions(options);

            log.info("推送参数构建完成 - audience: {}, platform: android", registrationIds);

            // 发送推送
            log.info("开始发送JPush推送 - receiverId: {}, deviceCount: {}", receiverId, registrationIds.size());
            PushSendResult result = pushApi.send(param);

            if (result != null) {
                log.info("✅ 来电通知发送成功 - receiverId: {}, deviceCount: {}", receiverId, registrationIds.size());
                log.info("推送结果详情: {}", result);
                log.info("═══════════════════════════════════════");
                return true;
            } else {
                log.error("❌ 来电通知发送失败 - receiverId: {}, deviceCount: {}, result: {}",
                        receiverId, registrationIds.size(), result);
                log.info("═══════════════════════════════════════");
                return false;
            }

        } catch (Exception e) {
            log.error("❌ 发送来电通知异常 - receiverId: {}", receiverId, e);
            log.info("═══════════════════════════════════════");
            return false;
        }
    }

    /**
     * 发送通话状态更新通知（多设备支持）
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @param status    状态 (ACCEPTED/REJECTED/ENDED)
     * @param message   消息内容
     * @return 是否发送成功
     */
    public boolean sendCallStatusNotification(Long userId, String sessionId, String status, String message) {
        try {
            log.info("发送通话状态通知 - userId: {}, sessionId: {}, status: {}",
                    userId, sessionId, status);

            // 获取用户Registration ID（单设备模式）
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || user.getJpushRegistrationId() == null) {
                log.warn("用户不存在或没有Registration ID - userId: {}", userId);
                return false;
            }
            List<String> registrationIds = Arrays.asList(user.getJpushRegistrationId());

            // 构建自定义数据
            Map<String, Object> extras = new HashMap<>();
            extras.put("type", "CALL_STATUS");
            extras.put("sessionId", sessionId);
            extras.put("status", status);
            extras.put("message", message); // 添加message字段
            extras.put("timestamp", String.valueOf(System.currentTimeMillis()));

            // 构建 Android 通知（简单的状态更新）
            NotificationMessage.Android android = new NotificationMessage.Android();
            android.setAlert(message);
            android.setTitle("通话状态更新");
            android.setExtras(extras);
            // 设置小图标 - 这是关键！
            android.setSmallIcon("ic_call"); // 对应AndroidManifest.xml中配置的图标

            // 构建通知消息
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setAlert(message);
            notificationMessage.setAndroid(android);

            // 构建推送参数
            PushSendParam param = new PushSendParam();

            // 设置目标受众 - 使用多个 Registration ID
            Audience audience = new Audience();
            audience.setRegistrationIdList(registrationIds);
            param.setAudience(audience);

            // 设置平台
            param.setPlatform(Arrays.asList(Platform.android));

            // 设置通知消息
            param.setNotification(notificationMessage);

            // 设置选项
            Options options = new Options();
            options.setApnsProduction(false);
            options.setTimeToLive(30L); // 消息保留30秒
            param.setOptions(options);

            // 发送推送
            log.info("开始发送通话状态通知 - userId: {}, deviceCount: {}", userId, registrationIds.size());
            PushSendResult result = pushApi.send(param);

            if (result != null) {
                log.info("✅ 通话状态通知发送成功 - userId: {}, deviceCount: {}, result: {}",
                        userId, registrationIds.size(), result);
                return true;
            } else {
                log.error("❌ 通话状态通知发送失败 - userId: {}, deviceCount: {}, result: {}",
                        userId, registrationIds.size(), result);
                return false;
            }

        } catch (Exception e) {
            log.error("发送通话状态通知失败 - userId: {}", userId, e);
            return false;
        }
    }

    /**
     * 为用户设置别名（用于推送）
     * 注意：设置别名需要在客户端完成
     *
     * @param userId 用户ID
     * @param alias  别名
     * @return 是否设置成功
     */
    public boolean setUserAlias(Long userId, String alias) {
        log.info("别名设置提示 - userId: {}, alias: {}", userId, alias);
        log.info("注意：设置别名需要在Android客户端调用 JPush.setAlias(context, sequence, alias)");
        // 别名设置需要在客户端完成，这里只是记录
        return true;
    }

    /**
     * 发送通用推送通知（多设备支持）
     *
     * @param userId         用户ID
     * @param title          通知标题
     * @param content        通知内容
     * @param extras         自定义数据
     * @return 是否发送成功
     */
    public boolean sendNotification(Long userId, String title, String content, Map<String, Object> extras) {
        try {
            log.info("发送通用推送通知 - userId: {}, title: {}, content: {}", userId, title, content);

            // 获取用户Registration ID（单设备模式）
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || user.getJpushRegistrationId() == null) {
                log.warn("用户不存在或没有Registration ID - userId: {}", userId);
                return false;
            }
            List<String> registrationIds = Arrays.asList(user.getJpushRegistrationId());

            // 构建 Android 通知
            NotificationMessage.Android android = new NotificationMessage.Android();
            android.setAlert(content);
            android.setTitle(title);
            android.setExtras(extras != null ? extras : new HashMap<>());
            android.setStyle(1); // 大文本样式
            android.setPriority(2); // 高优先级
            // 设置小图标 - 这是关键！
            android.setSmallIcon("ic_call"); // 对应AndroidManifest.xml中配置的图标

            // 构建通知消息
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setAlert(content);
            notificationMessage.setAndroid(android);

            // 构建推送参数
            PushSendParam param = new PushSendParam();

            // 设置目标受众 - 使用多个 Registration ID
            Audience audience = new Audience();
            audience.setRegistrationIdList(registrationIds);
            param.setAudience(audience);

            // 设置平台
            param.setPlatform(Arrays.asList(Platform.android));
            param.setNotification(notificationMessage);

            // 设置选项
            Options options = new Options();
            options.setApnsProduction(false);
            options.setTimeToLive(30L);
            param.setOptions(options);

            // 发送推送
            log.info("开始发送通用推送通知 - userId: {}, deviceCount: {}", userId, registrationIds.size());
            PushSendResult result = pushApi.send(param);

            if (result != null) {
                log.info("✅ 通用推送通知发送成功 - userId: {}, deviceCount: {}, result: {}",
                        userId, registrationIds.size(), result);
                return true;
            } else {
                log.error("❌ 通用推送通知发送失败 - userId: {}, deviceCount: {}, result: {}",
                        userId, registrationIds.size(), result);
                return false;
            }

        } catch (Exception e) {
            log.error("❌ 发送通用推送通知异常 - userId: {}", userId, e);
            return false;
        }
    }


    /**
     * 发送简单测试推送（多设备支持）
     */
    public boolean sendTestNotification(Long userId) {
        try {
            log.info("发送测试推送 - userId: {}", userId);
            
            // 获取用户Registration ID（单设备模式）
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || user.getJpushRegistrationId() == null) {
                log.warn("用户不存在或没有Registration ID - userId: {}", userId);
                return false;
            }
            List<String> registrationIds = Arrays.asList(user.getJpushRegistrationId());
            
            // 构建简单的推送内容
            String title = "测试推送";
            String content = "这是一条测试推送消息";
            
            // 构建自定义数据
            Map<String, Object> extras = new HashMap<>();
            extras.put("type", "TEST");
            extras.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            // 构建 Android 通知
            NotificationMessage.Android android = new NotificationMessage.Android();
            android.setAlert(content);
            android.setTitle(title);
            android.setExtras(extras);
            // 设置小图标 - 这是关键！
            android.setSmallIcon("ic_call"); // 对应AndroidManifest.xml中配置的图标
            
            // 构建通知消息
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setAlert(content);
            notificationMessage.setAndroid(android);
            
            // 构建推送参数
            PushSendParam param = new PushSendParam();
            
            // 设置目标受众 - 使用多个 Registration ID
            Audience audience = new Audience();
            audience.setRegistrationIdList(registrationIds);
            param.setAudience(audience);
            
            // 设置平台
            param.setPlatform(Arrays.asList(Platform.android));
            param.setNotification(notificationMessage);
            
            // 设置选项
            Options options = new Options();
            options.setApnsProduction(false);
            options.setTimeToLive(30L);
            param.setOptions(options);
            
            // 发送推送
            log.info("开始发送测试推送 - userId: {}, deviceCount: {}", userId, registrationIds.size());
            PushSendResult result = pushApi.send(param);
            
            if (result != null) {
                log.info("✅ 测试推送发送成功 - userId: {}, deviceCount: {}, result: {}", 
                        userId, registrationIds.size(), result);
                return true;
            } else {
                log.error("❌ 测试推送发送失败 - userId: {}, deviceCount: {}, result: {}", 
                        userId, registrationIds.size(), result);
                return false;
            }
            
        } catch (Exception e) {
            log.error("❌ 发送测试推送异常 - userId: {}", userId, e);
            return false;
        }
    }
}
