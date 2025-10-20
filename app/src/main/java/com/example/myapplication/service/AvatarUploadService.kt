package com.example.myapplication.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * 头像上传服务
 * 处理图片压缩、旋转和上传
 */
class AvatarUploadService(private val context: Context) {

    companion object {
        private const val TAG = "AvatarUploadService"
        private const val MAX_IMAGE_SIZE = 1024 // 最大宽高
        private const val JPEG_QUALITY = 85 // JPEG压缩质量
    }

    /**
     * 准备上传的图片文件
     * 将URI转换为压缩后的文件
     */
    fun prepareImageFile(imageUri: Uri): File? {
        try {
            Log.d(TAG, "准备图片文件: $imageUri")

            // 读取原始图片
            val inputStream = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                Log.e(TAG, "无法打开图片文件")
                return null
            }

            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap == null) {
                Log.e(TAG, "图片解码失败")
                return null
            }

            Log.d(TAG, "原始图片尺寸: ${bitmap.width}x${bitmap.height}")

            // 旋转图片（根据EXIF信息）
            val rotatedBitmap = rotateImageIfRequired(bitmap, imageUri)

            // 压缩图片
            val compressedBitmap = compressImage(rotatedBitmap)

            // 保存到临时文件
            val tempFile = File(context.cacheDir, "avatar_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(tempFile)
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
            outputStream.flush()
            outputStream.close()

            // 回收Bitmap
            if (bitmap != rotatedBitmap) {
                bitmap.recycle()
            }
            compressedBitmap.recycle()

            Log.d(TAG, "图片文件准备完成: ${tempFile.absolutePath}, 大小: ${tempFile.length() / 1024}KB")
            return tempFile

        } catch (e: Exception) {
            Log.e(TAG, "准备图片文件失败", e)
            return null
        }
    }

    /**
     * 创建Multipart请求体
     */
    fun createMultipartBody(file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    /**
     * 压缩图片
     */
    private fun compressImage(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // 如果图片尺寸小于最大值，直接返回
        if (width <= MAX_IMAGE_SIZE && height <= MAX_IMAGE_SIZE) {
            return bitmap
        }

        // 计算缩放比例
        val scale = if (width > height) {
            MAX_IMAGE_SIZE.toFloat() / width
        } else {
            MAX_IMAGE_SIZE.toFloat() / height
        }

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        Log.d(TAG, "压缩图片: ${width}x${height} -> ${newWidth}x${newHeight}")

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * 根据EXIF信息旋转图片
     */
    private fun rotateImageIfRequired(bitmap: Bitmap, imageUri: Uri): Bitmap {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                return bitmap
            }

            val exif = ExifInterface(inputStream)
            inputStream.close()

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                else -> bitmap
            }
        } catch (e: IOException) {
            Log.e(TAG, "读取EXIF信息失败", e)
            return bitmap
        }
    }

    /**
     * 旋转图片
     */
    private fun rotateImage(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * 清理临时文件
     */
    fun cleanupTempFile(file: File?) {
        try {
            file?.delete()
            Log.d(TAG, "临时文件已清理: ${file?.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "清理临时文件失败", e)
        }
    }
}
