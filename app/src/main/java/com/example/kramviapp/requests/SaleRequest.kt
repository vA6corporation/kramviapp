package com.example.kramviapp.requests

import com.example.kramviapp.models.CreatePaymentModel
import com.example.kramviapp.models.CreateSaleModel
import com.example.kramviapp.models.SaleItemModel
import com.google.gson.annotations.SerializedName

data class SaleRequest(
    @SerializedName("sale") val sale: CreateSaleModel,
    @SerializedName("saleItems") val saleItems: List<SaleItemModel>,
    @SerializedName("payments") val payments: List<CreatePaymentModel>,
)