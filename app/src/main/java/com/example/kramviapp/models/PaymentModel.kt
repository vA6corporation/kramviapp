package com.example.kramviapp.models

data class PaymentModel(
    val charge: Double,
    val paymentMethodId: Int,
    val createdAt: String,
    val deletedAt: String?,
)
