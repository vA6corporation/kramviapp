package com.example.kramviapp.models

data class PurchaseStockModel(
    val quantity: Double,
    val cost: Double,
    val paymentMethodId: String,
    val observations: String
)
