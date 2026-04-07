package com.example.kramviapp.models

data class BusinessModel(
    val id: Int = 0,
    val name: String = "",
    val ruc: String = "",
    val isDebtor: Boolean = false,
    val isDebtorCancel: Boolean = false,
    val certificateId: Int? = null,
    val offices: List<OfficeModel> = listOf()
)