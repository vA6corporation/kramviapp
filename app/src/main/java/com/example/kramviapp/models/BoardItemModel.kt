package com.example.kramviapp.models

import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.enums.PrintZoneType

data class BoardItemModel(
    val _id: String,
    val fullName: String,
    var price: Double,
    var quantity: Double,
    var preQuantity: Double,
    var igvCode: IgvCodeType,
    val preIgvCode: IgvCodeType,
    val unitCode: String,
    var observations: String,
    val printZone: PrintZoneType,
    val isTrackStock: Boolean,
    val categoryId: String,
    val productId: String,
    val boardId: String,
)
