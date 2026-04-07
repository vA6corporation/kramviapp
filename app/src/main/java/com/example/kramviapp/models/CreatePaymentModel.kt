package com.example.kramviapp.models

data class CreatePaymentModel(
    var charge: Double,
    var paymentMethodId: Int,
    val turnId: Int,
)
