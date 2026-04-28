package com.example.kramviapp.models

import com.example.kramviapp.enums.InvoiceCode

data class CreatePurchaseModel(
    val invoiceCode: InvoiceCode,
    val observation: String,
    val providerId: Int?,
    val serie: String,
    val createdAt: String?,
    val expirationAt: String?,
)
