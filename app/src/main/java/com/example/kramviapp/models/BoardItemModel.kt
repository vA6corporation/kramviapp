package com.example.kramviapp.models

import com.example.kramviapp.enums.IgvCodeType
import com.example.kramviapp.enums.PrintZoneType

data class BoardItemModel(
    val id: Int,
    val fullName: String,
    var price: Double,
    var quantity: Double,
    var preQuantity: Double,
    var igvCode: IgvCodeType,
    val preIgvCode: IgvCodeType,
    val unitCode: String,
    var observation: String,
    val printZone: PrintZoneType,
    val isTrackStock: Boolean,
    val categoryId: Int,
    val productId: Int,
    val boardId: Int,
)
