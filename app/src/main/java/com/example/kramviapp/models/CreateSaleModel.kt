package com.example.kramviapp.models

import com.example.kramviapp.enums.CurrencyCodeType
import com.example.kramviapp.enums.InvoiceType

data class CreateSaleModel(
    val invoiceType: InvoiceType,
    val paymentMethodId: String,
    val discount: Double,
    val cash: Double,
    val igvPercent: Int,
    val currencyCode: CurrencyCodeType,
    val observations: String,
    val workerId: String?,
    val customerId: String?,
    val turnId: String,
    val isCredit: Boolean
)