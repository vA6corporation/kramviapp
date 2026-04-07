package com.example.kramviapp.models

import com.example.kramviapp.enums.CurrencyCodeType
import com.example.kramviapp.enums.InvoiceCode

data class SaleModel(
    val id: Int,
    val invoiceCode: InvoiceCode,
    val invoiceName: String,
    val invoicePrefix: String,
    val invoiceNumber: String,
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
    val saleItems: List<SaleItemModel>,
    val payments: List<PaymentModel>,

    val isCredit: Boolean,

    val deletedAt: String?,
    val createdAt: String,

    var cdr: CdrModel?,
    var ticket: TicketModel?
)
