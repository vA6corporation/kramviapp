package com.example.kramviapp.models

import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.enums.PrintZoneType

data class ProductModel(
    val _id: String,
    val fullName: String,
    val onModel: String,
    val sku: String,
    val upc: String,
    var price: Double,
    val cost: Double,
    val igvCode: IgvCodeType,
    val unitCode: String,
    val categoryId: String,
    var isTrackStock: Boolean,
    val prices: List<PriceModel>,
    val annotations: List<String>,
    val printZone: PrintZoneType,
    var stock: Double,
)
