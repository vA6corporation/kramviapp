package com.example.kramviapp.enums

import com.google.gson.annotations.SerializedName

enum class PriceType(type: String) {

    @SerializedName("GLOBAL")
    GLOBAL("GLOBAL"),

    @SerializedName("OFICINA")
    OFICINA("OFICINA"),

    @SerializedName("LISTA")
    LISTA("LISTA"),

    @SerializedName("LISTAOFICINA")
    LISTAOFICINA("LISTAOFICINA"),

}