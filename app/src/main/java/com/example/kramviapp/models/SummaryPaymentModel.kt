package com.example.kramviapp.models

data class SummaryPaymentModel(
    val totalCharge: Double,
    val totalQuantity: Int,
    val paymentMethod: PaymentMethodModel
)
