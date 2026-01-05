package com.example.kramviapp.requests

import com.example.kramviapp.models.CreateCustomerModel
import com.google.gson.annotations.SerializedName

data class CustomerResquest(
    @SerializedName("customer") val customer: CreateCustomerModel
)