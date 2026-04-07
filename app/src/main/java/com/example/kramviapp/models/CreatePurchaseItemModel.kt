package com.example.kramviapp.models

import com.example.kramviapp.enums.IgvCodeType

data class CreatePurchaseItemModel(
    val fullName: String,
    val productId: Int,
    val igvCode: IgvCodeType,
    val unitCode: String,
    val quantity: Double,
    val cost: Double,
    val price: Double,
    val lot: Unit?,
)
