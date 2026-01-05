package com.example.kramviapp.models

data class ExpenseModel(
    val _id: String,
    val turnId: String,
    val concept: String,
    val charge: Double,
    val createdAt: String,
)
