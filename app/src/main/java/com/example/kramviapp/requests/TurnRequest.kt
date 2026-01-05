package com.example.kramviapp.requests

import com.example.kramviapp.models.CreatePaymentModel
import com.example.kramviapp.models.CreateSaleModel
import com.example.kramviapp.models.CustomerModel
import com.example.kramviapp.models.PaymentModel
import com.example.kramviapp.models.SaleItemModel
import com.example.kramviapp.models.TurnModel
import com.google.gson.annotations.SerializedName

data class TurnRequest(
    @SerializedName("turn") val turn: TurnModel,
)