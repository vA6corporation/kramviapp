package com.example.kramviapp.enums

import com.google.gson.annotations.SerializedName

enum class PrintZoneType(type: String) {

    @SerializedName("COCINA")
    COCINA("COCINA"),

    @SerializedName("BARRA")
    BARRA("BARRA"),

    @SerializedName("HORNO")
    HORNO("HORNO"),

    @SerializedName("CAJA")
    CAJA("CAJA"),

}