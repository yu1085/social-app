package com.example.myapplication.rtc

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.ss.bytertc.engine.RTCRoom
import com.ss.bytertc.engine.RTCRoomConfig
import com.ss.bytertc.engine.RTCVideo
import com.ss.bytertc.engine.UserInfo
import com.ss.bytertc.engine.VideoCanvas
import com.ss.bytertc.engine.data.CameraId
import com.ss.bytertc.engine.data.MirrorType
import com.ss.bytertc.engine.data.RemoteStreamKey
import com.ss.bytertc.engine.data.StreamIndex
import com.ss.bytertc.engine.handler.IRTCRoomEventHandler
import com.ss.bytertc.engine.handler.IRTCVideoEventHandler
import com.ss.bytertc.engine.type.ChannelProfile
import com.ss.bytertc.engine.type.MediaStreamType
import com.ss.bytertc.engine.type.RTCRoomStats
import com.ss.bytertc.engine.type.StreamRemoveReason
import com.ss.bytertc.engine.video.VideoCaptureConfig

/**
 * 火山引擎 RTC 管理类
 * 负责管理 RTC 引擎的生命周期和回调
 */
class RTCManager private constructor() {

    companion object {
        private const val TAG = "RTCManager"
        val instance: RTCManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RTCManager()
        }
    }

    private var rtcVideo: RTCVideo? = null
    private var rtcRoom: RTCRoom? = null
    private var currentRoomId: String? = null
    private var currentUserId: String? = null

    // 回调监听器
    var onRemoteUserJoined: ((userId: String) -> Unit)? = null
    var onRemoteUserLeft: ((userId: String) -> Unit)? = null
    var onLocalJoinRoom: ((isSuccess: Boolean) -> Unit)? = null
    var onMessageReceived: ((fromUserId: String, message: String) -> Unit)? = null

    /**
     * 创建 TextureView (使用 Android 原生 TextureView)
     */
    fun createTextureView(context: Context): android.view.View {
        // 使用 Android 原生 TextureView
        val textureView = android.view.TextureView(context)
        textureView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return textureView
    }

    /**
     * 初始化 RTC 引擎
     */
    fun initEngine(context: Context, appId: String) {
        if (rtcVideo != null) {
            Log.w(TAG, "RTC Engine already initialized")
            return
        }

        try {
            // 创建 RTCVideo 实例
            rtcVideo = RTCVideo.createRTCVideo(
                context.applicationContext,
                appId,
                rtcVideoEventHandler,
                null,
                null
            )

            // 设置视频采集参数
            val captureConfig = VideoCaptureConfig().apply {
                width = 640
                height = 480
                frameRate = 15
            }
            rtcVideo?.setVideoCaptureConfig(captureConfig)

            Log.d(TAG, "RTC Engine initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize RTC Engine", e)
        }
    }

    /**
     * 加入房间
     */
    fun joinRoom(roomId: String, userId: String, token: String) {
        if (rtcVideo == null) {
            Log.e(TAG, "RTC Engine not initialized")
            return
        }

        currentRoomId = roomId
        currentUserId = userId

        // 创建房间实例
        rtcRoom = rtcVideo?.createRTCRoom(roomId)
        rtcRoom?.setRTCRoomEventHandler(rtcRoomEventHandler)

        // 设置用户信息
        val userInfo = UserInfo(userId, null)

        // 设置房间配置
        val roomConfig = RTCRoomConfig(
            ChannelProfile.CHANNEL_PROFILE_CHAT_ROOM, // 使用聊天室模式，适合1v1
            true, // 自动发布
            true, // 自动订阅音频
            true  // 自动订阅视频
        )

        // 加入房间
        rtcRoom?.joinRoom(token, userInfo, roomConfig)

        // 开启本地视频采集
        rtcVideo?.startVideoCapture()
        // 开启本地音频采集
        rtcVideo?.startAudioCapture()

        Log.d(TAG, "Joining room: $roomId as user: $userId")
    }

    /**
     * 离开房间
     */
    fun leaveRoom() {
        rtcRoom?.leaveRoom()
        rtcRoom?.destroy()
        rtcRoom = null

        rtcVideo?.stopVideoCapture()
        rtcVideo?.stopAudioCapture()

        currentRoomId = null
        currentUserId = null

        Log.d(TAG, "Left room")
    }

    /**
     * 设置本地视频渲染视图
     */
    fun setLocalVideoView(videoView: android.view.View) {
        val canvas = VideoCanvas().apply {
            renderView = videoView
            renderMode = VideoCanvas.RENDER_MODE_HIDDEN
        }
        rtcVideo?.setLocalVideoCanvas(StreamIndex.STREAM_INDEX_MAIN, canvas)
    }

    /**
     * 设置远端视频渲染视图
     */
    fun setRemoteVideoView(userId: String, videoView: android.view.View) {
        val remoteStreamKey = RemoteStreamKey(currentRoomId, userId, StreamIndex.STREAM_INDEX_MAIN)
        val canvas = VideoCanvas().apply {
            renderView = videoView
            renderMode = VideoCanvas.RENDER_MODE_HIDDEN
        }
        rtcVideo?.setRemoteVideoCanvas(remoteStreamKey, canvas)
    }

    /**
     * 切换摄像头
     */
    fun switchCamera() {
        rtcVideo?.switchCamera(CameraId.CAMERA_ID_FRONT)
    }

    /**
     * 静音/取消静音
     */
    fun muteLocalAudio(mute: Boolean) {
        if (mute) {
            rtcVideo?.stopAudioCapture()
        } else {
            rtcVideo?.startAudioCapture()
        }
    }

    /**
     * 关闭/开启本地视频
     */
    fun muteLocalVideo(mute: Boolean) {
        if (mute) {
            rtcVideo?.stopVideoCapture()
        } else {
            rtcVideo?.startVideoCapture()
        }
    }

    /**
     * 销毁引擎
     */
    fun destroyEngine() {
        leaveRoom()
        RTCVideo.destroyRTCVideo()
        rtcVideo = null
        Log.d(TAG, "RTC Engine destroyed")
    }

    // ========== RTCVideo 事件回调 ==========
    private val rtcVideoEventHandler = object : IRTCVideoEventHandler() {
        override fun onWarning(warn: Int) {
            Log.w(TAG, "onWarning: $warn")
        }

        override fun onError(err: Int) {
            Log.e(TAG, "onError: $err")
        }
    }

    // ========== RTCRoom 事件回调 ==========
    private val rtcRoomEventHandler = object : IRTCRoomEventHandler() {

        override fun onRoomStateChanged(
            roomId: String,
            uid: String,
            state: Int,
            extraInfo: String
        ) {
            Log.d(TAG, "onRoomStateChanged: roomId=$roomId, uid=$uid, state=$state")
            if (state == 0) { // 加入房间成功
                onLocalJoinRoom?.invoke(true)
            }
        }

        override fun onUserJoined(userInfo: UserInfo, elapsed: Int) {
            Log.d(TAG, "onUserJoined: ${userInfo.uid}")
            // 注意：不要在这里设置远端视图，应该在 onUserPublishStream 中设置
        }

        override fun onUserLeave(uid: String, reason: Int) {
            Log.d(TAG, "onUserLeave: $uid, reason=$reason")
            onRemoteUserLeft?.invoke(uid)
        }

        override fun onUserPublishStream(uid: String, type: MediaStreamType) {
            Log.d(TAG, "onUserPublishStream: $uid, type=$type")
            // 重要：当用户开始发布流时，设置远端视图
            onRemoteUserJoined?.invoke(uid)
        }

        override fun onUserUnpublishStream(uid: String, type: MediaStreamType, reason: StreamRemoveReason) {
            Log.d(TAG, "onUserUnpublishStream: $uid, type=$type, reason=$reason")
            // 用户停止发布流时，清除远端视图
            onRemoteUserLeft?.invoke(uid)
        }

        override fun onRoomStats(stats: RTCRoomStats) {
            // 房间统计信息
        }
    }
}
