package com.example.kramviapp.models

data class CategoryModel(
    val id: Int,
    val name: String,
    var products: List<ProductModel>? = null
)
