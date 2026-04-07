package com.example.kramviapp.enums

import com.google.gson.annotations.SerializedName

enum class CurrencyCodeType(type: String) {

    @SerializedName("PEN")
    SOLES("PEN"),

    @SerializedName("USD")
    DOLARES("USD")
}