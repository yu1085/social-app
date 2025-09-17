package com.example.myapplication.dto

import java.math.BigDecimal

data class WalletDTO(
    val id: Long? = null,
    val userId: Long? = null,
    val balance: BigDecimal? = null,
    val frozenAmount: BigDecimal? = null,
    val currency: String? = null,
    val availableBalance: BigDecimal? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
