package com.example.kramviapp.enums

import com.google.gson.annotations.SerializedName

enum class IgvCodeType(type: String) {

    @SerializedName("10")
    GRAVADO("10"),

    @SerializedName("20")
    EXONERADO("20"),

    @SerializedName("30")
    INAFECTO("30"),

    @SerializedName("11")
    BONIFICACION("11")

}