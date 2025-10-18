package com.example.myapplication.rtc

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.ss.bytertc.engine.RTCRoom
import com.ss.bytertc.engine.RTCRoomConfig
import com.ss.bytertc.engine.RTCVideo
import com.ss.bytertc.engine.UserInfo
import com.ss.bytertc.engine.VideoCanvas
import com.ss.bytertc.engine.data.CameraId
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
            Log.d(TAG, "Starting RTC Engine initialization with AppID: $appId")
            
            // 检查AppID是否有效
            if (appId.isBlank() || appId == "your_app_id_here") {
                Log.e(TAG, "Invalid AppID: $appId")
                throw IllegalArgumentException("Invalid AppID: $appId")
            }

            // 检查权限
            if (!hasRequiredPermissions(context)) {
                Log.e(TAG, "Missing required permissions for RTC")
                throw SecurityException("Missing required permissions for RTC")
            }

            // 设置日志级别（调试模式）
            try {
                // 尝试设置日志级别，如果方法不存在则忽略
                val setLogLevelMethod = RTCVideo::class.java.getDeclaredMethod("setLogLevel", Int::class.java)
                setLogLevelMethod.invoke(null, 2) // 2 = Debug level
            } catch (e: Exception) {
                Log.w(TAG, "Could not set log level: ${e.message}")
            }
            
            // 创建 RTCVideo 实例
            rtcVideo = RTCVideo.createRTCVideo(
                context.applicationContext,
                appId,
                rtcVideoEventHandler,
                null,
                null
            )

            if (rtcVideo == null) {
                Log.e(TAG, "Failed to create RTCVideo instance")
                throw RuntimeException("Failed to create RTCVideo instance")
            }

            // 设置视频采集参数
            try {
                val captureConfig = VideoCaptureConfig().apply {
                    width = 640
                    height = 480
                    frameRate = 15
                }
                rtcVideo?.setVideoCaptureConfig(captureConfig)
            } catch (e: Exception) {
                Log.w(TAG, "Could not set video capture config: ${e.message}")
            }

            Log.d(TAG, "RTC Engine initialized successfully")
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Native library loading failed", e)
            Log.e(TAG, "Please check if libvolcenginertc.so is properly included in the APK")
            throw e
        } catch (e: NoClassDefFoundError) {
            Log.e(TAG, "Missing class definition", e)
            Log.e(TAG, "Please check if all required dependencies are included")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize RTC Engine", e)
            throw e
        }
    }

    /**
     * 检查是否有必要的权限
     */
    private fun hasRequiredPermissions(context: Context): Boolean {
        val requiredPermissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_CONNECT
        )
        
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
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
            Log.w(TAG, "RTC Warning: $warn")
        }

        override fun onError(err: Int) {
            Log.e(TAG, "RTC Error: $err")
            when (err) {
                -1 -> Log.e(TAG, "RTC Error: Invalid parameter")
                -2 -> Log.e(TAG, "RTC Error: Invalid state")
                -3 -> Log.e(TAG, "RTC Error: No permission")
                -4 -> Log.e(TAG, "RTC Error: Network error")
                -5 -> Log.e(TAG, "RTC Error: Token expired")
                -6 -> Log.e(TAG, "RTC Error: Token invalid")
                -7 -> Log.e(TAG, "RTC Error: App ID invalid")
                -8 -> Log.e(TAG, "RTC Error: Room ID invalid")
                -9 -> Log.e(TAG, "RTC Error: User ID invalid")
                -10 -> Log.e(TAG, "RTC Error: Token expired")
                else -> Log.e(TAG, "RTC Error: Unknown error $err")
            }
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
