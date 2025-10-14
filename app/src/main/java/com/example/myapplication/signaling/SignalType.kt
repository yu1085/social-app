package com.example.myapplication.signaling

/**
 * 信令类型定义
 * 定义了完整的呼叫流程中的所有信令消息类型
 */
enum class SignalType {
    // ========== 呼叫相关 ==========
    /**
     * 发起呼叫
     * A -> Server -> B: 用户A向用户B发起视频通话请求
     */
    CALL_REQUEST,

    /**
     * 接听呼叫
     * B -> Server -> A: 用户B接听了用户A的呼叫
     */
    CALL_ACCEPT,

    /**
     * 拒绝呼叫
     * B -> Server -> A: 用户B拒绝了用户A的呼叫
     */
    CALL_REJECT,

    /**
     * 取消呼叫
     * A -> Server -> B: 用户A在对方接听前取消了呼叫
     */
    CALL_CANCEL,

    /**
     * 呼叫超时
     * Server -> A/B: 呼叫超时未接听（通常30秒）
     */
    CALL_TIMEOUT,

    /**
     * 忙线
     * B -> Server -> A: 用户B正在通话中，无法接听
     */
    CALL_BUSY,

    // ========== 通话中 ==========
    /**
     * 挂断通话
     * A/B -> Server -> B/A: 任意一方主动挂断通话
     */
    CALL_END,

    /**
     * 用户离开
     * Server -> A/B: 对方用户离开了通话（异常断线）
     */
    USER_LEAVE,

    // ========== 连接相关 ==========
    /**
     * 用户上线
     * Client -> Server: 用户登录并连接到信令服务器
     */
    USER_ONLINE,

    /**
     * 用户离线
     * Server -> Others: 用户断开连接或离线
     */
    USER_OFFLINE,

    /**
     * 心跳
     * Client <-> Server: 保持连接活跃的心跳消息
     */
    HEARTBEAT
}
