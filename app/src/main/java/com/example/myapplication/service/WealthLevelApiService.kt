package com.example.myapplication.service

import com.example.myapplication.model.WealthLevelData
import com.example.myapplication.model.PrivilegeType
import com.example.myapplication.model.LevelProgressInfo
import com.example.myapplication.model.LevelRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * 财富等级API服务
 */
class WealthLevelApiService {
    
    private val baseUrl = "http://10.0.2.2:8080/api/wealth-level"
    
    /**
     * 获取用户财富等级信息
     */
    suspend fun getMyWealthLevel(token: String): Result<WealthLevelData> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/my-level")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer $token")
            connection.setRequestProperty("Content-Type", "application/json")
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                
                if (jsonResponse.getBoolean("success")) {
                    val data = jsonResponse.getJSONObject("data")
                    val wealthLevel = WealthLevelData(
                        levelName = data.getString("levelName"),
                        levelIcon = data.getString("levelIcon"),
                        levelColor = data.getString("levelColor"),
                        wealthValue = data.getInt("wealthValue"),
                        progressPercentage = data.optDouble("progressPercentage", 0.0),
                        nextLevelName = data.optString("nextLevelName").takeIf { it != "null" },
                        nextLevelRequirement = data.optInt("nextLevelRequirement").takeIf { it != 0 },
                        userId = data.optLong("userId", 0L)
                    )
                    Result.success(wealthLevel)
                } else {
                    Result.failure(Exception(jsonResponse.getString("message")))
                }
            } else {
                Result.failure(Exception("HTTP Error: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取用户等级进度信息
     */
    suspend fun getLevelProgress(token: String): Result<LevelProgressInfo> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/progress")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer $token")
            connection.setRequestProperty("Content-Type", "application/json")
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                
                if (jsonResponse.getBoolean("success")) {
                    val data = jsonResponse.getJSONObject("data")
                    val progressInfo = LevelProgressInfo(
                        currentLevel = data.getString("currentLevel"),
                        currentWealthValue = data.getInt("currentWealthValue"),
                        currentLevelIcon = data.getString("currentLevelIcon"),
                        currentLevelColor = data.getString("currentLevelColor"),
                        progressPercentage = data.getDouble("progressPercentage"),
                        nextLevelRequirement = data.optInt("nextLevelRequirement").takeIf { it != 0 },
                        nextLevelName = data.optString("nextLevelName").takeIf { it != "null" },
                        nextLevelIcon = data.optString("nextLevelIcon").takeIf { it != "null" },
                        nextLevelColor = data.optString("nextLevelColor").takeIf { it != "null" }
                    )
                    Result.success(progressInfo)
                } else {
                    Result.failure(Exception(jsonResponse.getString("message")))
                }
            } else {
                Result.failure(Exception("HTTP Error: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取用户特权列表
     */
    suspend fun getUserPrivileges(token: String): Result<List<PrivilegeType>> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/privileges")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer $token")
            connection.setRequestProperty("Content-Type", "application/json")
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                
                if (jsonResponse.getBoolean("success")) {
                    val privilegesArray = jsonResponse.getJSONArray("data")
                    val privileges = mutableListOf<PrivilegeType>()
                    
                    for (i in 0 until privilegesArray.length()) {
                        val privilegeName = privilegesArray.getString(i)
                        try {
                            val privilege = PrivilegeType.valueOf(privilegeName)
                            privileges.add(privilege)
                        } catch (e: IllegalArgumentException) {
                            // 忽略无效的特权类型
                        }
                    }
                    
                    Result.success(privileges)
                } else {
                    Result.failure(Exception(jsonResponse.getString("message")))
                }
            } else {
                Result.failure(Exception("HTTP Error: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 检查用户是否有特定特权
     */
    suspend fun hasPrivilege(token: String, privilege: PrivilegeType): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/has-privilege/${privilege.name}")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer $token")
            connection.setRequestProperty("Content-Type", "application/json")
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                
                if (jsonResponse.getBoolean("success")) {
                    val hasPrivilege = jsonResponse.getBoolean("data")
                    Result.success(hasPrivilege)
                } else {
                    Result.failure(Exception(jsonResponse.getString("message")))
                }
            } else {
                Result.failure(Exception("HTTP Error: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取财富排行榜
     */
    suspend fun getWealthRanking(limit: Int = 10): Result<List<WealthLevelData>> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/ranking?limit=$limit")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                
                if (jsonResponse.getBoolean("success")) {
                    val dataArray = jsonResponse.getJSONArray("data")
                    val wealthLevels = mutableListOf<WealthLevelData>()
                    
                    for (i in 0 until dataArray.length()) {
                        val data = dataArray.getJSONObject(i)
                        val wealthLevel = WealthLevelData(
                            levelName = data.getString("levelName"),
                            levelIcon = data.getString("levelIcon"),
                            levelColor = data.getString("levelColor"),
                            wealthValue = data.getInt("wealthValue"),
                            progressPercentage = data.optDouble("progressPercentage", 0.0),
                            nextLevelName = data.optString("nextLevelName").takeIf { it != "null" },
                            nextLevelRequirement = data.optInt("nextLevelRequirement").takeIf { it != 0 },
                            userId = data.optLong("userId", 0L)
                        )
                        wealthLevels.add(wealthLevel)
                    }
                    
                    Result.success(wealthLevels)
                } else {
                    Result.failure(Exception(jsonResponse.getString("message")))
                }
            } else {
                Result.failure(Exception("HTTP Error: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取所有等级规则
     */
    suspend fun getLevelRules(): Result<List<LevelRule>> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/rules")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                
                if (jsonResponse.getBoolean("success")) {
                    val dataArray = jsonResponse.getJSONArray("data")
                    val rules = mutableListOf<LevelRule>()
                    
                    for (i in 0 until dataArray.length()) {
                        val data = dataArray.getJSONObject(i)
                        val rule = LevelRule(
                            levelName = data.getString("levelName"),
                            levelIcon = data.getString("levelIcon"),
                            levelColor = data.getString("levelColor"),
                            minWealthValue = data.getInt("minWealthValue"),
                            maxWealthValue = data.optInt("maxWealthValue").takeIf { it != 0 },
                            description = data.getString("description")
                        )
                        rules.add(rule)
                    }
                    
                    Result.success(rules)
                } else {
                    Result.failure(Exception(jsonResponse.getString("message")))
                }
            } else {
                Result.failure(Exception("HTTP Error: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
