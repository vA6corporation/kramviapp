package com.example.kramviapp.models

import androidx.compose.ui.graphics.vector.ImageVector

data class ActionModel(
    val id: String,
    val text: String,
    val drawer: ImageVector,
    val isHide: Boolean = true,
)
