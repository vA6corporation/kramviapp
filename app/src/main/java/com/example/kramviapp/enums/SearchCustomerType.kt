package com.example.kramviapp.enums

import com.google.gson.annotations.SerializedName

enum class SearchCustomerType(type: String) {

    @SerializedName("RUC")
    RUC("RUC"),

    @SerializedName("DNI")
    DNI("DNI"),

    @SerializedName("CE")
    CE("CE"),

    @SerializedName("MOBILE")
    MOBILE("MOBILE"),

    @SerializedName("NAME")
    NAME("NAME"),

}