package com.example.kramviapp.models

data class CreatePaymentModel(
    var charge: Double,
    var paymentMethodId: String,
    val turnId: String,
)
