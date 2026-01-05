package com.example.kramviapp.models

data class SummarySaleItemModel(
    val productId: String,
    val categoryId: String,
    val fullName: String,
    val cost: Double,
    val totalQuantity: Double,
    val totalBonus: Double,
    val totalSale: Double,
    val totalPurchase: Double,
)
