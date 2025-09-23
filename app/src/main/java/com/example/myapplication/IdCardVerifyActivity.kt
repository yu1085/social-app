package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.service.IdCardApiService
import com.example.myapplication.service.IdCardVerifyResult
import com.example.myapplication.auth.AuthManager

class IdCardVerifyActivity : AppCompatActivity() {
    
    private lateinit var toolbar: Toolbar
    private lateinit var etName: EditText
    private lateinit var etIdCard: EditText
    private lateinit var btnVerify: Button
    private lateinit var tvResult: TextView
    
    // API服务
    private val idCardApiService = IdCardApiService()
    
    // 认证管理器
    private lateinit var authManager: AuthManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_id_card_verify)
        
        // 初始化认证管理器
        authManager = AuthManager.getInstance(this)
        
        initViews()
        setupUI()
        setupClickListeners()
    }
    
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        etName = findViewById(R.id.et_real_name)
        etIdCard = findViewById(R.id.et_id_card_number)
        btnVerify = findViewById(R.id.btn_submit)
        tvResult = findViewById(R.id.tv_description)
    }
    
    private fun setupUI() {
        // 设置标题
        toolbar.title = "身份证实名认证"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun setupClickListeners() {
        btnVerify.setOnClickListener {
            android.util.Log.d("IdCardVerify", "按钮被点击")
            verifyIdCard()
        }
        
        // 添加额外的调试信息
        android.util.Log.d("IdCardVerify", "按钮ID: ${btnVerify.id}, 按钮文本: ${btnVerify.text}")
    }
    
    private fun verifyIdCard() {
        val name = etName.text.toString().trim()
        val idCard = etIdCard.text.toString().trim()
        
        if (name.isEmpty()) {
            etName.error = "请输入姓名"
            return
        }
        
        if (idCard.isEmpty()) {
            etIdCard.error = "请输入身份证号"
            return
        }
        
        if (!isValidIdCard(idCard)) {
            etIdCard.error = "身份证号格式不正确"
            return
        }
        
        // 显示加载状态
        btnVerify.isEnabled = false
        btnVerify.text = "验证中..."
        tvResult.text = "正在验证身份证信息..."
        
        // 检查用户是否已登录
        if (!authManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录后再进行认证", Toast.LENGTH_LONG).show()
            btnVerify.isEnabled = true
            btnVerify.text = "提交认证"
            return
        }
        
        // 获取用户ID和token
        val userId = authManager.getUserId()
        val token = authManager.getToken()
        
        if (userId == -1L || token == null) {
            Toast.makeText(this, "认证信息无效，请重新登录", Toast.LENGTH_LONG).show()
            btnVerify.isEnabled = true
            btnVerify.text = "提交认证"
            return
        }
        
        // 添加调试日志
        android.util.Log.d("IdCardVerify", "用户ID: $userId, 开始验证身份证: name=$name, idCard=$idCard")
        
        // 调用真实的后端API
        lifecycleScope.launch {
            try {
                // 调用后端身份证验证API
                val result = callIdCardVerifyAPI(name, idCard, token)
                
                val isValid = result["match"] as? Boolean ?: false
                
                runOnUiThread {
                    btnVerify.isEnabled = true
                    btnVerify.text = "提交认证"  // 修复按钮文本
                    
                    if (isValid) {
                        tvResult.text = "验证成功！身份证信息匹配。"
                        tvResult.setTextColor(getColor(android.R.color.holo_green_dark))
                        
                        // 验证成功后可以跳转到其他页面
                        Toast.makeText(this@IdCardVerifyActivity, "身份证验证成功", Toast.LENGTH_SHORT).show()
                        android.util.Log.d("IdCardVerify", "身份证验证成功")
                    } else {
                        tvResult.text = "验证失败！身份证信息不匹配。"
                        tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
                        android.util.Log.d("IdCardVerify", "身份证验证失败")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("IdCardVerify", "验证过程中发生错误", e)
                runOnUiThread {
                    btnVerify.isEnabled = true
                    btnVerify.text = "提交认证"  // 修复按钮文本
                    tvResult.text = "验证失败：${e.message}"
                    tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            }
        }
    }
    
    private fun isValidIdCard(idCard: String): Boolean {
        // 简单的身份证号格式验证
        val pattern = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$"
        return idCard.matches(pattern.toRegex())
    }
    
    /**
     * 调用后端身份证验证API
     */
    private suspend fun callIdCardVerifyAPI(name: String, idCard: String, token: String): Map<String, Any> {
        return try {
            android.util.Log.d("IdCardVerify", "调用后端API: name=$name, idCard=$idCard, token=${token.take(10)}...")
            
            // 使用IdCardApiService调用真实的后端API
            val result = idCardApiService.verifyIdCard(name, idCard, token)
            
            mapOf(
                "match" to result.match,
                "message" to result.message,
                "verifyId" to (result.verifyId ?: ""),
                "certifyId" to (result.certifyId ?: ""),
                "success" to result.success
            )
        } catch (e: Exception) {
            android.util.Log.e("IdCardVerify", "API调用失败", e)
            mapOf(
                "match" to false,
                "message" to "API调用失败: ${e.message}",
                "verifyId" to "",
                "success" to false
            )
        }
    }
    
    
    private fun simulateIdCardVerification(name: String, idCard: String): Boolean {
        // 模拟验证逻辑
        // 在实际应用中，这里应该调用后端API
        return name.isNotEmpty() && idCard.length == 18
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}


