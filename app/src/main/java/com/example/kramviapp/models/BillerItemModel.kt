package com.example.kramviapp.models

import com.example.kramviapp.enums.IgvCodeType

data class BillerItemModel(
    val fullName: String,
    val price: Double,
    val quantity: Double,
    val igvCode: IgvCodeType,
)