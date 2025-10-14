package com.example.myapplication.rtc

/**
 * 火山引擎 RTC 配置
 * 从火山引擎控制台获取：https://console.volcengine.com/rtc
 */
object RTCConfig {
    // ==================== 必填配置 ====================

    /**
     * 应用名称: defaultAppName
     * 创建时间: 2025-09-19 22:13:31
     */

    /**
     * AppID - 从火山引擎控制台获取
     * 位置：控制台 > 实时音视频 > 应用管理
     */
    const val APP_ID = "68cd650639d78f0174109df1"

    /**
     * AppKey - 用于服务端生成 Token（不应在客户端使用）
     * 注意：AppKey 是敏感信息，仅用于服务端
     * ⚠️ 生产环境请删除此配置，不要暴露在客户端代码中
     */
    const val APP_KEY = "d1c4f2e9f9ea496e9c4acc76dc2649bf" // 仅供参考，生产环境请移除

    // ==================== 测试配置 ====================

    /**
     * 默认房间ID
     * 多个设备使用相同房间ID才能互相通话
     */
    const val DEFAULT_ROOM_ID = "111"

    /**
     * 默认用户ID
     * 每个设备必须使用不同的用户ID
     */
    const val DEFAULT_USER_ID = "111"

    /**
     * 测试 Token
     * 注意：此 Token 将于 2025-10-19 19:23 到期
     * 过期后需要重新生成
     */
    const val TEST_TOKEN = "00168cd650639d78f0174109df1PABxjSsFtY/raDXK9GgDADExMQMAMTExBgAAADXK9GgBADXK9GgCADXK9GgDADXK9GgEADXK9GgFADXK9GggAG5p6vjl1HiCnmXt6IKHjtQbJI6xi92MtPa5ikTk8p38"

    // ==================== 生产环境说明 ====================

    /**
     * 生产环境配置步骤：
     *
     * 1. 获取 AppID 和 AppKey：
     *    - 登录火山引擎控制台：https://console.volcengine.com/rtc
     *    - 创建或选择应用
     *    - 获取 AppID 和 AppKey（保密）
     *
     * 2. Token 生成（必须在服务端）：
     *    - 使用 AppID、AppKey、RoomID、UserID 生成 Token
     *    - Token 包含权限和过期时间
     *    - 文档：https://www.volcengine.com/docs/6348/70121
     *
     * 3. 客户端获取 Token：
     *    - 从你的服务器 API 获取 Token
     *    - 不要在客户端硬编码 Token
     *    - 不要把 AppKey 放在客户端代码中
     *
     * 4. 安全建议：
     *    - AppKey 仅用于服务端，不要泄露
     *    - Token 应该有过期时间（建议24小时内）
     *    - 定期轮换 Token
     */
}
