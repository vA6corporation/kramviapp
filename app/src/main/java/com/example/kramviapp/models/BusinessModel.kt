package com.example.kramviapp.models

data class BusinessModel(
    val _id: String = "",
    val businessName: String = "",
    val ruc: String = "",
    val isDebtor: Boolean = false,
    val isDebtorCancel: Boolean = false,
    val certificateId: String? = null,
    val offices: List<OfficeModel> = listOf()
)