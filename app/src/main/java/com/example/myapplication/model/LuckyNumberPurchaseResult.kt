package com.example.myapplication.model

import java.math.BigDecimal

/**
 * 靓号购买结果
 */
sealed class LuckyNumberPurchaseResult {
    data class Success(val luckyNumber: LuckyNumber) : LuckyNumberPurchaseResult()
    data class Error(val message: String) : LuckyNumberPurchaseResult()
    data class InsufficientBalance(val required: BigDecimal, val current: BigDecimal) : LuckyNumberPurchaseResult()
    data class AlreadyOwned(val luckyNumber: LuckyNumber) : LuckyNumberPurchaseResult()
}
