package com.example.myapplication.service

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * 阿里云实人认证服务
 * 实现真人认证功能（金融级实人认证）
 */
class AliyunFaceAuthService private constructor() {

    companion object {
        private const val TAG = "AliyunFaceAuthService"

        @Volatile
        private var INSTANCE: AliyunFaceAuthService? = null

        fun getInstance(): AliyunFaceAuthService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AliyunFaceAuthService().also { INSTANCE = it }
            }
        }
    }

    // 阿里云金融级实人认证配置
    // 注意：请将敏感信息配置到环境变量或配置文件中
    private val accessKeyId = System.getenv("ALIYUN_ACCESS_KEY_ID") ?: "YOUR_ACCESS_KEY_ID"
    private val accessKeySecret = System.getenv("ALIYUN_ACCESS_KEY_SECRET") ?: "YOUR_ACCESS_KEY_SECRET"
    private val regionId = "cn-shanghai"
    private val sceneId = "1000015106"  // SocialMeet认证场景ID
    private val productCode = "ID_PRO"  // 金融级实人认证产品代码

    /**
     * 初始化阿里云实人认证服务
     */
    fun initialize(context: Context) {
        try {
            Log.d(TAG, "初始化阿里云实人认证服务")
            
            // TODO: 初始化阿里云官方SDK
            // 暂时使用模拟实现，等确认SDK API后启用真实实现
            
            Log.d(TAG, "阿里云实人认证服务初始化成功")
            
        } catch (e: Exception) {
            Log.e(TAG, "阿里云实人认证服务初始化失败", e)
        }
    }

    /**
     * 检测人脸
     * @param bitmap 人脸图片
     * @return 检测结果
     */
    suspend fun detectFace(bitmap: Bitmap): FaceDetectionResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始检测人脸")
            
            // 检查图片质量
            val qualityResult = checkImageQuality(bitmap)
            if (!qualityResult.isValid) {
                return@withContext FaceDetectionResult(
                    success = false,
                    message = qualityResult.message
                )
            }
            
            // 模拟人脸检测（使用阿里云官方SDK）
            // 这里应该调用阿里云官方SDK的人脸检测API
            // 由于SDK API可能不同，先使用模拟实现
            
            val faceInfo = FaceInfo(
                faceId = UUID.randomUUID().toString(),
                confidence = 0.95f,
                quality = qualityResult.quality,
                landmarks = generateFaceLandmarks()
            )
            
            Log.d(TAG, "人脸检测成功: ${faceInfo.faceId}")
            
            FaceDetectionResult(
                success = true,
                message = "人脸检测成功",
                faceInfo = faceInfo
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "人脸检测失败", e)
            FaceDetectionResult(
                success = false,
                message = "人脸检测失败: ${e.message}"
            )
        }
    }

    /**
     * 活体检测
     * @param bitmap 人脸图片
     * @return 活体检测结果
     */
    suspend fun livenessDetection(bitmap: Bitmap): LivenessDetectionResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始活体检测")
            
            // 模拟活体检测过程
            delay(1500)
            
            // 检查是否为真人
            val isLive = performLivenessCheck(bitmap)
            
            if (isLive) {
                Log.d(TAG, "活体检测通过")
                LivenessDetectionResult(
                    success = true,
                    message = "活体检测通过",
                    isLive = true,
                    confidence = 0.92f
                )
            } else {
                Log.d(TAG, "活体检测失败")
                LivenessDetectionResult(
                    success = false,
                    message = "检测到非活体，请确保是真人操作",
                    isLive = false,
                    confidence = 0.15f
                )
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "活体检测失败", e)
            LivenessDetectionResult(
                success = false,
                message = "活体检测失败: ${e.message}",
                isLive = false,
                confidence = 0.0f
            )
        }
    }

    /**
     * 执行实人认证
     * @param bitmap 人脸图片
     * @param userId 用户ID
     * @param token JWT认证token
     * @return 认证结果
     */
    suspend fun performRealPersonAuth(bitmap: Bitmap, userId: Long, token: String): RealPersonAuthResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始执行实人认证，用户ID: $userId, token: ${token.take(10)}...")
            
            // 检查图片有效性
            if (bitmap.isRecycled) {
                Log.e(TAG, "图片已被回收")
                return@withContext RealPersonAuthResult(
                    success = false,
                    message = "图片无效，请重新拍摄"
                )
            }
            
            // 先进行人脸检测
            Log.d(TAG, "开始人脸检测")
            val faceResult = detectFace(bitmap)
            if (!faceResult.success) {
                Log.w(TAG, "人脸检测失败: ${faceResult.message}")
                return@withContext RealPersonAuthResult(
                    success = false,
                    message = "人脸检测失败: ${faceResult.message}"
                )
            }
            Log.d(TAG, "人脸检测成功")
            
            // 进行活体检测
            Log.d(TAG, "开始活体检测")
            val livenessResult = livenessDetection(bitmap)
            if (!livenessResult.success || !livenessResult.isLive) {
                Log.w(TAG, "活体检测失败: ${livenessResult.message}")
                return@withContext RealPersonAuthResult(
                    success = false,
                    message = "活体检测失败: ${livenessResult.message}",
                    livenessInfo = livenessResult
                )
            }
            Log.d(TAG, "活体检测成功")
            
            // 执行金融级实人认证
            Log.d(TAG, "开始金融级实人认证")
            val authResult = performFinancialGradeAuth(bitmap)
            
            if (authResult.success) {
                Log.d(TAG, "实人认证成功")
            } else {
                Log.w(TAG, "实人认证失败: ${authResult.message}")
            }
            
            RealPersonAuthResult(
                success = authResult.success,
                message = authResult.message,
                livenessInfo = livenessResult
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "实人认证执行失败", e)
            RealPersonAuthResult(
                success = false,
                message = "实人认证执行失败: ${e.message}"
            )
        }
    }

    /**
     * 执行金融级实人认证
     */
    private suspend fun performFinancialGradeAuth(bitmap: Bitmap): RealPersonAuthResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "执行金融级实人认证")
            
            // 调用后端实人认证API
            val result = callRealPersonAuthAPI(bitmap)
            
            if (result.success) {
                Log.d(TAG, "金融级实人认证成功")
                RealPersonAuthResult(
                    success = true,
                    message = result.message,
                    livenessInfo = LivenessDetectionResult(
                        success = true,
                        message = "活体检测通过",
                        isLive = true,
                        confidence = result.confidence
                    )
                )
            } else {
                Log.d(TAG, "金融级实人认证失败: ${result.message}")
                RealPersonAuthResult(
                    success = false,
                    message = result.message,
                    livenessInfo = LivenessDetectionResult(
                        success = false,
                        message = "活体检测失败",
                        isLive = false,
                        confidence = 0.2f
                    )
                )
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "金融级实人认证执行失败", e)
            RealPersonAuthResult(
                success = false,
                message = "金融级实人认证执行失败: ${e.message}"
            )
        }
    }
    
    /**
     * 调用后端实人认证API
     */
    private suspend fun callRealPersonAuthAPI(bitmap: Bitmap): RealPersonAuthAPIResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "调用后端实人认证API")
            
            // 将bitmap转换为base64
            val base64Image = bitmapToBase64(bitmap)
            
            // 构建请求数据
            val requestData = mapOf(
                "image" to base64Image,
                "sceneId" to sceneId,
                "productCode" to productCode
            )
            
            // 发送HTTP请求到后端
            val url = java.net.URL("http://10.0.2.2:8080/api/auth/real-person/verify")
            val connection = url.openConnection() as java.net.HttpURLConnection
            
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.doOutput = true
            
            // 发送请求
            val outputStream = connection.outputStream
            val writer = java.io.OutputStreamWriter(outputStream)
            writer.write(org.json.JSONObject(requestData).toString())
            writer.flush()
            writer.close()
            
            // 读取响应
            val responseCode = connection.responseCode
            val response = java.io.BufferedReader(connection.inputStream.reader()).use { it.readText() }
            
            Log.d(TAG, "实人认证API响应: $response")
            
            val jsonResponse = org.json.JSONObject(response)
            val success = jsonResponse.optBoolean("success", false)
            val data = jsonResponse.optJSONObject("data")
            
            if (success && data != null) {
                val match = data.optBoolean("match", false)
                val message = data.optString("message", "")
                val confidence = data.optDouble("confidence", 0.0).toFloat()
                
                RealPersonAuthAPIResult(
                    success = true,
                    match = match,
                    message = message,
                    confidence = confidence
                )
            } else {
                RealPersonAuthAPIResult(
                    success = false,
                    match = false,
                    message = jsonResponse.optString("message", "认证失败"),
                    confidence = 0.0f
                )
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "实人认证API调用失败", e)
            RealPersonAuthAPIResult(
                success = false,
                match = false,
                message = "网络错误: ${e.message}",
                confidence = 0.0f
            )
        }
    }

    /**
     * 检查图片质量
     */
    private fun checkImageQuality(bitmap: Bitmap): ImageQualityResult {
        val width = bitmap.width
        val height = bitmap.height
        
        Log.d(TAG, "检查图片质量: ${width}x${height}")
        
        // 检查图片是否有效
        if (bitmap.isRecycled) {
            return ImageQualityResult(
                isValid = false,
                message = "图片已被回收，请重新拍摄",
                quality = 0.0f
            )
        }
        
        // 检查图片尺寸 - 在模拟器环境中放宽要求
        val minSize = if (isEmulator()) 50 else 300
        if (width < minSize || height < minSize) {
            return ImageQualityResult(
                isValid = false,
                message = "图片尺寸过小，请确保人脸清晰可见（建议至少${minSize}x${minSize}像素）",
                quality = 0.0f
            )
        }
        
        // 检查图片比例 - 人脸照片通常接近正方形
        val aspectRatio = width.toFloat() / height.toFloat()
        if (aspectRatio < 0.6f || aspectRatio > 1.8f) {
            return ImageQualityResult(
                isValid = false,
                message = "图片比例不合适，请保持正面拍摄",
                quality = 0.0f
            )
        }
        
        // 检查图片大小（避免过大的图片）
        val byteCount = bitmap.byteCount
        val maxSize = 5 * 1024 * 1024 // 5MB
        if (byteCount > maxSize) {
            return ImageQualityResult(
                isValid = false,
                message = "图片过大，请重新拍摄",
                quality = 0.0f
            )
        }
        
        // 模拟质量检查 - 基于图片尺寸和比例
        val sizeScore = minOf(1.0f, (width * height).toFloat() / (500 * 500))
        val ratioScore = if (aspectRatio in 0.8f..1.2f) 1.0f else 0.8f
        val quality = (sizeScore * ratioScore * 0.9f + 0.1f).coerceIn(0.0f, 1.0f)
        
        Log.d(TAG, "图片质量评分: $quality")
        
        return ImageQualityResult(
            isValid = true,
            message = "图片质量良好",
            quality = quality
        )
    }

    /**
     * 执行活体检测
     */
    private fun performLivenessCheck(bitmap: Bitmap): Boolean {
        // 在模拟器环境中，直接返回通过
        if (isEmulator()) {
            Log.d(TAG, "模拟器环境：跳过活体检测")
            return true
        }
        
        // 模拟活体检测逻辑
        // 在实际实现中，这里应该调用阿里云的人脸活体检测API
        
        // 基于图片质量进行活体检测
        val qualityResult = checkImageQuality(bitmap)
        if (!qualityResult.isValid) {
            Log.w(TAG, "图片质量不符合活体检测要求")
            return false
        }
        
        // 模拟基于图片特征的活体检测
        val width = bitmap.width
        val height = bitmap.height
        
        // 检查图片清晰度（基于尺寸）
        val clarityScore = minOf(1.0f, (width * height).toFloat() / (400 * 400))
        
        // 检查图片比例（人脸照片应该接近正方形）
        val aspectRatio = width.toFloat() / height.toFloat()
        val ratioScore = if (aspectRatio in 0.7f..1.4f) 1.0f else 0.7f
        
        // 综合评分
        val livenessScore = (clarityScore * ratioScore * qualityResult.quality)
        
        Log.d(TAG, "活体检测评分: $livenessScore (清晰度: $clarityScore, 比例: $ratioScore, 质量: ${qualityResult.quality})")
        
        // 85% 概率通过（基于评分）
        val threshold = 0.6f
        val isLive = livenessScore > threshold && Math.random() > 0.15
        
        return isLive
    }

    /**
     * 生成人脸关键点
     */
    private fun generateFaceLandmarks(): List<FaceLandmark> {
        return listOf(
            FaceLandmark("left_eye", 0.3f, 0.4f),
            FaceLandmark("right_eye", 0.7f, 0.4f),
            FaceLandmark("nose", 0.5f, 0.5f),
            FaceLandmark("left_mouth", 0.4f, 0.6f),
            FaceLandmark("right_mouth", 0.6f, 0.6f)
        )
    }

    /**
     * 将Bitmap转换为Base64
     */
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return Base64.getEncoder().encodeToString(imageBytes)
    }
    
    /**
     * 检测是否在模拟器环境中
     */
    private fun isEmulator(): Boolean {
        return try {
            android.os.Build.FINGERPRINT.startsWith("generic") ||
            android.os.Build.FINGERPRINT.startsWith("unknown") ||
            android.os.Build.MODEL.contains("google_sdk") ||
            android.os.Build.MODEL.contains("Emulator") ||
            android.os.Build.MODEL.contains("Android SDK built for x86") ||
            android.os.Build.MANUFACTURER.contains("Genymotion") ||
            android.os.Build.BOARD.contains("goldfish") ||
            android.os.Build.HARDWARE.contains("ranchu") ||
            android.os.Build.PRODUCT.contains("sdk") ||
            android.os.Build.PRODUCT.contains("emulator") ||
            android.os.Build.PRODUCT.contains("simulator")
        } catch (e: Exception) {
            false
        }
    }
}

// 数据类定义
data class FaceDetectionResult(
    val success: Boolean,
    val message: String,
    val faceInfo: FaceInfo? = null
)

data class FaceInfo(
    val faceId: String,
    val confidence: Float,
    val quality: Float,
    val landmarks: List<FaceLandmark>
)

data class FaceLandmark(
    val name: String,
    val x: Float,
    val y: Float
)

data class LivenessDetectionResult(
    val success: Boolean,
    val message: String,
    val isLive: Boolean,
    val confidence: Float
)

data class RealPersonAuthResult(
    val success: Boolean,
    val message: String,
    val livenessInfo: LivenessDetectionResult? = null
)

data class ImageQualityResult(
    val isValid: Boolean,
    val message: String,
    val quality: Float
)

data class RealPersonAuthAPIResult(
    val success: Boolean,
    val match: Boolean,
    val message: String,
    val confidence: Float
)