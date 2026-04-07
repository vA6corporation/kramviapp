package com.example.kramviapp.requests

import com.example.kramviapp.models.CreatePurchaseItemModel
import com.example.kramviapp.models.CreatePurchaseModel
import com.google.gson.annotations.SerializedName

data class PurchaseRequest(
    @SerializedName("purchase") val purchase: CreatePurchaseModel,
    @SerializedName("purchaseItems") val purchaseItems: List<CreatePurchaseItemModel>,
)
