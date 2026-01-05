package com.example.kramviapp.models

data class BoardModel(
    val _id: String,
    val ticketNumber: String,
    val tableId: String,
    var boardItems: MutableList<BoardItemModel>
)