package com.example.kramviapp.models

import com.example.kramviapp.enums.InvoiceCode

data class CreatePurchaseModel(
    val invoiceType: InvoiceCode,
    val observation: String,
    val isCredit: Boolean,
    val paymentMethodId: Int,
    val purchasedAt: String,
    val providerId: Int?,
    val serie: String?,
    val expirationAt: String?,
)
