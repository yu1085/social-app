package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityVideoChatBinding
import com.example.myapplication.rtc.RTCConfig
import com.example.myapplication.rtc.RTCManager
import com.example.myapplication.rtc.accesstoken.AccessToken
import com.example.myapplication.rtc.accesstoken.Utils

/**
 * 1v1 视频聊天界面
 * 使用火山引擎 RTC SDK 实现
 */
class VideoChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoChatBinding
    private val rtcManager = RTCManager.instance

    // 配置信息 - 从火山引擎控制台获取
    private val APP_ID = RTCConfig.APP_ID
    private val APP_KEY = RTCConfig.APP_KEY
    private val ROOM_ID = RTCConfig.DEFAULT_ROOM_ID
    // 使用设备唯一ID生成用户ID，确保每个设备ID不同（懒加载，避免在初始化时访问contentResolver）
    private val USER_ID by lazy {
        "User_${android.provider.Settings.Secure.getString(
            contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        ).takeLast(6)}"
    }

    // 动态生成 Token（懒加载，使用USER_ID）
    private val TOKEN by lazy {
        generateToken(ROOM_ID, USER_ID)
    }

    // 权限请求
    private val PERMISSION_REQUEST_CODE = 1001
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    // 通话计时
    private var callDurationSeconds = 0
    private val durationHandler = Handler(Looper.getMainLooper())
    private val durationRunnable = object : Runnable {
        override fun run() {
            callDurationSeconds++
            val minutes = callDurationSeconds / 60
            val seconds = callDurationSeconds % 60
            binding.durationText.text = String.format("%02d:%02d", minutes, seconds)
            durationHandler.postDelayed(this, 1000)
        }
    }

    // 按钮状态
    private var isAudioMuted = false
    private var isVideoMuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 检查权限
        if (checkPermissions()) {
            initializeVideoChat()
        } else {
            requestPermissions()
        }

        setupControls()
        setupRTCCallbacks()
    }

    /**
     * 初始化视频聊天
     */
    private fun initializeVideoChat() {
        try {
            Log.d("VideoChatActivity", "开始初始化视频聊天")
            Log.d("VideoChatActivity", "AppID: $APP_ID")
            Log.d("VideoChatActivity", "RoomID: $ROOM_ID")
            Log.d("VideoChatActivity", "UserID: $USER_ID")
            
            // 检查AppID是否有效
            if (APP_ID.isBlank() || APP_ID == "your_app_id_here") {
                Log.e("VideoChatActivity", "无效的AppID，请检查RTCConfig配置")
                Toast.makeText(this, "RTC配置错误，请检查AppID", Toast.LENGTH_LONG).show()
                return
            }
            
            // 初始化 RTC 引擎
            rtcManager.initEngine(this, APP_ID)

            // 创建本地视频 TextureView 并设置
            val localTextureView = rtcManager.createTextureView(this)
            binding.localVideoView.addView(localTextureView)
            rtcManager.setLocalVideoView(localTextureView)

            // 延迟加入房间，确保引擎完全初始化
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    rtcManager.joinRoom(ROOM_ID, USER_ID, TOKEN)
                } catch (e: Exception) {
                    Log.e("VideoChatActivity", "加入房间失败", e)
                    Toast.makeText(this, "加入房间失败: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }, 1000)

            // 更新房间信息
            binding.roomInfoText.text = "房间: $ROOM_ID\n用户: $USER_ID"

            // 显示等待状态
            binding.statusText.visibility = View.VISIBLE
            binding.statusText.text = "等待对方加入..."
            
            Log.d("VideoChatActivity", "视频聊天初始化完成")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("VideoChatActivity", "RTC SDK库加载失败", e)
            Toast.makeText(this, "RTC SDK库加载失败，请检查APK是否包含必要的so库", Toast.LENGTH_LONG).show()
        } catch (e: NoClassDefFoundError) {
            Log.e("VideoChatActivity", "缺少必要的类定义", e)
            Toast.makeText(this, "缺少必要的类定义: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("VideoChatActivity", "初始化视频聊天失败", e)
            Toast.makeText(this, "初始化视频聊天失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 设置控制按钮
     */
    private fun setupControls() {
        // 静音/取消静音
        binding.muteAudioButton.setOnClickListener {
            isAudioMuted = !isAudioMuted
            rtcManager.muteLocalAudio(isAudioMuted)

            if (isAudioMuted) {
                binding.muteAudioButton.setImageResource(android.R.drawable.ic_lock_silent_mode)
                Toast.makeText(this, "已静音", Toast.LENGTH_SHORT).show()
            } else {
                binding.muteAudioButton.setImageResource(android.R.drawable.ic_btn_speak_now)
                Toast.makeText(this, "已取消静音", Toast.LENGTH_SHORT).show()
            }
        }

        // 挂断
        binding.hangUpButton.setOnClickListener {
            finish()
        }

        // 切换摄像头
        binding.switchCameraButton.setOnClickListener {
            rtcManager.switchCamera()
            Toast.makeText(this, "切换摄像头", Toast.LENGTH_SHORT).show()
        }

        // 开启/关闭视频
        binding.muteVideoButton.setOnClickListener {
            isVideoMuted = !isVideoMuted
            rtcManager.muteLocalVideo(isVideoMuted)

            if (isVideoMuted) {
                binding.muteVideoButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                binding.localVideoView.visibility = View.INVISIBLE
                Toast.makeText(this, "已关闭视频", Toast.LENGTH_SHORT).show()
            } else {
                binding.muteVideoButton.setImageResource(android.R.drawable.ic_menu_camera)
                binding.localVideoView.visibility = View.VISIBLE
                Toast.makeText(this, "已开启视频", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 设置 RTC 回调
     */
    private fun setupRTCCallbacks() {
        // 本地加入房间成功
        rtcManager.onLocalJoinRoom = { isSuccess ->
            runOnUiThread {
                if (isSuccess) {
                    Toast.makeText(this, "成功加入房间", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "加入房间失败", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 远端用户加入
        rtcManager.onRemoteUserJoined = { userId ->
            runOnUiThread {
                binding.statusText.visibility = View.GONE
                Toast.makeText(this, "用户 $userId 加入通话", Toast.LENGTH_SHORT).show()

                // 创建远端视频 TextureView 并设置
                val remoteTextureView = rtcManager.createTextureView(this)
                binding.remoteVideoView.addView(remoteTextureView)
                rtcManager.setRemoteVideoView(userId, remoteTextureView)

                // 开始计时
                startCallDuration()
            }
        }

        // 远端用户离开
        rtcManager.onRemoteUserLeft = { userId ->
            runOnUiThread {
                binding.statusText.visibility = View.VISIBLE
                binding.statusText.text = "对方已离开"
                Toast.makeText(this, "用户 $userId 离开通话", Toast.LENGTH_SHORT).show()

                // 停止计时
                stopCallDuration()
                
                // 延迟2秒后自动退出界面
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 2000)
            }
        }
    }

    /**
     * 开始通话计时
     */
    private fun startCallDuration() {
        callDurationSeconds = 0
        durationHandler.post(durationRunnable)
    }

    /**
     * 停止通话计时
     */
    private fun stopCallDuration() {
        durationHandler.removeCallbacks(durationRunnable)
    }

    /**
     * 检查权限
     */
    private fun checkPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 请求权限
     */
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.d("VideoChatActivity", "所有权限已授予，开始初始化视频聊天")
                initializeVideoChat()
            } else {
                Log.e("VideoChatActivity", "权限被拒绝，无法进行视频通话")
                Toast.makeText(this, "需要相机和麦克风权限才能进行视频通话", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCallDuration()
        rtcManager.leaveRoom()
    }

    /**
     * 生成 Token
     * 注意：生产环境应该在服务端生成Token，这里仅用于测试
     */
    private fun generateToken(roomId: String, userId: String): String {
        val token = AccessToken(APP_ID, APP_KEY, roomId, userId)
        // Token 有效期1小时
        token.ExpireTime(Utils.getTimestamp() + 3600)
        // 添加发布流权限
        token.AddPrivilege(AccessToken.Privileges.PrivPublishStream, Utils.getTimestamp() + 3600)
        // 添加订阅流权限
        token.AddPrivilege(AccessToken.Privileges.PrivSubscribeStream, 0)

        val tokenString = token.Serialize()
        android.util.Log.d("VideoChatActivity", "Generated token for user $userId: $tokenString")
        return tokenString
    }
}
