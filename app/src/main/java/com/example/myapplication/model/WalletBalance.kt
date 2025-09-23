package com.example.myapplication.model

/**
 * 钱包余额数据类
 */
data class WalletBalance(
    val totalBalance: Long = 0,
    val rechargeBalance: Long = 0,
    val giftBalance: Long = 0,
    val exchangeBalance: Long = 0,
    val earnBalance: Long = 0
) {
    /**
     * 获取总余额显示文本
     */
    fun getTotalBalanceText(): String {
        return "${totalBalance}金币"
    }
    
    /**
     * 获取充值余额显示文本
     */
    fun getRechargeBalanceText(): String {
        return "${rechargeBalance}金币"
    }
    
    /**
     * 获取礼物余额显示文本
     */
    fun getGiftBalanceText(): String {
        return "${giftBalance}金币"
    }
    
    /**
     * 获取兑换余额显示文本
     */
    fun getExchangeBalanceText(): String {
        return "${exchangeBalance}金币"
    }
    
    /**
     * 获取赚取余额显示文本
     */
    fun getEarnBalanceText(): String {
        return "${earnBalance}金币"
    }
    
    /**
     * 是否有余额
     */
    fun hasBalance(): Boolean {
        return totalBalance > 0
    }
    
    /**
     * 获取余额详情列表
     */
    fun getBalanceDetails(): List<BalanceDetail> {
        return listOf(
            BalanceDetail("充值余额", rechargeBalance, "通过充值获得的金币"),
            BalanceDetail("礼物余额", giftBalance, "通过礼物获得的金币"),
            BalanceDetail("兑换余额", exchangeBalance, "通过兑换获得的金币"),
            BalanceDetail("赚取余额", earnBalance, "通过活动赚取的金币")
        )
    }
}

/**
 * 余额详情数据类
 */
data class BalanceDetail(
    val name: String,
    val amount: Long,
    val description: String
) {
    /**
     * 获取金额显示文本
     */
    fun getAmountText(): String {
        return "${amount}金币"
    }
}

