package com.example.kramviapp.models

import com.example.kramviapp.enums.CurrencyCodeType

data class ProformaModel(
    val id: Int,
    val addressIndex: Number,
    val proformaNumber: String,
    val charge: Double,
    val igv: Double,
    val chargeLetters: String,
    val discount: Double?,
    val cash: Double?,
    val currencyCode: CurrencyCodeType,
    val observation: String,
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
