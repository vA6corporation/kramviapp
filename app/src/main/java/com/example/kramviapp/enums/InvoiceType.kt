package com.example.kramviapp.enums

import com.google.gson.annotations.SerializedName

enum class InvoiceType(type: String) {

    @SerializedName("BOLETA")
    BOLETA("BOLETA"),

    @SerializedName("FACTURA")
    FACTURA("FACTURA"),

    @SerializedName("NOTA DE VENTA")
    NOTA_DE_VENTA("NOTA DE VENTA")

}