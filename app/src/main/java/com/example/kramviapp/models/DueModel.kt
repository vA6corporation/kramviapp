package com.example.kramviapp.models

import java.util.Date

data class DueModel(
    val charge: Double,
    val preCharge: Double,
    val dueDate: Date
)
