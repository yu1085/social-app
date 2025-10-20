package com.example.myapplication.network

import com.example.myapplication.auth.AuthManager
import com.example.myapplication.dto.ApiResponse
import com.example.myapplication.dto.WalletDTO
import com.example.myapplication.dto.UserPhotoDTO
import com.example.myapplication.dto.UploadPhotoResponse
import com.example.myapplication.dto.AlipayOrderResponse
import com.example.myapplication.dto.CreateOrderRequest
import com.example.myapplication.dto.PaymentOrderDTO
import com.example.myapplication.dto.UserSettingsDTO
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object NetworkService {
    
    suspend fun getWalletBalance(token: String): Response<ApiResponse<WalletDTO>> {
        return withContext(Dispatchers.IO) {
            try {
                val call = NetworkConfig.getApiService().getWalletBalance("Bearer $token")
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    // 用户设置相关方法

    /**
     * 获取用户设置
     */
    suspend fun getUserSettings(token: String): Response<ApiResponse<UserSettingsDTO>> {
        return withContext(Dispatchers.IO) {
            try {
                val call = NetworkConfig.getApiService().getUserSettings("Bearer $token")
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * 更新用户设置
     */
    suspend fun updateUserSettings(token: String, settings: UserSettingsDTO): Response<ApiResponse<UserSettingsDTO>> {
        return withContext(Dispatchers.IO) {
            try {
                val call = NetworkConfig.getApiService().updateUserSettings("Bearer $token", settings)
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    
    // 支付相关方法
    
    /**
     * 创建支付宝订单
     */
    suspend fun createAlipayOrder(token: String, request: CreateOrderRequest): Response<ApiResponse<AlipayOrderResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val call = NetworkConfig.getApiService().createAlipayOrder("Bearer $token", request)
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    
    /**
     * 获取订单列表
     */
    suspend fun getOrderList(token: String, status: String? = null): Response<ApiResponse<List<PaymentOrderDTO>>> {
        return withContext(Dispatchers.IO) {
            try {
                val call = NetworkConfig.getApiService().getOrderList("Bearer $token", status)
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    
    /**
     * 获取订单详情
     */
    suspend fun getOrderDetail(token: String, orderId: String): Response<ApiResponse<PaymentOrderDTO>> {
        return withContext(Dispatchers.IO) {
            try {
                val call = NetworkConfig.getApiService().getOrderDetail("Bearer $token", orderId)
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    
    /**
     * 取消订单
     */
    suspend fun cancelOrder(token: String, orderId: String): Response<ApiResponse<String>> {
        return withContext(Dispatchers.IO) {
            try {
                val call = NetworkConfig.getApiService().cancelOrder("Bearer $token", orderId)
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    
    /**
     * 获取支付统计
     */
    suspend fun getPaymentStatistics(token: String): Response<ApiResponse<Map<String, Any>>> {
        return withContext(Dispatchers.IO) {
            try {
                val call = NetworkConfig.getApiService().getPaymentStatistics("Bearer $token")
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    
    // 相册相关方法
    
    /**
     * 获取用户相册照片列表
     */
    suspend fun getUserPhotos(userId: Long, context: android.content.Context): Response<ApiResponse<List<UserPhotoDTO>>> {
        return withContext(Dispatchers.IO) {
            try {
                val authManager = AuthManager.getInstance(context)
                val authHeader = "Bearer ${authManager.getToken()}"
                val call = NetworkConfig.getApiService().getUserPhotos(userId, authHeader)
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    
    /**
     * 上传照片到相册
     */
    suspend fun uploadPhoto(userId: Long, photoFile: File, context: android.content.Context, isAvatar: Boolean = false): Response<ApiResponse<UploadPhotoResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val authManager = AuthManager.getInstance(context)
                val authHeader = "Bearer ${authManager.getToken()}"
                val requestFile = photoFile.asRequestBody("image/*".toMediaType())
                val photoPart = MultipartBody.Part.createFormData("photo", photoFile.name, requestFile)
                val call = NetworkConfig.getApiService().uploadPhoto(userId, photoPart, isAvatar, authHeader)
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    
    /**
     * 删除照片
     */
    suspend fun deletePhoto(userId: Long, photoId: Long, context: android.content.Context): Response<ApiResponse<String>> {
        return withContext(Dispatchers.IO) {
            try {
                val authManager = AuthManager.getInstance(context)
                val authHeader = "Bearer ${authManager.getToken()}"
                val call = NetworkConfig.getApiService().deletePhoto(userId, photoId, authHeader)
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    
    /**
     * 设置照片为头像
     */
    suspend fun setAsAvatar(userId: Long, photoId: Long, context: android.content.Context): Response<ApiResponse<String>> {
        return withContext(Dispatchers.IO) {
            try {
                val authManager = AuthManager.getInstance(context)
                val authHeader = "Bearer ${authManager.getToken()}"
                val call = NetworkConfig.getApiService().setAsAvatar(userId, photoId, authHeader)
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    
    // 身份证二要素核验相关方法
    
    /**
     * 提交身份证二要素核验
     */
    suspend fun submitIdCardVerification(certName: String, certNo: String, context: android.content.Context): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            try {
                val authManager = AuthManager.getInstance(context)
                val authHeader = "Bearer ${authManager.getToken()}"
                val request = mapOf(
                    "certName" to certName,
                    "certNo" to certNo
                )
                val call = NetworkConfig.getApiService().submitIdCardVerification(authHeader, request)
                val response = call.execute()
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        mapOf(
                            "success" to true,
                            "message" to apiResponse.message,
                            "verificationResult" to apiResponse.data
                        )
                    } else {
                        mapOf(
                            "success" to false,
                            "message" to (apiResponse?.message ?: "认证失败")
                        )
                    }
                } else {
                    mapOf(
                        "success" to false,
                        "message" to "网络请求失败: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                mapOf(
                    "success" to false,
                    "message" to "网络错误: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 获取实名认证状态
     */
    suspend fun getVerificationStatus(context: android.content.Context): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            try {
                val authManager = AuthManager.getInstance(context)
                val authHeader = "Bearer ${authManager.getToken()}"
                val call = NetworkConfig.getApiService().getVerificationStatus(authHeader)
                val response = call.execute()
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        mapOf(
                            "success" to true,
                            "data" to apiResponse.data
                        )
                    } else {
                        mapOf(
                            "success" to false,
                            "message" to (apiResponse?.message ?: "查询失败")
                        )
                    }
                } else {
                    mapOf(
                        "success" to false,
                        "message" to "网络请求失败: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                mapOf(
                    "success" to false,
                    "message" to "网络错误: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 查询认证结果
     */
    suspend fun getVerificationResult(context: android.content.Context): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            try {
                val authManager = AuthManager.getInstance(context)
                val authHeader = "Bearer ${authManager.getToken()}"
                val call = NetworkConfig.getApiService().getVerificationResult(authHeader)
                val response = call.execute()
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        mapOf(
                            "success" to true,
                            "data" to apiResponse.data
                        )
                    } else {
                        mapOf(
                            "success" to false,
                            "message" to (apiResponse?.message ?: "查询失败")
                        )
                    }
                } else {
                    mapOf(
                        "success" to false,
                        "message" to "网络请求失败: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                mapOf(
                    "success" to false,
                    "message" to "网络错误: ${e.message}"
                )
            }
        }
    }
}
