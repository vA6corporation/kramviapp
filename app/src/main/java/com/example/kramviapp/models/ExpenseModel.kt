package com.example.kramviapp.models

data class ExpenseModel(
    val id: Int,
    val turnId: Int,
    val concept: String,
    val charge: Double,
    val createdAt: String,
)
