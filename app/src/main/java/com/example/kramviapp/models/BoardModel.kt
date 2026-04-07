package com.example.kramviapp.models

data class BoardModel(
    val id: Int,
    val ticketNumber: String,
    val tableId: Int,
    var boardItems: MutableList<BoardItemModel>
)