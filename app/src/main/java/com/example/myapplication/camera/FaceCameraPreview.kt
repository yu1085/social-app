package com.example.myapplication.camera

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.myapplication.util.ComposePerformanceOptimizer
import com.example.myapplication.util.EGLPerformanceMonitor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 人脸识别相机预览组件
 */
@Composable
fun FaceCameraPreview(
    onFaceDetected: (Boolean) -> Unit,
    onPhotoTaken: (Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var isCapturing by remember { mutableStateOf(false) }
    var faceDetected by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    var isCameraReady by remember { mutableStateOf(false) }
    
    // 相机执行器
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    // 帧时间记录
    var lastFrameTime by remember { mutableStateOf(0L) }
    
    // 清理资源
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            // 清理帧时间记录
            lastFrameTime = 0L
            // 建议垃圾回收
            System.gc()
        }
    }
    
    Box(modifier = modifier) {
        // 相机预览 - 优化版本
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    // 启用硬件加速
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .then(ComposePerformanceOptimizer.optimizedCanvasModifier()),
            update = { previewView ->
                startCamera(
                    previewView, 
                    context, 
                    lifecycleOwner, 
                    cameraExecutor,
                    onCameraReady = { isCameraReady = true },
                    onCameraError = { error -> cameraError = error }
                ) { imageProxy ->
                    // 记录渲染性能（使用正确的帧间隔计算）
                    val currentTime = System.currentTimeMillis()
                    if (lastFrameTime > 0) {
                        val frameTime = currentTime - lastFrameTime
                        EGLPerformanceMonitor.recordRenderTime(frameTime)
                    }
                    lastFrameTime = currentTime
                    
                    // 模拟人脸检测
                    val hasFace = simulateFaceDetection()
                    faceDetected = hasFace
                    onFaceDetected(hasFace)
                }
            }
        )
        
        // 人脸检测指示器
        if (faceDetected) {
            FaceDetectionIndicator(
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // 拍照按钮
        FloatingActionButton(
            onClick = {
                if (!isCapturing && faceDetected) {
                    isCapturing = true
                    // 模拟拍照
                    simulateTakePhoto { bitmap ->
                        onPhotoTaken(bitmap)
                        isCapturing = false
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            containerColor = if (faceDetected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ) {
            if (isCapturing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "拍照",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        
        // 错误状态显示
        cameraError?.let { error ->
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "相机错误",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // 相机加载状态
        if (!isCameraReady && cameraError == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "正在启动相机...",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        
        // 提示信息
        if (!faceDetected && isCameraReady && cameraError == null) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            ) {
                Text(
                    text = "请将面部对准相机",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * 启动相机
 */
private fun startCamera(
    previewView: PreviewView,
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    cameraExecutor: ExecutorService,
    onCameraReady: () -> Unit = {},
    onCameraError: (String) -> Unit = {},
    onImageAvailable: (ImageProxy) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    
    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()
            
            // 先解绑所有相机
            cameraProvider.unbindAll()
            
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build()
                
            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, FaceAnalyzer(onImageAvailable))
                }
            
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            
            // 绑定相机到生命周期
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
            
            // 设置预览Surface
            preview.setSurfaceProvider(previewView.surfaceProvider)
            
            Log.d("CameraX", "相机启动成功")
            onCameraReady()
            
        } catch (e: Exception) {
            val errorMsg = "相机启动失败: ${e.message}"
            Log.e("CameraX", errorMsg, e)
            onCameraError(errorMsg)
        }
    }, ContextCompat.getMainExecutor(context))
}

/**
 * 人脸检测分析器 - 优化版本
 */
private class FaceAnalyzer(
    private val onImageAvailable: (ImageProxy) -> Unit
) : ImageAnalysis.Analyzer {
    
    private var lastAnalysisTime = 0L
    private val analysisInterval = 200L // 限制分析频率为5fps，进一步减少GPU负载
    
    override fun analyze(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        
        // 限制分析频率，减少GPU负载
        if (currentTime - lastAnalysisTime >= analysisInterval) {
            onImageAvailable(imageProxy)
            lastAnalysisTime = currentTime
        } else {
            // 如果不需要分析，直接关闭
            imageProxy.close()
        }
    }
}

/**
 * 模拟人脸检测
 */
private fun simulateFaceDetection(): Boolean {
    // 模拟人脸检测，增加稳定性
    // 在实际应用中，这里应该使用真实的人脸检测算法
    val random = Math.random()
    
    // 模拟检测稳定性：80%概率检测到人脸
    // 添加一些随机性来模拟真实环境
    return random > 0.2
}

/**
 * 人脸检测指示器
 */
@Composable
private fun FaceDetectionIndicator(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = 100f
        
        // 绘制绿色圆圈表示检测到人脸
        drawCircle(
            color = Color.Green.copy(alpha = 0.3f),
            radius = radius,
            center = Offset(centerX, centerY)
        )
        
        // 绘制边框
        drawCircle(
            color = Color.Green,
            radius = radius,
            center = Offset(centerX, centerY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
        )
    }
}

/**
 * 模拟拍照功能
 */
private fun simulateTakePhoto(
    onPhotoTaken: (Bitmap) -> Unit
) {
    // 模拟拍照延迟
    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        // 创建一个模拟的Bitmap
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        onPhotoTaken(bitmap)
    }, 1000)
}