package com.example.kramviapp.requests

import com.example.kramviapp.models.CreateProductModel
import com.example.kramviapp.models.PriceModel
import com.google.gson.annotations.SerializedName

data class ProductRequest(
    @SerializedName("product") val product: CreateProductModel,
    @SerializedName("prices") val prices: List<PriceModel>,
)
