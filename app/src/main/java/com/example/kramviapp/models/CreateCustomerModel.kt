package com.example.kramviapp.models

import com.example.kramviapp.enums.DocumentType

data class CreateCustomerModel(
    val documentType: DocumentType,
    val document: String,
    val name: String,
    val address: String?,
    val mobileNumber: String?,
    val email: String?,
)
