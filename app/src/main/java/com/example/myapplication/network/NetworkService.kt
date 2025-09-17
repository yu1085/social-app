package com.example.myapplication.network

import com.example.myapplication.dto.ApiResponse
import com.example.myapplication.dto.WalletDTO
import com.example.myapplication.model.UserCard
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    
    suspend fun rechargeWallet(token: String, amount: java.math.BigDecimal, paymentMethod: String): Response<ApiResponse<WalletDTO>> {
        return withContext(Dispatchers.IO) {
            try {
                val request = com.example.myapplication.network.ApiService.RechargeRequest(amount, paymentMethod)
                val call = NetworkConfig.getApiService().rechargeWallet("Bearer $token", request)
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    
    /**
     * 获取首页用户卡片列表
     */
    suspend fun getHomeUserCards(page: Int = 0, size: Int = 10): Response<ApiResponse<List<UserCard>>> {
        return withContext(Dispatchers.IO) {
            try {
                val call = NetworkConfig.getApiService().getHomeUserCards(page, size)
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    
    /**
     * 获取用户详情
     */
    suspend fun getUserDetail(userId: Long): Response<ApiResponse<UserCard>> {
        return withContext(Dispatchers.IO) {
            try {
                val call = NetworkConfig.getApiService().getUserDetail(userId)
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
    
    /**
     * 搜索用户
     */
    suspend fun searchUsers(keyword: String? = null, location: String? = null, gender: String? = null, page: Int = 0, size: Int = 20): Response<ApiResponse<List<UserCard>>> {
        return withContext(Dispatchers.IO) {
            try {
                val call = NetworkConfig.getApiService().searchUsers(keyword, location, gender, page, size)
                call.execute()
            } catch (e: Exception) {
                throw e
            }
        }
    }
}
