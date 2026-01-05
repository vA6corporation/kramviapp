package com.example.kramviapp.models

data class TurnModel(
    val _id: String,
    var openCash: Double,
    val createdAt: String,
    val closedAt: String,
    var observations: String,
    val user: UserModel
)
