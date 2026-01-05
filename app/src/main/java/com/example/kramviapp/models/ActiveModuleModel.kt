package com.example.kramviapp.models

data class ActiveModuleModel(
    val openBox: Boolean = false,
    val posStandard: Boolean = false,
    val posFastFood: Boolean = false,
    val proformar: Boolean = false,
    val proformas: Boolean = false,
    val boards: Boolean = false,
    val boardsWaiter: Boolean = false,
    val products: Boolean = false,
    val inventories: Boolean = false,
    val invoices: Boolean = false,
    val biller: Boolean = false,
)