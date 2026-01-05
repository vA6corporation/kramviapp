package com.example.kramviapp.requests

import com.example.kramviapp.models.CreateDueModel
import com.example.kramviapp.models.CreatePaymentModel
import com.example.kramviapp.models.CreateSaleModel
import com.example.kramviapp.models.SaleItemModel
import com.google.gson.annotations.SerializedName

data class CreditRequest(
    @SerializedName("credit") val credit: CreateSaleModel,
    @SerializedName("saleItems") val saleItems: List<SaleItemModel>,
    @SerializedName("payments") val payments: List<CreatePaymentModel>,
    @SerializedName("dues") val dues: List<CreateDueModel>,
)