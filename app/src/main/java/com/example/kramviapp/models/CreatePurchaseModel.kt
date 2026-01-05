package com.example.kramviapp.models

import com.example.kramviapp.enums.InvoiceType

data class CreatePurchaseModel(
    val invoiceType: InvoiceType,
    val observations: String,
    val isCredit: Boolean,
    val paymentMethodId: String,
    val purchasedAt: String,
    val providerId: String?,
    val serie: String?,
    val expirationAt: String?,
)
