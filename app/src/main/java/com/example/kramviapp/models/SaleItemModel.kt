package com.example.kramviapp.models

import com.example.kramviapp.enums.IgvCodeType

data class SaleItemModel(
    val fullName: String,
    var price: Double,
    val onModel: String,
    var quantity: Double,
    var igvCode: IgvCodeType,
    val preIgvCode: IgvCodeType,
    val unitCode: String,
    val productId: String,
    val prices: List<PriceModel>,
    val isTrackStock: Boolean,
    var observations: String = "",
)
