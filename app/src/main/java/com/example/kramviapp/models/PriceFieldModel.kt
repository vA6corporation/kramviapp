package com.example.kramviapp.models

data class PriceFieldModel(
    val name: String,
    var price: String,
    val priceListId: Int?,
    val officeId: Int?
)