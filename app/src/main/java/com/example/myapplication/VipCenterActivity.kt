package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.screens.VipCenterScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * VIP会员中心Activity
 */
class VipCenterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VipCenterScreen(
                        onBackClick = {
                            // 返回上一页
                            finish()
                        },
                        onUpgradeSvipClick = {
                            // 处理升级SVIP点击
                            handleUpgradeSvipClick()
                        },
                        onActivateVipClick = {
                            // 处理开通VIP点击
                            handleActivateVipClick()
                        },
                        onPaymentConfirm = { paymentMethod, membershipType, price ->
                            // 处理支付确认
                            handlePaymentConfirm(paymentMethod, membershipType, price)
                        },
                        onAgreementClick = {
                            // 处理协议点击
                            handleAgreementClick()
                        }
                    )
                }
            }
        }
    }
    
    /**
     * 处理升级SVIP点击事件
     */
    private fun handleUpgradeSvipClick() {
        showToast("点击了升级SVIP")
        // TODO: 实现升级SVIP逻辑
    }
    
    /**
     * 处理开通VIP点击事件
     */
    private fun handleActivateVipClick() {
        showToast("点击了立即开通VIP")
        // TODO: 实现开通VIP逻辑
    }
    
    /**
     * 处理支付确认事件
     */
    private fun handlePaymentConfirm(paymentMethod: String, membershipType: String, price: String) {
        showToast("支付确认: $paymentMethod - $membershipType - $price")
        // TODO: 实现真实的支付逻辑
        // 这里可以集成支付宝、微信支付等第三方支付SDK
    }
    
    /**
     * 处理协议点击事件
     */
    private fun handleAgreementClick() {
        val intent = android.content.Intent(this, MembershipAgreementActivity::class.java)
        startActivity(intent)
    }
    
    /**
     * 显示Toast消息
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
