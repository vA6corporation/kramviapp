package com.example.kramviapp.models

import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.enums.PrintZoneType

data class CreateProductModel(
    val name: String,
    val sku: String,
    val upc: String,
    val categoryId: String,
    var price: Double,
    val unitCode: String,
    val igvCode: IgvCodeType,
    val isTrackStock: Boolean,
    val stock: Double,
    val annotations: List<Unit>
)
