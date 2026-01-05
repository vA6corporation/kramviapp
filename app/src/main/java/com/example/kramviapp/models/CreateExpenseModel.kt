package com.example.kramviapp.models

data class CreateExpenseModel(
    val concept: String,
    val charge: Double,
    val turnId: String
)
