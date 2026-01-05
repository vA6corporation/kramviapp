package com.example.kramviapp.models

import com.example.kramviapp.enums.CurrencyCodeType
import com.example.kramviapp.enums.InvoiceType

data class CreateProformaModel(
    val discount: Double,
    val igvPercent: Int,
    val currencyCode: CurrencyCodeType,
    val observations: String,
    val customerId: String?,
)
