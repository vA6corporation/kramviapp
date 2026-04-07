package com.example.kramviapp.requests

import com.example.kramviapp.models.BoardItemModel
import com.google.gson.annotations.SerializedName

data class BoardRequest(
    @SerializedName("boardItems") val boardItems: List<BoardItemModel>,
    @SerializedName("preBoardItems") val preBoardItems: List<BoardItemModel>
)
