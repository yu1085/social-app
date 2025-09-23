# SocialMeet Java后端API使用指南

## 项目架构

```
F:\MyApplication\
├── app/                    # Android客户端（Kotlin/Java）
├── SocialMeet/            # Spring Boot Java后端
│   ├── controller/        # REST API控制器
│   ├── service/          # 业务逻辑服务
│   ├── entity/           # 数据实体
│   ├── repository/       # 数据访问层
│   └── dto/              # 数据传输对象
```

## 现有API接口

### 1. 用户相关API

#### 获取用户资料
```
GET /api/users/profile
Authorization: Bearer {token}
```

**响应示例：**
```json
{
    "success": true,
    "message": "获取成功",
    "data": {
        "id": 1,
        "username": "user123",
        "nickname": "相约未来605",
        "phone": "13800138000",
        "email": "user@example.com",
        "avatarUrl": "https://example.com/avatar.jpg",
        "gender": "男",
        "birthDate": "1990-01-01T00:00:00",
        "bio": "我很懒没想好个性签名",
        "location": "南京",
        "age": 34,
        "height": 175,
        "weight": 70,
        "education": "本科",
        "income": "10-20万",
        "isOnline": true,
        "isVerified": false
    }
}
```

#### 更新用户资料
```
PUT /api/users/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
    "nickname": "新昵称",
    "bio": "新的个性签名",
    "height": 175,
    "weight": 70,
    "education": "本科",
    "income": "10-20万"
}
```

#### 搜索用户
```
GET /api/users/search?keyword=关键词&location=城市&gender=性别&page=0&size=20
Authorization: Bearer {token}
```

### 2. 认证相关API

#### 获取认证信息
```
GET /api/user-certification
Authorization: Bearer {token}
```

#### 提交认证
```
POST /api/user-certification
Authorization: Bearer {token}
Content-Type: application/json

{
    "realName": "张三",
    "idCardNumber": "110101199001011234",
    "certificationType": "REALNAME"
}
```

### 3. 钱包相关API

#### 获取余额
```
GET /api/wallet/balance
Authorization: Bearer {token}
```

#### 充值
```
POST /api/wallet/recharge?amount=100.00
Authorization: Bearer {token}
```

#### 获取交易记录
```
GET /api/wallet/transactions?page=0&size=20
Authorization: Bearer {token}
```

### 4. 通话相关API

#### 发起通话
```
POST /api/call/initiate
Authorization: Bearer {token}
Content-Type: application/json

{
    "targetUserId": 2,
    "callType": "VOICE"
}
```

#### 接听通话
```
POST /api/call/accept
Authorization: Bearer {token}
Content-Type: application/json

{
    "callSessionId": "call_123456"
}
```

### 5. 匹配相关API

#### 创建匹配请求
```
POST /api/match/request
Authorization: Bearer {token}
Content-Type: application/json

{
    "targetUserId": 2,
    "message": "你好，我想认识你"
}
```

#### 尝试匹配
```
POST /api/match/try-match
Authorization: Bearer {token}
```

## Android端调用示例

### 1. 获取用户资料
```kotlin
class UserService {
    suspend fun getProfile(): UserDTO? {
        val url = URL("${ApiConfig.getBaseUrl()}${ApiConfig.Endpoints.GET_PROFILE}")
        val connection = url.openConnection() as HttpURLConnection
        
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "Bearer ${getAuthToken()}")
        
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val response = readResponse(connection)
            val jsonResponse = JSONObject(response)
            return parseUserDTO(jsonResponse.getJSONObject("data"))
        }
        return null
    }
}
```

### 2. 更新用户资料
```kotlin
class UserService {
    suspend fun updateProfile(profileData: ProfileData): Boolean {
        val url = URL("${ApiConfig.getBaseUrl()}${ApiConfig.Endpoints.UPDATE_PROFILE.replace("{id}", getUserId())}")
        val connection = url.openConnection() as HttpURLConnection
        
        connection.requestMethod = "PUT"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer ${getAuthToken()}")
        connection.doOutput = true
        
        val jsonData = JSONObject().apply {
            put("nickname", profileData.nickname)
            put("bio", profileData.signature)
            put("height", profileData.height.replace("cm", "").toIntOrNull())
            put("weight", profileData.weight.replace("kg", "").toIntOrNull())
            put("education", profileData.education)
            put("income", profileData.annualIncome)
        }
        
        val outputStream = connection.outputStream
        val writer = OutputStreamWriter(outputStream)
        writer.write(jsonData.toString())
        writer.flush()
        writer.close()
        
        return connection.responseCode == HttpURLConnection.HTTP_OK
    }
}
```

### 3. 提交实名认证
```kotlin
class CertificationService {
    suspend fun submitRealNameCertification(realName: String, idCardNumber: String): Boolean {
        val url = URL("${ApiConfig.getBaseUrl()}${ApiConfig.Endpoints.SUBMIT_CERTIFICATION}")
        val connection = url.openConnection() as HttpURLConnection
        
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer ${getAuthToken()}")
        connection.doOutput = true
        
        val jsonData = JSONObject().apply {
            put("realName", realName)
            put("idCardNumber", idCardNumber)
            put("certificationType", "REALNAME")
        }
        
        val outputStream = connection.outputStream
        val writer = OutputStreamWriter(outputStream)
        writer.write(jsonData.toString())
        writer.flush()
        writer.close()
        
        return connection.responseCode == HttpURLConnection.HTTP_OK
    }
}
```

## 数据模型映射

### Android ProfileData → Java User实体
```kotlin
// Android端
data class ProfileData(
    var nickname: String = "",
    var birthday: String = "",
    var gender: String = "",
    var height: String = "",
    var weight: String = "",
    var signature: String = "",
    // ...
)

// 映射到Java后端
val jsonData = JSONObject().apply {
    put("nickname", profileData.nickname)
    put("bio", profileData.signature)
    put("height", profileData.height.replace("cm", "").toIntOrNull())
    put("weight", profileData.weight.replace("kg", "").toIntOrNull())
    // ...
}
```

## 启动和测试

### 1. 启动SocialMeet后端
```bash
cd F:\MyApplication\SocialMeet
./gradlew bootRun
```

### 2. 测试API
```bash
# 测试健康检查
curl http://localhost:8080/api/health

# 测试获取用户资料
curl -H "Authorization: Bearer your_token" http://localhost:8080/api/users/profile
```

### 3. Android端配置
```kotlin
// ApiConfig.kt
object ApiConfig {
    private const val DEV_BASE_URL = "http://10.0.2.2:8080/api"  // Spring Boot端口8080
    private const val IS_DEVELOPMENT = true
}
```

## 注意事项

1. **端口配置** - Spring Boot默认端口是8080，不是3000
2. **认证方式** - 使用JWT token认证
3. **数据格式** - 使用JSON格式交换数据
4. **错误处理** - 统一使用ApiResponse包装响应
5. **跨域配置** - 后端已配置CORS支持

现在Android客户端可以正确调用SocialMeet Java后端的所有API接口了！
