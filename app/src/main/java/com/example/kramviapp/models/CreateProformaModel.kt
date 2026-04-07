package com.example.kramviapp.models

import com.example.kramviapp.enums.CurrencyCodeType

data class CreateProformaModel(
    val discount: Double,
    val igvPercent: Double,
    val currencyCode: CurrencyCodeType,
    val observation: String,
    val customerId: Int?,
)
