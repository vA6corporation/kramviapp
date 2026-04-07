package com.example.kramviapp.enums

import com.google.gson.annotations.SerializedName

enum class InvoiceCode(type: String) {

    @SerializedName("03")
    BOLETA("03"),

    @SerializedName("01")
    FACTURA("01"),

    @SerializedName("00")
    NOTA_DE_VENTA("00")

}