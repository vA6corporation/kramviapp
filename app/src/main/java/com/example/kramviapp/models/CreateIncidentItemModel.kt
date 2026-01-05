package com.example.kramviapp.models

data class CreateIncidentItemModel(
    val quantity: Double,
    val cost: Double,
    val unitCode: String?,
    val productId: String,
)
