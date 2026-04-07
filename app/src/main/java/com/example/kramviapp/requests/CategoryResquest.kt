package com.example.kramviapp.requests

import com.example.kramviapp.models.CreateCategoryModel
import com.example.kramviapp.models.CreateExpenseModel
import com.google.gson.annotations.SerializedName

data class CategoryResquest(
    @SerializedName("category") val category: CreateCategoryModel
)