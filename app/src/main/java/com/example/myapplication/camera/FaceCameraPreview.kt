package com.example.myapplication.camera

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
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
    
    // 相机执行器
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    // 清理资源
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
    
    Box(modifier = modifier) {
        // 相机预览
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                startCamera(previewView, context, lifecycleOwner, cameraExecutor) { imageProxy ->
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
        
        // 提示信息
        if (!faceDetected) {
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
    onImageAvailable: (ImageProxy) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        
        val preview = Preview.Builder().build()
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, FaceAnalyzer(onImageAvailable))
            }
        
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
            preview.setSurfaceProvider(previewView.surfaceProvider)
        } catch (e: Exception) {
            Log.e("CameraX", "相机启动失败", e)
        }
    }, ContextCompat.getMainExecutor(context))
}

/**
 * 人脸检测分析器
 */
private class FaceAnalyzer(
    private val onImageAvailable: (ImageProxy) -> Unit
) : ImageAnalysis.Analyzer {
    
    override fun analyze(imageProxy: ImageProxy) {
        onImageAvailable(imageProxy)
        imageProxy.close()
    }
}

/**
 * 模拟人脸检测
 */
private fun simulateFaceDetection(): Boolean {
    // 模拟90%概率检测到人脸
    return Math.random() > 0.1
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