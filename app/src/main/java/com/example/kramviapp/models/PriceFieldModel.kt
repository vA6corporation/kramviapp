package com.example.kramviapp.models

data class PriceFieldModel(
    val name: String,
    var price: String,
    val priceListId: String?,
    val officeId: String?
)