package com.example.kramviapp.models

data class PaymentModel(
    val charge: Double,
    val paymentMethodId: String,
    val createdAt: String,
    val deletedAt: String?,
)
