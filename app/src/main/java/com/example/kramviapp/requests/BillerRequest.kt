package com.example.kramviapp.requests

import com.example.kramviapp.models.ProductItemModel
import com.example.kramviapp.models.CreatePaymentModel
import com.example.kramviapp.models.CreateSaleModel
import com.example.kramviapp.models.DueModel
import com.google.gson.annotations.SerializedName

data class BillerRequest(
    @SerializedName("sale") val sale: CreateSaleModel,
    @SerializedName("productItems") val productItems: List<ProductItemModel>,
    @SerializedName("payments") val payments: List<CreatePaymentModel>,
    @SerializedName("dues") val dues: List<DueModel>,
)