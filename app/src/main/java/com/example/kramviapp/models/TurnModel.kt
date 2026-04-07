package com.example.kramviapp.models

data class TurnModel(
    val id: Int,
    var openCash: Double,
    val createdAt: String,
    val closedAt: String,
    var observation: String,
    val user: UserModel
)
