package com.example.kramviapp.requests

import com.example.kramviapp.models.BillerItemModel
import com.example.kramviapp.models.BoardItemModel
import com.example.kramviapp.models.CreatePaymentModel
import com.example.kramviapp.models.CreateSaleModel
import com.example.kramviapp.models.CustomerModel
import com.example.kramviapp.models.DueModel
import com.example.kramviapp.models.PaymentModel
import com.example.kramviapp.models.SaleItemModel
import com.google.gson.annotations.SerializedName

data class BillerRequest(
    @SerializedName("sale") val sale: CreateSaleModel,
    @SerializedName("saleItems") val saleItems: List<BillerItemModel>,
    @SerializedName("payments") val payments: List<CreatePaymentModel>,
    @SerializedName("dues") val dues: List<DueModel>,
)