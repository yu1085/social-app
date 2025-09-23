package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.TransactionRecord
import com.example.myapplication.model.TransactionStatus
import com.example.myapplication.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 钱包明细页面ViewModel
 */
class WalletDetailsViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(WalletDetailsUiState())
    val uiState: StateFlow<WalletDetailsUiState> = _uiState.asStateFlow()
    
    init {
        loadTransactionHistory()
    }
    
    private fun loadTransactionHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // TODO: 从API获取真实交易记录
                val mockTransactions = generateMockTransactions()
                
                val totalRecharge = mockTransactions
                    .filter { it.type == TransactionType.RECHARGE && it.status == TransactionStatus.SUCCESS }
                    .sumOf { it.amount }
                
                val totalConsume = mockTransactions
                    .filter { it.type == TransactionType.CONSUME && it.status == TransactionStatus.SUCCESS }
                    .sumOf { it.amount }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    transactions = mockTransactions,
                    totalBalance = 3195,
                    totalRecharge = totalRecharge,
                    totalConsume = totalConsume
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun generateMockTransactions(): List<TransactionRecord> {
        val now = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L
        val oneHour = 60 * 60 * 1000L
        
        return listOf(
            TransactionRecord(
                id = "tx_001",
                type = TransactionType.RECHARGE,
                amount = 1200,
                description = "充值1200金币",
                timestamp = now - oneHour,
                status = TransactionStatus.SUCCESS
            ),
            TransactionRecord(
                id = "tx_002",
                type = TransactionType.CONSUME,
                amount = 50,
                description = "发送礼物给小美",
                timestamp = now - 2 * oneHour,
                status = TransactionStatus.SUCCESS
            ),
            TransactionRecord(
                id = "tx_003",
                type = TransactionType.CONSUME,
                amount = 30,
                description = "语音通话消费",
                timestamp = now - 3 * oneHour,
                status = TransactionStatus.SUCCESS
            ),
            TransactionRecord(
                id = "tx_004",
                type = TransactionType.EARN,
                amount = 20,
                description = "每日签到奖励",
                timestamp = now - oneDay,
                status = TransactionStatus.SUCCESS
            ),
            TransactionRecord(
                id = "tx_005",
                type = TransactionType.RECHARGE,
                amount = 5800,
                description = "充值5800金币",
                timestamp = now - oneDay - oneHour,
                status = TransactionStatus.SUCCESS
            ),
            TransactionRecord(
                id = "tx_006",
                type = TransactionType.CONSUME,
                amount = 100,
                description = "购买VIP特权",
                timestamp = now - oneDay - 2 * oneHour,
                status = TransactionStatus.SUCCESS
            ),
            TransactionRecord(
                id = "tx_007",
                type = TransactionType.GIFT,
                amount = 80,
                description = "收到用户小王的礼物",
                timestamp = now - 2 * oneDay,
                status = TransactionStatus.SUCCESS
            ),
            TransactionRecord(
                id = "tx_008",
                type = TransactionType.CONSUME,
                amount = 25,
                description = "视频通话消费",
                timestamp = now - 2 * oneDay - oneHour,
                status = TransactionStatus.SUCCESS
            ),
            TransactionRecord(
                id = "tx_009",
                type = TransactionType.RECHARGE,
                amount = 800,
                description = "充值800金币",
                timestamp = now - 3 * oneDay,
                status = TransactionStatus.PENDING
            ),
            TransactionRecord(
                id = "tx_010",
                type = TransactionType.EXCHANGE,
                amount = 50,
                description = "积分兑换金币",
                timestamp = now - 3 * oneDay - oneHour,
                status = TransactionStatus.SUCCESS
            ),
            TransactionRecord(
                id = "tx_011",
                type = TransactionType.CONSUME,
                amount = 15,
                description = "开启隐身模式",
                timestamp = now - 4 * oneDay,
                status = TransactionStatus.SUCCESS
            ),
            TransactionRecord(
                id = "tx_012",
                type = TransactionType.EARN,
                amount = 10,
                description = "分享APP奖励",
                timestamp = now - 4 * oneDay - oneHour,
                status = TransactionStatus.SUCCESS
            ),
            TransactionRecord(
                id = "tx_013",
                type = TransactionType.RECHARGE,
                amount = 2800,
                description = "充值2800金币",
                timestamp = now - 5 * oneDay,
                status = TransactionStatus.FAILED
            ),
            TransactionRecord(
                id = "tx_014",
                type = TransactionType.CONSUME,
                amount = 40,
                description = "购买表情包",
                timestamp = now - 5 * oneDay - oneHour,
                status = TransactionStatus.SUCCESS
            ),
            TransactionRecord(
                id = "tx_015",
                type = TransactionType.GIFT,
                amount = 120,
                description = "收到用户小李的礼物",
                timestamp = now - 6 * oneDay,
                status = TransactionStatus.SUCCESS
            )
        )
    }
    
    fun refreshTransactions() {
        loadTransactionHistory()
    }
}

/**
 * 钱包明细页面UI状态
 */
data class WalletDetailsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val transactions: List<TransactionRecord> = emptyList(),
    val totalBalance: Long = 0,
    val totalRecharge: Long = 0,
    val totalConsume: Long = 0
)
