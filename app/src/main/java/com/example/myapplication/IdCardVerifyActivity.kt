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

class IdCardVerifyActivity : AppCompatActivity() {
    
    private lateinit var toolbar: Toolbar
    private lateinit var etName: EditText
    private lateinit var etIdCard: EditText
    private lateinit var btnVerify: Button
    private lateinit var tvResult: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_id_card_verify)
        
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
            verifyIdCard()
        }
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
        
        // 模拟API调用
        lifecycleScope.launch {
            try {
                // 这里应该调用真实的API
                // val result = apiService.verifyIdCard(name, idCard)
                
                // 模拟验证结果
                val isValid = simulateIdCardVerification(name, idCard)
                
                runOnUiThread {
                    btnVerify.isEnabled = true
                    btnVerify.text = "开始验证"
                    
                    if (isValid) {
                        tvResult.text = "验证成功！身份证信息匹配。"
                        tvResult.setTextColor(getColor(android.R.color.holo_green_dark))
                        
                        // 验证成功后可以跳转到其他页面
                        Toast.makeText(this@IdCardVerifyActivity, "身份证验证成功", Toast.LENGTH_SHORT).show()
                    } else {
                        tvResult.text = "验证失败！身份证信息不匹配。"
                        tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    btnVerify.isEnabled = true
                    btnVerify.text = "开始验证"
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


