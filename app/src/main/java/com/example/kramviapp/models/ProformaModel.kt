package com.example.kramviapp.models

import com.example.kramviapp.enums.CurrencyCodeType
import com.example.kramviapp.enums.InvoiceType

data class ProformaModel(
    val _id: String,
    val addressIndex: Number,
    val proformaNumber: String,
    val charge: Double,
    val igv: Double,
    val chargeLetters: String,
    val discount: Double?,
    val cash: Double?,
    val currencyCode: CurrencyCodeType,
    val observations: String,
    val gravado: Double,
    val gratuito: Double,
    val exonerado: Double,
    val inafecto: Double,

    val customer: CustomerModel?,
    val user: UserModel,
    val proformaItems: List<ProformaItemModel>,

    val isCredit: Boolean,

    val deletedAt: String?,
    val createdAt: String,
)
