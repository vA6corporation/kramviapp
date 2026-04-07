package com.example.kramviapp.models

data class SummarySaleItemModel(
    val productId: Int,
    val categoryId: Int,
    val fullName: String,
    val cost: Double,
    val totalQuantity: Double,
    val totalBonus: Double,
    val totalSale: Double,
    val totalPurchase: Double,
)
