package com.example.kramviapp.models

import com.example.kramviapp.enums.CurrencyCodeType
import com.example.kramviapp.enums.InvoiceCode

data class CreateSaleModel(
    val invoiceCode: InvoiceCode,
    val discount: Double,
    val cash: Double,
    val igvPercent: Double,
    val rcPercent: Double,
    val currencyCode: CurrencyCodeType,
    val observation: String,
    val customerId: Int?,
    val turnId: Int,
    val isCredit: Boolean
)