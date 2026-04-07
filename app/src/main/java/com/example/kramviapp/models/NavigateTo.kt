package com.example.kramviapp.models

data class NavigateTo(
    val path: String,
    val isNoBack: Boolean = false
)
