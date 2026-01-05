package com.example.kramviapp.models

data class CategoryModel(
    val _id: String,
    val name: String,
    var products: List<ProductModel>? = null
)
