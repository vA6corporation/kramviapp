package com.example.kramviapp.models

import com.example.kramviapp.enums.IgvCodeType

data class OutStockModel(
    val productId: Int,
    val fullName: String,
    val stock: Double,
)
